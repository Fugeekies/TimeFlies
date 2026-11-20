package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.rules.Equivalences

/**
 * The events the user can choose on the "Quel âge ?" screen (docs/UISpec/EventAges.template.md, "Recherche").
 * Events less than a year old are left out, as everywhere (decision of 13/11/2026).
 */
class SearchEvents(
    private val events: EventRepository,
    private val clock: Clock,
) {
    /**
     * Events whose title contains [query], ignoring case and accents, the most recent first; empty below 2 characters.
     */
    suspend fun search(query: String, region: Region = Region.FRANCE): List<Event> {
        if (query.trim().length < MIN_QUERY_LENGTH) return emptyList()
        val today = clock.today()
        return events.search(query, MAX_RESULTS)
            .filter { Equivalences.isOldEnough(it.dateFor(region), today) }
            .sortedWith(mostRecentFirst(region))
    }

    /**
     * Every event by family, the most recent first within each: the user's own events first (decision of 19/11/2026),
     * then the themes in their order.
     */
    suspend fun byFamily(region: Region = Region.FRANCE): Map<Family, List<Event>> {
        val latest = Equivalences.latestOldEnough(clock.today())
        return (listOf(Family.PERSONAL) + Family.THEMES).associateWith { family ->
            EventType.entries.filter { it.family == family }
                .flatMap { events.ofType(it, Equivalences.EARLIEST, latest, region) }
                .sortedWith(mostRecentFirst(region))
        }.filterValues { it.isNotEmpty() }
    }

    private fun mostRecentFirst(region: Region): Comparator<Event> =
        compareByDescending<Event> { it.dateFor(region) }.thenBy { it.id }

    companion object {
        const val MIN_QUERY_LENGTH = 2
        const val MAX_RESULTS = 100
    }
}
