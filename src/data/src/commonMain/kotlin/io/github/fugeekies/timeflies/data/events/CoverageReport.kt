package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region

/**
 * How well the base covers the year and the types (docs/EVENTS_DATA.md, "Volume visé").
 * Informative only: it never fails a build.
 */
data class CoverageReport(
    val eventCount: Int,
    val eventsByFamily: Map<Family, Int>,
    val daysWithoutAnniversary: Int,
    val typesBelowMinimum: Map<EventType, Int>,
) {
    override fun toString(): String = buildString {
        appendLine("Événements : $eventCount")
        Family.entries.forEach { appendLine("  ${it.fileName} : ${eventsByFamily[it] ?: 0}") }
        appendLine("Jours de l'année sans anniversaire : $daysWithoutAnniversary / ${ALL_DAYS.size}")
        appendLine("Types avec moins de $MIN_EVENTS_PER_TYPE événements : ${typesBelowMinimum.size}")
        typesBelowMinimum.forEach { (type, count) -> appendLine("  ${type.id} : $count") }
    }

    companion object {
        const val MIN_EVENTS_PER_TYPE = 4

        private val ALL_DAYS: List<MonthDay> =
            (1..12).flatMap { month -> (1..31).mapNotNull { day -> runCatching { MonthDay(month, day) }.getOrNull() } }

        fun of(events: List<Event>, region: Region): CoverageReport {
            val coveredDays = events.mapTo(HashSet()) { MonthDay.of(it.dateFor(region)) }
            val countByType = events.groupingBy { it.type }.eachCount()
            return CoverageReport(
                eventCount = events.size,
                eventsByFamily = events.groupingBy { it.family }.eachCount(),
                daysWithoutAnniversary = ALL_DAYS.count { it !in coveredDays },
                typesBelowMinimum = EventType.entries
                    .associateWith { countByType[it] ?: 0 }
                    .filterValues { it < MIN_EVENTS_PER_TYPE },
            )
        }
    }
}
