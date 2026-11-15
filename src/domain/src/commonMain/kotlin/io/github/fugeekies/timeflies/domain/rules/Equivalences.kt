package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Region
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.math.abs

/** Pairing rules of the equivalences (docs/EVENTS_DATA.md, "Règles d'appariement des équivalences"). */
object Equivalences {
    /** A candidate must be less than this many years from the target date (decision of 13/11/2026). */
    const val TOLERANCE_YEARS = 2

    /**
     * The target date for an event of [eventDate] in the life of the person born on [fromBirth]: the day the person
     * born on [toBirth] had the same age, `eventDate + (toBirth − fromBirth)`, to the day.
     */
    fun targetDate(eventDate: LocalDate, fromBirth: LocalDate, toBirth: LocalDate): LocalDate =
        eventDate.plus(fromBirth.daysUntil(toBirth), DateTimeUnit.DAY)

    /** An event counts from 1 year old (decision of 13/11/2026): younger ones are neither paired nor shown. */
    fun isOldEnough(date: LocalDate, today: LocalDate): Boolean = date <= today.minus(1, DateTimeUnit.YEAR)

    /** First and last dates a candidate may have around [target]. */
    fun window(target: LocalDate): ClosedRange<LocalDate> =
        target.minus(TOLERANCE_YEARS, DateTimeUnit.YEAR)..target.plus(TOLERANCE_YEARS, DateTimeUnit.YEAR)

    /**
     * The equivalent of [event] among [candidates] (same type, any order) for [target]: within the tolerance, the
     * closest to the target, then the highest importance, never the event itself nor an event less than a year old.
     * Null when the target is in the future or when no candidate fits. Ties left after that go to the earliest date,
     * then the smallest id, so the result never depends on the order of [candidates].
     */
    fun bestMatch(event: Event, target: LocalDate, candidates: List<Event>, region: Region, today: LocalDate): Event? {
        if (target > today) return null
        val window = window(target)
        return candidates
            .asSequence()
            .filter { it.id != event.id && it.type == event.type }
            .filter { it.dateFor(region) in window && isOldEnough(it.dateFor(region), today) }
            .minWithOrNull(ranking(target, region))
    }

    /** Order of the candidates for [target]: the closest first, then the highest importance, the earliest date, the smallest id. */
    fun ranking(target: LocalDate, region: Region): Comparator<Event> =
        compareBy<Event> { abs(it.dateFor(region).daysUntil(target)) }
            .thenByDescending { it.importance }
            .thenBy { it.dateFor(region) }
            .thenBy { it.id }
}
