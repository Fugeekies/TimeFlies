package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.rules.Anniversaries
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/** The "coup de vieux du jour" for today. Shared by the Today screen, the Android widget and the Windows tray. */
class GetDailyShock(
    private val events: EventRepository,
    private val clock: Clock,
) {
    /**
     * @param region region whose release dates are used (France until the Settings screen exists).
     * @param birth the user's birth date, or null when unknown (no "Tu avais N ans").
     */
    suspend operator fun invoke(region: Region = Region.FRANCE, birth: LocalDate? = null): DailyShock {
        val today = clock.today()
        val candidates = mutableMapOf<LocalDate, List<Event>>()
        for (offset in 0..Anniversaries.UPCOMING_DAYS) {
            val day = today.plus(offset, DateTimeUnit.DAY)
            candidates[day] = Anniversaries.celebratedOn(day).flatMap { events.onMonthDay(it, region) }
        }
        return Anniversaries.dailyShock(today, { candidates[it].orEmpty() }, region, birth)
    }
}
