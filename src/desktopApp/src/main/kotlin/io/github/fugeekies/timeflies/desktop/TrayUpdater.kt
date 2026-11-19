package io.github.fugeekies.timeflies.desktop

import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.SendMorningNotification
import io.github.fugeekies.timeflies.domain.usecase.ReleaseUpdates
import io.github.fugeekies.timeflies.domain.usecase.PersonalEvents
import io.github.fugeekies.timeflies.ui.text.AppLocale
import io.github.fugeekies.timeflies.ui.text.ShockMessages
import io.github.fugeekies.timeflies.ui.text.apply
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock as KotlinClock
import kotlin.time.Duration.Companion.minutes

/**
 * Keeps the tray icon up to date (docs/UISpec/WindowsTray.template.md): the tooltip when the day or the region
 * changes, the morning notification once its time has come, and the daily look for a new version.
 *
 * It checks every minute rather than sleeping until the chosen time: after a sleep or with the PC turned on late,
 * the notification goes out within a minute of the wake-up, once a day ([SendMorningNotification.ifDue]).
 */
class TrayUpdater(
    private val sendMorningNotification: SendMorningNotification,
    private val observeSettings: ObserveSettings,
    private val clock: Clock,
    private val releaseUpdates: ReleaseUpdates,
    private val personalEvents: PersonalEvents,
) {
    private val log = Logger.withTag("Tray")

    /** Day, region, event filter, language and personal events of the tooltip shown, to recompute it only when one of them changes. */
    private var tooltipFor: List<Any?>? = null

    /** Runs until cancelled, when the app quits. */
    suspend fun run(tray: WindowsTray) {
        merge(observeSettings().map { }, personalEvents.observe().map { }, clock.manualChanges(), everyMinute()).collect { check(tray) }
    }

    /** "Coup de vieux du jour" in the menu and the debug panel: today's notification at once, the morning one still due. */
    suspend fun sendNow(tray: WindowsTray) {
        log.d { "Coup de vieux du jour demandé" }
        guarded("La notification n'a pas pu être envoyée") {
            AppLocale.apply(observeSettings().first().language)
            notify(tray, sendMorningNotification.now())
        }
    }

    private suspend fun check(tray: WindowsTray) {
        guarded("L'icône n'a pas pu être mise à jour") {
            val settings = observeSettings().first()
            // The tray may run before any window: its texts follow the language chosen in Settings too (milestone 17).
            AppLocale.apply(settings.language)
            // The user's own events too (decision of 19/11/2026).
            val key = listOf(clock.today(), settings.region, settings.excludedEventGroups, settings.language, personalEvents.observe().first())
            if (key != tooltipFor) {
                tray.setTooltip(ShockMessages.tooltip(sendMorningNotification.now()))
                tooltipFor = key
            }
            sendMorningNotification.ifDue(now())?.let {
                log.i { "Notification du matin" }
                notify(tray, it)
            }
        }
        // A new version on GitHub, once a day at most: the app may stay in the tray for weeks.
        guarded("Les nouvelles versions n'ont pas pu être cherchées") { releaseUpdates.checkIfDue() }
    }

    private suspend fun notify(tray: WindowsTray, shock: DailyShock) {
        log.d { "Texte de la notification…" }
        val message = ShockMessages.notification(shock)
        // The title only: the text may give the user's age.
        log.d { "Notification envoyée : ${message.title}" }
        tray.notify(message.title, message.text)
    }

    private fun everyMinute() = flow {
        while (true) {
            emit(Unit)
            delay(1.minutes)
        }
    }

    /** The time of day on the system clock (a fake date of the debug panel changes the day only). */
    private fun now(): LocalTime = KotlinClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time

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
