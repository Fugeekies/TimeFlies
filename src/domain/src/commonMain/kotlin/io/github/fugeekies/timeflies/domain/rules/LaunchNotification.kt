package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import kotlinx.datetime.LocalDate

/**
 * When the Windows notification is sent (docs/UISpec/WindowsTray.template.md, decision of 20/11/2026): when TimeFlies
 * starts, with Windows or by hand, and only once a day. No chosen time and no check every minute any more: simpler,
 * and the app does nothing while it waits in the taskbar.
 */
object LaunchNotification {
    /** True when the notification of [today] is due at a start: turned on, not sent yet today. */
    fun isDue(settings: WindowsSettings, today: LocalDate): Boolean =
        settings.launchNotification && settings.lastNotification != today
}
