package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.port.EventRepository

/** Number of events in the base, shown in Settings › À propos. */
class CountEvents(private val events: EventRepository) {
    suspend operator fun invoke(): Int = events.count()
}
