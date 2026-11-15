package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.testEvent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventAgesTest {

    /** Same contract as the data layer: by type sorted by regional date, search in titles ignoring case. */
    private class FakeRepository(private val events: List<Event>) : EventRepository {
        override suspend fun onMonthDay(monthDay: MonthDay, region: Region) = emptyList<Event>()
        override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region) =
            events.filter { it.type == type && it.dateFor(region) in from..to }.sortedBy { it.dateFor(region) }
        override suspend fun search(query: String, limit: Int) =
            events.filter { event -> event.titles.values.any { it.contains(query.trim(), ignoreCase = true) } }.take(limit)
        override suspend fun count() = events.size
    }

    private val today = LocalDate(2026, 11, 15)
    private val user = LocalDate(1991, 5, 1)
    private val child = LocalDate(2011, 11, 15)

    @Test
    fun givesBothAgesNegativeBeforeABirth() {
        val ages = GetEventAges()(testEvent("titanic", "1998-01-07"), userBirth = user, otherBirth = child)
        assertEquals(6, ages.userAge)
        // 13 years and 10 months before the birth: "−14 ans".
        assertEquals(-14, ages.otherAge)
        assertEquals(LocalDate(1998, 1, 7), ages.date)
    }

    @Test
    fun countsAFewMonthsBeforeTheBirthAsMinusOne() {
        val ages = GetEventAges()(testEvent("spring", "2011-06-01"), userBirth = user, otherBirth = child)
        assertEquals(-1, ages.otherAge)
        assertEquals(20, ages.userAge)
    }

    @Test
    fun usesTheRegionalDate() {
        val event = testEvent("regional", "2000-01-01", regionalDates = mapOf(Region.FRANCE to LocalDate(2012, 1, 1)))
        val ages = GetEventAges()(event, userBirth = user, otherBirth = child, region = Region.FRANCE)
        assertEquals(0, ages.otherAge)
        assertEquals(20, ages.userAge)
    }

    @Test
    fun searchesTheTitlesAndListsByFamilyWithoutRecentEvents() = runTest {
        val events = listOf(
            testEvent("frozen", "2013-12-04", type = EventType.ANIMATED_FEATURE),
            testEvent("toy-story", "1996-03-27", type = EventType.ANIMATED_FEATURE),
            testEvent("recent", "2026-07-01", type = EventType.ANIMATED_FEATURE),
            testEvent("other-type", "1993-12-04", type = EventType.BLOCKBUSTER),
        )
        val search = SearchEvents(FakeRepository(events), Clock { today })
        assertTrue(search.search("t").isEmpty(), "two characters at least")
        assertEquals(listOf("toy-story"), search.search("toy").map { it.id })
        assertTrue(search.search("recent").isEmpty())
        val byFamily = search.byFamily()
        assertEquals(listOf(Family.CINEMA), byFamily.keys.toList())
        assertEquals(listOf("frozen", "toy-story", "other-type"), byFamily.getValue(Family.CINEMA).map { it.id })
    }
}
