package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.port.EventRepository

/** Every group of the event base with its number of events: the tree of the event filter (decision of 16/11/2026). */
class GetEventGroups(private val events: EventRepository) {
    suspend operator fun invoke(): List<EventGroupCount> = events.groups()
}
