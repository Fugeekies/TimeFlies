package io.github.fugeekies.timeflies.domain.port

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import kotlinx.datetime.LocalDate

/**
 * Read access to the event base. Implemented by the data layer (JSON files today). The groups the user unticked
 * ([io.github.fugeekies.timeflies.domain.model.Settings.excludedEventGroups]) are left out of every query, as if
 * they were not in the base (decision of 16/11/2026); only [groups] lists them.
 */
interface EventRepository {
    /** Every group of the base with its number of events, unticked ones included, sorted by path. */
    suspend fun groups(): List<EventGroupCount> = emptyList()

    /** Events whose date in [region] falls on [monthDay], any year. */
    suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event>

    /** Events of [type] whose date in [region] is between [from] and [to] inclusive, sorted by that date. */
    suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region): List<Event>

    /** Events whose title contains [query], ignoring case and accents; at most [limit]. */
    suspend fun search(query: String, limit: Int): List<Event>

    /** Number of valid events in the base. */
    suspend fun count(): Int
}
