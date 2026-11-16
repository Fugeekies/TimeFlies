package io.github.fugeekies.timeflies.domain.model

import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth

/**
 * The user's settings, kept on the device (docs/UISpec/Settings.template.md). Defaults are those of a first launch.
 *
 * @property birth the user's birth month and year, never the day (decision of 13/11/2026); null when not given.
 * @property otherAge age of the other person the user compares themselves with, from 1 to 100, younger or older
 *   (decision of 15/11/2026). Set on the Equivalences screen only.
 * @property region region whose event dates are used (France only for now, decision of 15/11/2026).
 * @property onboardingDone the first launch screen was completed or skipped ("Plus tard"); it does not come back.
 * @property equivalencesReversed reading direction of the Equivalences screen: false puts the other person on the
 *   left, true puts the user on the left.
 * @property equivalenceFamilies themes ticked on the Equivalences screen (all by default).
 * @property windows the Windows panel: notification, startup and tray.
 */
data class Settings(
    val birth: YearMonth? = null,
    val otherAge: Int = Generations.DEFAULT_OTHER_AGE,
    val region: Region = Region.FRANCE,
    val onboardingDone: Boolean = false,
    val equivalencesReversed: Boolean = false,
    val equivalenceFamilies: Set<Family> = Family.entries.toSet(),
    val windows: WindowsSettings = WindowsSettings(),
)

/**
 * Settings of the Windows tray icon and morning notification (docs/UISpec/WindowsTray.template.md), defaults
 * decided on 13/11/2026. Ignored on Android.
 *
 * @property launchAtStartup TimeFlies starts with Windows, window hidden, in the notification area only.
 * @property morningNotification the "coup de vieux" is sent as a notification each morning at [notificationTime].
 * @property notificationTime a quarter of an hour (9:00 by default).
 * @property keepInTray closing the window hides it instead of quitting the app.
 * @property trayHintShown the notification explaining that TimeFlies stays in the taskbar was already shown once.
 * @property lastNotification the day the morning notification was last sent: at most once a day, even after a sleep.
 */
data class WindowsSettings(
    val launchAtStartup: Boolean = true,
    val morningNotification: Boolean = true,
    val notificationTime: LocalTime = DEFAULT_NOTIFICATION_TIME,
    val keepInTray: Boolean = true,
    val trayHintShown: Boolean = false,
    val lastNotification: LocalDate? = null,
) {
    companion object {
        val DEFAULT_NOTIFICATION_TIME = LocalTime(9, 0)
    }
}
