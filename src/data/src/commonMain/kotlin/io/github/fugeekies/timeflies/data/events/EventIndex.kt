package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import kotlinx.datetime.LocalDate

/**
 * In-memory indexes of the events for one region, built once: by day of the year (anniversaries)
 * and by type sorted by date (equivalences).
 */
internal class EventIndex(events: List<Event>, private val region: Region) {

    private val byMonthDay: Map<MonthDay, List<Event>> =
        events.groupBy { MonthDay.of(it.dateFor(region)) }

    private val byType: Map<EventType, List<Event>> =
        events.groupBy { it.type }.mapValues { (_, list) -> list.sortedBy { it.dateFor(region) } }

    fun onMonthDay(monthDay: MonthDay): List<Event> = byMonthDay[monthDay].orEmpty()

    fun ofType(type: EventType, from: LocalDate, to: LocalDate): List<Event> {
        val sorted = byType[type] ?: return emptyList()
        if (from > to) return emptyList()
        val start = lowerBound(sorted, from)
        val result = mutableListOf<Event>()
        for (i in start until sorted.size) {
            val event = sorted[i]
            if (event.dateFor(region) > to) break
            result += event
        }
        return result
    }

    /** First position whose date is on or after [date]. */
    private fun lowerBound(sorted: List<Event>, date: LocalDate): Int {
        var low = 0
        var high = sorted.size
        while (low < high) {
            val mid = (low + high) ushr 1
            if (sorted[mid].dateFor(region) < date) low = mid + 1 else high = mid
        }
        return low
    }
}
