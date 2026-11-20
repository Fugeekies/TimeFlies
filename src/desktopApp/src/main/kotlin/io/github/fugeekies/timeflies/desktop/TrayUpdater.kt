package io.github.fugeekies.timeflies.desktop

import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.PersonalEvents
import io.github.fugeekies.timeflies.domain.usecase.ReleaseUpdates
import io.github.fugeekies.timeflies.domain.usecase.SendLaunchNotification
import io.github.fugeekies.timeflies.ui.dayChanges
import io.github.fugeekies.timeflies.ui.text.AppLocale
import io.github.fugeekies.timeflies.ui.text.ShockMessages
import io.github.fugeekies.timeflies.ui.text.apply
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * Keeps the tray icon up to date (docs/UISpec/WindowsTray.template.md): the notification once when TimeFlies starts,
 * the tooltip when the day, the settings or the user's own events change, and the daily look for a new version.
 *
 * Nothing runs on a timer while TimeFlies waits in the taskbar (decision of 20/11/2026): it wakes up at midnight, when
 * something changes, and that is all.
 */
class TrayUpdater(
    private val sendLaunchNotification: SendLaunchNotification,
    private val observeSettings: ObserveSettings,
    private val clock: Clock,
    private val releaseUpdates: ReleaseUpdates,
    private val personalEvents: PersonalEvents,
) {
    private val log = Logger.withTag("Tray")

    /** Day, region, event filter, language and personal events of the tooltip shown, to recompute it only when one of them changes. */
    private var tooltipFor: List<Any?>? = null

    /** Sends the notification of the start if due, then runs until cancelled, when the app quits. */
    suspend fun run(tray: WindowsTray) {
        guarded("La notification n'a pas pu être envoyée") {
            AppLocale.apply(observeSettings().first().language)
            sendLaunchNotification.ifDue()?.let {
                log.i { "Notification du lancement" }
                notify(tray, it)
            }
        }
        // The tooltip follows the settings and the user's own events; a new day (midnight, or the fake date of the
        // debug panel) changes it too and looks for a new version: the app may stay in the tray for weeks.
        merge(observeSettings().map { false }, personalEvents.observe().map { false }, clock.dayChanges().map { true })
            .collect { newDay -> update(tray, newDay) }
    }

    /** "Coup de vieux du jour" in the menu and the debug panel: today's notification at once. */
    suspend fun sendNow(tray: WindowsTray) {
        log.d { "Coup de vieux du jour demandé" }
        guarded("La notification n'a pas pu être envoyée") {
            AppLocale.apply(observeSettings().first().language)
            notify(tray, sendLaunchNotification.now())
        }
    }

    private suspend fun update(tray: WindowsTray, newDay: Boolean) {
        guarded("L'icône n'a pas pu être mise à jour") {
            val settings = observeSettings().first()
            // The tray may run before any window: its texts follow the language chosen in Settings too (milestone 17).
            AppLocale.apply(settings.language)
            // The user's own events too (decision of 19/11/2026).
            val key = listOf(clock.today(), settings.region, settings.excludedEventGroups, settings.language, personalEvents.observe().first())
            if (key != tooltipFor) {
                tray.setTooltip(ShockMessages.tooltip(sendLaunchNotification.now()))
                tooltipFor = key
            }
        }
        if (newDay) guarded("Les nouvelles versions n'ont pas pu être cherchées") { releaseUpdates.checkIfDue() }
    }

    private suspend fun notify(tray: WindowsTray, shock: DailyShock) {
        val message = ShockMessages.notification(shock)
        // The title only: the text may give the user's age.
        log.d { "Notification envoyée : ${message.title}" }
        tray.notify(message.title, message.text)
    }

    private inline fun guarded(message: String, block: () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.e(e) { message }
        }
    }
}
