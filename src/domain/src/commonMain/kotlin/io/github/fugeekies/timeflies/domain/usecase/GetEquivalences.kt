package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Equivalence
import io.github.fugeekies.timeflies.domain.model.EquivalenceSide
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.rules.Ages
import io.github.fugeekies.timeflies.domain.rules.Equivalences
import kotlinx.datetime.LocalDate

/** The pairs of the Equivalences screen (docs/UISpec/Equivalences.template.md). */
class GetEquivalences(
    private val events: EventRepository,
    private val clock: Clock,
) {
    /**
     * Every event of the ticked [families] that has an equivalent, as seen from the person born on [leftBirth]:
     * each pair puts their event on the left and the equivalent in the life of the person born on [rightBirth] on
     * the right. Sorted from the most recent left event to the oldest. Either person can be the older one.
     */
    suspend operator fun invoke(
        leftBirth: LocalDate,
        rightBirth: LocalDate,
        families: Set<Family>,
        region: Region = Region.FRANCE,
    ): List<Equivalence> {
        val today = clock.today()
        val pairs = mutableListOf<Equivalence>()
        for (type in EventType.entries.filter { it.family in families }) {
            // Sorted by date in the region, events younger than a year excluded.
            val ofType = events.ofType(type, EARLIEST, today, region).filter { Equivalences.isOldEnough(it.dateFor(region), today) }
            for (event in ofType) {
                val date = event.dateFor(region)
                val target = Equivalences.targetDate(date, leftBirth, rightBirth)
                val match = Equivalences.bestMatch(event, target, around(ofType, target, region), region, today) ?: continue
                val matchDate = match.dateFor(region)
                pairs += Equivalence(
                    type = type,
                    left = EquivalenceSide(event, date, Ages.userAge(leftBirth, date)),
                    right = EquivalenceSide(match, matchDate, Ages.userAge(rightBirth, matchDate)),
                )
            }
        }
        return pairs.sortedWith(compareByDescending<Equivalence> { it.left.date }.thenBy { it.left.event.id })
    }

    /** The events of [sorted] within the tolerance of [target], found by binary search. */
    private fun around(sorted: List<Event>, target: LocalDate, region: Region): List<Event> {
        val window = Equivalences.window(target)
        val start = lowerBound(sorted, window.start, region)
        var end = start
        while (end < sorted.size && sorted[end].dateFor(region) <= window.endInclusive) end++
        return sorted.subList(start, end)
    }

    private fun lowerBound(sorted: List<Event>, date: LocalDate, region: Region): Int {
        var low = 0
        var high = sorted.size
        while (low < high) {
            val mid = (low + high) ushr 1
            if (sorted[mid].dateFor(region) < date) low = mid + 1 else high = mid
        }
        return low
    }

    private companion object {
        val EARLIEST = LocalDate(1, 1, 1)
    }
}
