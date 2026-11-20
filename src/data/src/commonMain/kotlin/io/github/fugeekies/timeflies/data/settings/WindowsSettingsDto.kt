package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * The Windows panel as stored in `settings.json` (version 3, milestone 10): dates as readable text ("2026-11-16"). A
 * value edited by hand that cannot be read takes its default.
 *
 * Version 9 (decision of 20/11/2026) dropped `notificationTime`: the notification goes out when TimeFlies starts, not at
 * a chosen time; an older file still reads, the field being ignored.
 *
 * @property morningNotification the notification when TimeFlies starts ([WindowsSettings.launchNotification]); the
 *   name of the field is kept so the choice of an older file stays.
 */
@Serializable
internal data class WindowsSettingsDto(
    val launchAtStartup: Boolean = true,
    val morningNotification: Boolean = true,
    val keepInTray: Boolean = true,
    val trayHintShown: Boolean = false,
    val lastNotificationDate: String? = null,
) {
    fun toDomain(): WindowsSettings = WindowsSettings(
        launchAtStartup = launchAtStartup,
        launchNotification = morningNotification,
        keepInTray = keepInTray,
        trayHintShown = trayHintShown,
        lastNotification = lastNotificationDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    )

    companion object {
        fun of(settings: WindowsSettings) = WindowsSettingsDto(
            launchAtStartup = settings.launchAtStartup,
            morningNotification = settings.launchNotification,
            keepInTray = settings.keepInTray,
            trayHintShown = settings.trayHintShown,
            lastNotificationDate = settings.lastNotification?.toString(),
        )
    }
}
