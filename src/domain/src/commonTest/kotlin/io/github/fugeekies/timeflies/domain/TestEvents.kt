package io.github.fugeekies.timeflies.domain

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Region
import kotlinx.datetime.LocalDate

/** Builds a made-up event for tests; never shipped. */
internal fun testEvent(
    id: String,
    date: String,
    importance: Int = 2,
    type: EventType = EventType.BLOCKBUSTER,
    regionalDates: Map<Region, LocalDate> = emptyMap(),
) = Event(
    id = id,
    date = LocalDate.parse(date),
    regionalDates = regionalDates,
    titles = mapOf(Event.FRENCH to "Titre $id"),
    kind = EventKind.FILM_RELEASE,
    type = type,
    importance = importance,
    source = "https://example.org/$id",
)
