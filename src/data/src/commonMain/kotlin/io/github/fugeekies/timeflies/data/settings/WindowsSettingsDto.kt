package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import io.github.fugeekies.timeflies.domain.rules.MorningNotification
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

/**
 * The Windows panel as stored in `settings.json` (version 3, milestone 10): times and dates as readable text
 * ("09:00", "2026-11-16"). A value edited by hand that cannot be read takes its default.
 */
@Serializable
internal data class WindowsSettingsDto(
    val launchAtStartup: Boolean = true,
    val morningNotification: Boolean = true,
    val notificationTime: String = format(WindowsSettings.DEFAULT_NOTIFICATION_TIME),
    val keepInTray: Boolean = true,
    val trayHintShown: Boolean = false,
    val lastNotificationDate: String? = null,
) {
    fun toDomain(): WindowsSettings = WindowsSettings(
        launchAtStartup = launchAtStartup,
        morningNotification = morningNotification,
        notificationTime = runCatching { LocalTime.parse(notificationTime) }.getOrNull()
            ?.takeIf(MorningNotification::isQuarterHour)
            ?: WindowsSettings.DEFAULT_NOTIFICATION_TIME,
        keepInTray = keepInTray,
        trayHintShown = trayHintShown,
        lastNotification = lastNotificationDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    )

    companion object {
        fun of(settings: WindowsSettings) = WindowsSettingsDto(
            launchAtStartup = settings.launchAtStartup,
            morningNotification = settings.morningNotification,
            notificationTime = format(settings.notificationTime),
            keepInTray = settings.keepInTray,
            trayHintShown = settings.trayHintShown,
            lastNotificationDate = settings.lastNotification?.toString(),
        )

        /** "09:00": hours and minutes only. */
        private fun format(time: LocalTime): String =
            "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
    }
}
