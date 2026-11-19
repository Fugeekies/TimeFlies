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

/** The cards of the Equivalences screen (docs/UISpec/Equivalences.template.md). */
class GetEquivalences(
    private val events: EventRepository,
    private val clock: Clock,
) {
    /**
     * Every event of the ticked [families] in the life of the user born on [userBirth] that has an equivalent for
     * at least one of the people born on [otherBirths] (up to three, side by side, decision of 17/11/2026). Sorted
     * from the user's most recent event to the oldest. Each other person can be younger or older than the user.
     */
    suspend operator fun invoke(
        userBirth: LocalDate,
        otherBirths: List<LocalDate>,
        families: Set<Family>,
        region: Region = Region.FRANCE,
    ): List<Equivalence> {
        if (otherBirths.isEmpty()) return emptyList()
        val today = clock.today()
        val cards = mutableListOf<Equivalence>()
        // Personal events have no match: never a card (decision of 19/11/2026).
        for (type in EventType.entries.filter { it.family in families && it.family != Family.PERSONAL }) {
            // Sorted by date in the region, events younger than a year excluded.
            val ofType = events.ofType(type, EARLIEST, today, region).filter { Equivalences.isOldEnough(it.dateFor(region), today) }
            for (event in ofType) {
                val date = event.dateFor(region)
                val others = otherBirths.map { otherBirth ->
                    val target = Equivalences.targetDate(date, userBirth, otherBirth)
                    Equivalences.bestMatch(event, target, around(ofType, target, region), region, today)?.let { match ->
                        val matchDate = match.dateFor(region)
                        EquivalenceSide(match, matchDate, Ages.userAge(otherBirth, matchDate))
                    }
                }
                if (others.all { it == null }) continue
                cards += Equivalence(type, EquivalenceSide(event, date, Ages.userAge(userBirth, date)), others)
            }
        }
        return cards.sortedWith(compareByDescending<Equivalence> { it.user.date }.thenBy { it.user.event.id })
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
