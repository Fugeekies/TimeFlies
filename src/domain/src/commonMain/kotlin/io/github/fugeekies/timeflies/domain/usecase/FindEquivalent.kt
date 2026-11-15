package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.EquivalenceSide
import io.github.fugeekies.timeflies.domain.model.EquivalentCandidate
import io.github.fugeekies.timeflies.domain.model.EquivalentSearch
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.rules.Ages
import io.github.fugeekies.timeflies.domain.rules.Equivalences
import kotlinx.datetime.LocalDate
import kotlinx.datetime.periodUntil

/** The equivalent of one chosen event, for the Mon équivalent screen (docs/UISpec/MyEquivalent.template.md). */
class FindEquivalent(
    private val events: EventRepository,
    private val clock: Clock,
) {
    /**
     * The equivalent of [event], taken in the life of the person born on [fromBirth], in the life of the person born
     * on [toBirth]. Either person can be the older one.
     */
    suspend operator fun invoke(event: Event, fromBirth: LocalDate, toBirth: LocalDate, region: Region = Region.FRANCE): EquivalentSearch {
        val today = clock.today()
        val date = event.dateFor(region)
        val target = Equivalences.targetDate(date, fromBirth, toBirth)
        val sameType = events.ofType(event.type, EARLIEST, today, region)
            .filter { it.id != event.id && Equivalences.isOldEnough(it.dateFor(region), today) }
            .sortedWith(Equivalences.ranking(target, region))
        val window = Equivalences.window(target)
        val inside = if (target > today) emptyList() else sameType.filter { it.dateFor(region) in window }
        fun candidate(match: Event): EquivalentCandidate {
            val matchDate = match.dateFor(region)
            return EquivalentCandidate(EquivalenceSide(match, matchDate, Ages.userAge(toBirth, matchDate)), target.periodUntil(matchDate))
        }
        return EquivalentSearch(
            chosen = EquivalenceSide(event, date, Ages.userAge(fromBirth, date)),
            target = target,
            best = inside.firstOrNull()?.let(::candidate),
            others = inside.drop(1).take(MAX_LISTED).map(::candidate),
            outside = if (inside.isEmpty()) sameType.filter { it.dateFor(region) !in window }.take(MAX_LISTED).map(::candidate) else emptyList(),
        )
    }

    private companion object {
        val EARLIEST = LocalDate(1, 1, 1)

        /** At most 3 other candidates, and 3 events outside the tolerance (docs/UISpec/MyEquivalent.template.md). */
        const val MAX_LISTED = 3
    }
}
