package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.rules.LaunchNotification
import kotlinx.coroutines.flow.first

/**
 * The Windows notification sent when TimeFlies starts (docs/UISpec/WindowsTray.template.md, decision of 20/11/2026);
 * the content is the same "coup de vieux" as the Today screen.
 */
class SendLaunchNotification(
    private val getDailyShock: GetDailyShock,
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    /**
     * The daily shock to send at a start when the notification is due ([LaunchNotification.isDue]), marking today as
     * sent so a second start the same day sends nothing; null when it is not due.
     */
    suspend fun ifDue(): DailyShock? {
        val today = clock.today()
        val current = settings.settings.first()
        if (!LaunchNotification.isDue(current.windows, today)) return null
        val shock = getDailyShock(current.region, current.birth?.let(Generations::userBirth))
        settings.update { it.copy(windows = it.windows.copy(lastNotification = today)) }
        return shock
    }

    /** Today's shock, at once and without marking the day ("Coup de vieux du jour" in the tray menu, debug panel). */
    suspend fun now(): DailyShock {
        val current = settings.settings.first()
        return getDailyShock(current.region, current.birth?.let(Generations::userBirth))
    }
}
