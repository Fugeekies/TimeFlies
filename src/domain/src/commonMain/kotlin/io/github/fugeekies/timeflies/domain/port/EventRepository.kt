package io.github.fugeekies.timeflies.domain.port

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import kotlinx.datetime.LocalDate

/** Read access to the event base. Implemented by the data layer (JSON files today). */
interface EventRepository {
    /** Events whose date in [region] falls on [monthDay], any year. */
    suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event>

    /** Events of [type] whose date in [region] is between [from] and [to] inclusive, sorted by that date. */
    suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region): List<Event>

    /** Events whose title contains [query], ignoring case and accents; at most [limit]. */
    suspend fun search(query: String, limit: Int): List<Event>

    /** Number of valid events in the base. */
    suspend fun count(): Int
}
