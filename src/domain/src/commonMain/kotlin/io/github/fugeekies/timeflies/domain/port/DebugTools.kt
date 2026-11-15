package io.github.fugeekies.timeflies.domain.port

import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Settings
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

/**
 * What the debug panel can do (docs/DEVELOPMENT.md, "Panneau de débogage"). Built by the apps in debug builds
 * only, never in a release: the panel does not exist when there is no instance.
 */
interface DebugTools {
    /** The fake date of the day, or null when the system clock is used. */
    val fakeDate: StateFlow<LocalDate?>

    /** Replaces the date of the day (null goes back to the system clock); screens recompute at once. */
    fun setFakeDate(date: LocalDate?)

    /** Writes any settings, without the checks of the screens. */
    suspend fun forceSettings(transform: (Settings) -> Settings)

    /** Statistics of the event base, in the current region. */
    suspend fun baseStats(): BaseStats
}

/**
 * Statistics of the event base shown in the debug panel.
 *
 * @property emptyDays days of the year on which no event has its anniversary.
 * @property typesBelowMinimum types with fewer than [minEventsPerType] events, with their count.
 * @property rejections entries left out by the validation, one line each.
 */
data class BaseStats(
    val eventCount: Int,
    val eventsByFamily: Map<Family, Int>,
    val emptyDays: List<MonthDay>,
    val minEventsPerType: Int,
    val typesBelowMinimum: Map<EventType, Int>,
    val rejections: List<String>,
)
