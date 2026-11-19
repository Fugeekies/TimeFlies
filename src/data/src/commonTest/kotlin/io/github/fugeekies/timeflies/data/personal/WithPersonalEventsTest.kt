package io.github.fugeekies.timeflies.data.personal

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.port.PersonalEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class WithPersonalEventsTest {

    private val film = Event(
        id = "film", date = LocalDate(2001, 12, 5), regionalDates = emptyMap(), titles = mapOf(Event.FRENCH to "Un film"),
        kind = EventKind.FILM_RELEASE, group = EventGroup(EventType.BLOCKBUSTER, "us"), importance = 2, source = "https://example.org",
    )

    private val base = object : EventRepository {
        override suspend fun groups() = listOf(EventGroupCount(film.group, 1))
        override suspend fun onMonthDay(monthDay: MonthDay, region: Region) = if (monthDay == MonthDay(12, 5)) listOf(film) else emptyList()
        override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region) = if (type == film.type) listOf(film) else emptyList()
        override suspend fun search(query: String, limit: Int) = if (query.lowercase() in "un film") listOf(film).take(limit) else emptyList()
        override suspend fun count() = 1
    }

    private val wedding = PersonalEvent("personal-1", "Mariage au cinéma", LocalDate(2015, 12, 5))
    private val birth = PersonalEvent("personal-2", "Naissance", LocalDate(2018, 3, 2))

    private val personal = object : PersonalEventRepository {
        override val events = MutableStateFlow(listOf(wedding, birth))
        override suspend fun add(title: String, date: LocalDate) = Unit
        override suspend fun update(id: String, title: String, date: LocalDate) = Unit
        override suspend fun remove(id: String) = Unit
        override suspend fun addAll(events: List<Pair<String, LocalDate>>) = Unit
        override suspend fun encode() = ""
        override fun decode(text: String): PersonalEventRepository.Decoded? = null
    }

    private val events = WithPersonalEvents(base, personal)

    @Test
    fun theAnniversariesOfADayIncludeThePersonalEvents() = runTest {
        assertEquals(listOf("film", wedding.id), events.onMonthDay(MonthDay(12, 5), Region.FRANCE).map { it.id })
        assertEquals(listOf(birth.id), events.onMonthDay(MonthDay(3, 2), Region.FRANCE).map { it.id })
    }

    @Test
    fun thePersonalTypeGivesThePersonalEventsInTheRange() = runTest {
        assertEquals(listOf(wedding.id), events.ofType(EventType.PERSONAL, LocalDate(2010, 1, 1), LocalDate(2016, 1, 1), Region.FRANCE).map { it.id })
        assertEquals(listOf("film"), events.ofType(EventType.BLOCKBUSTER, LocalDate(1900, 1, 1), LocalDate(2026, 1, 1), Region.FRANCE).map { it.id })
    }

    @Test
    fun theSearchFindsThePersonalEventsFirstIgnoringAccents() = runTest {
        assertEquals(listOf(wedding.id), events.search("CINEMA", 10).map { it.id })
        // "i" is in "Mariage au cinéma", "Naissance" and "Un film": the user's own first, the limit kept.
        assertEquals(listOf(wedding.id, birth.id, "film"), events.search("i", 10).map { it.id })
        assertEquals(listOf(wedding.id), events.search("i", 1).map { it.id })
    }

    @Test
    fun theGroupsAndTheCountAreThoseOfTheBase() = runTest {
        assertEquals(1, events.count())
        assertEquals(listOf(film.group), events.groups().map { it.group })
    }
}
