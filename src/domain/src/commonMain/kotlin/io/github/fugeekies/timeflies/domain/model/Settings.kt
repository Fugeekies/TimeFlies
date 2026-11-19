package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth

/**
 * The user's settings, kept on the device (docs/UISpec/Settings.template.md). Defaults are those of a first launch.
 * The other ages of "Quel âge ?" and Equivalences are not settings: they are forgotten when the app closes
 * (decision of 17/11/2026).
 *
 * @property birth the user's birth month and year, never the day (decision of 13/11/2026); null when not given.
 * @property region region whose event dates are used (France only for now, decision of 15/11/2026).
 * @property onboardingDone the first launch screen was completed or skipped ("Plus tard"); it does not come back.
 * @property equivalenceFamilies themes ticked on the Equivalences and "Quel âge ?" screens, one choice for both (all by default, decision of 18/11/2026).
 * @property windows the Windows panel: notification, startup and tray.
 * @property excludedEventGroups paths of the event groups the user unticked ("music/rap-album-hit/us", see
 *   [EventGroup.path]): left out everywhere, as if not in the base (decision of 16/11/2026). Empty: everything.
 * @property displayStyle the display style of the app and the widget, Polaroïd by default (decision of 17/11/2026).
 * @property language the language chosen in Settings › Affichage; null: the system language when available, else
 *   French (milestone 17).
 */
data class Settings(
    val birth: YearMonth? = null,
    val region: Region = Region.FRANCE,
    val onboardingDone: Boolean = false,
    val equivalenceFamilies: Set<Family> = Family.THEMES.toSet(),
    val windows: WindowsSettings = WindowsSettings(),
    val releaseCheck: ReleaseCheck = ReleaseCheck(),
    val excludedEventGroups: Set<String> = emptySet(),
    val displayStyle: DisplayStyle = DisplayStyle.POLAROID,
    val language: AppLanguage? = null,
)

/**
 * Looking for a new version at start (decision of 16/11/2026): one request to GitHub a day at most, without any
 * personal data, and a banner on Today when a newer version exists.
 *
 * @property enabled the user can turn it off in Settings › À propos.
 * @property lastCheck the day GitHub was last asked.
 * @property latest the latest version GitHub gave, kept so the banner shows without asking again.
 * @property dismissedVersion "Plus tard" on the banner of this version: it comes back with the next one only.
 */
data class ReleaseCheck(
    val enabled: Boolean = true,
    val lastCheck: LocalDate? = null,
    val latest: AppRelease? = null,
    val dismissedVersion: String? = null,
)

/** A published version: "1.2.0" and the page of its release. */
data class AppRelease(val version: String, val url: String)

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
