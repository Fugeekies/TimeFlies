package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.rules.MorningNotification
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalTime

/**
 * The Windows morning notification (docs/UISpec/WindowsTray.template.md). The tray checks it regularly; the
 * content is the same "coup de vieux" as the Today screen.
 */
class SendMorningNotification(
    private val getDailyShock: GetDailyShock,
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    /**
     * The daily shock to send when the notification is due at [now] ([MorningNotification.isDue]), marking today as
     * sent so it goes out only once; null when it is not due.
     */
    suspend fun ifDue(now: LocalTime): DailyShock? {
        val today = clock.today()
        val current = settings.settings.first()
        if (!MorningNotification.isDue(current.windows, today, now)) return null
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
