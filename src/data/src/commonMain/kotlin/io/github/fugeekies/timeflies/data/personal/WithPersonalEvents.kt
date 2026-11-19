package io.github.fugeekies.timeflies.data.personal

import io.github.fugeekies.timeflies.data.events.fold
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.port.PersonalEventRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

/**
 * The event base with the user's own events added (decision of 19/11/2026): they join the anniversaries of a day
 * (Today, the widget, the notification), the search and the "personal" type of "Quel âge ?". They are not part of the
 * base: neither in its groups (the filter of Settings) nor in its count (Settings › À propos), and their date is the
 * same in every region.
 */
class WithPersonalEvents(
    private val base: EventRepository,
    private val personal: PersonalEventRepository,
) : EventRepository {

    override suspend fun groups(): List<EventGroupCount> = base.groups()

    override suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event> {
        val own = personalEvents().filter { MonthDay.of(it.date) == monthDay }
        return base.onMonthDay(monthDay, region) + own
    }

    override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region): List<Event> =
        if (type != EventType.PERSONAL) base.ofType(type, from, to, region) else personalEvents().filter { it.date in from..to }

    override suspend fun search(query: String, limit: Int): List<Event> {
        val needle = fold(query.trim())
        if (needle.isEmpty() || limit <= 0) return emptyList()
        // The user's own first: they are the ones looked for when they match.
        val own = personalEvents().filter { fold(it.title(Event.FRENCH)).contains(needle) }.take(limit)
        return own + base.search(query, limit - own.size)
    }

    override suspend fun count(): Int = base.count()

    /** Sorted by date, as the stored file. */
    private suspend fun personalEvents(): List<Event> = personal.events.first().map { it.toEvent() }
}
