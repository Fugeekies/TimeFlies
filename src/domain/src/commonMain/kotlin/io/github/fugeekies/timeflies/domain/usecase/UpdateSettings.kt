package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.rules.MorningNotification
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth

/** Changes the settings; each change is saved at once (docs/UISpec/Settings.template.md). */
class UpdateSettings(
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    /**
     * Saves the user's birth month and year. The other person's age is kept even if the gap becomes small:
     * the Equivalences screen warns (decision of 15/11/2026).
     *
     * @throws IllegalArgumentException if the year is outside 1920 to the current year minus 5.
     */
    suspend fun setBirth(birth: YearMonth) {
        require(birth.year in Generations.birthYears(clock.today())) { "Birth year out of range: ${birth.year}" }
        settings.update { it.copy(birth = birth) }
    }

    /** Removes the birth month and year (reduced mode). */
    suspend fun clearBirth() {
        settings.update { it.copy(birth = null) }
    }

    /** Saves the themes ticked on the Equivalences screen. */
    suspend fun setEquivalenceFamilies(families: Set<Family>) {
        settings.update { it.copy(equivalenceFamilies = families) }
    }

    /** Windows: start TimeFlies with Windows, window hidden (the app writes or removes its startup entry). */
    suspend fun setLaunchAtStartup(enabled: Boolean) = updateWindows { it.copy(launchAtStartup = enabled) }

    /** Windows: send the "coup de vieux" as a notification each morning. */
    suspend fun setMorningNotification(enabled: Boolean) = updateWindows { it.copy(morningNotification = enabled) }

    /**
     * Windows: time of the morning notification.
     *
     * @throws IllegalArgumentException if [time] is not a quarter of an hour.
     */
    suspend fun setNotificationTime(time: LocalTime) {
        require(MorningNotification.isQuarterHour(time)) { "Not a quarter of an hour: $time" }
        updateWindows { it.copy(notificationTime = time) }
    }

    /** Windows: closing the window hides it in the notification area instead of quitting. */
    suspend fun setKeepInTray(enabled: Boolean) = updateWindows { it.copy(keepInTray = enabled) }

    /** Windows: the notification explaining that TimeFlies stays in the taskbar was shown; it does not come back. */
    suspend fun markTrayHintShown() = updateWindows { it.copy(trayHintShown = true) }

    /** Settings › Événements: the groups left out everywhere (paths, see [io.github.fugeekies.timeflies.domain.model.EventGroup.path]). */
    suspend fun setExcludedEventGroups(paths: Set<String>) {
        settings.update { it.copy(excludedEventGroups = paths) }
    }

    /** Settings › À propos: look for new versions at start, or never (decision of 16/11/2026). */
    suspend fun setReleaseCheck(enabled: Boolean) {
        settings.update { it.copy(releaseCheck = it.releaseCheck.copy(enabled = enabled)) }
    }

    /** Settings › Affichage: the style of the whole app and of the widget (decision of 17/11/2026). */
    suspend fun setDisplayStyle(style: DisplayStyle) {
        settings.update { it.copy(displayStyle = style) }
    }

    /** Settings › Affichage › Langue: the language of the interface, applied at once (milestone 17). */
    suspend fun setLanguage(language: AppLanguage) {
        settings.update { it.copy(language = language) }
    }

    private suspend fun updateWindows(transform: (WindowsSettings) -> WindowsSettings) {
        settings.update { it.copy(windows = transform(it.windows)) }
    }
}
