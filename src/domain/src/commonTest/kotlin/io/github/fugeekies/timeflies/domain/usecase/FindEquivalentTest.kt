package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.UserAge
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.testEvent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FindEquivalentTest {

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
    private val clock = Clock { today }

    private val frozen = testEvent("frozen", "2013-12-04", type = EventType.ANIMATED_FEATURE)
    private val events = listOf(
        frozen,
        testEvent("lion-king", "1994-11-09", type = EventType.ANIMATED_FEATURE),
        testEvent("aladdin", "1993-11-24", type = EventType.ANIMATED_FEATURE, importance = 3),
        testEvent("toy-story", "1996-03-27", type = EventType.ANIMATED_FEATURE),
        testEvent("snow-white", "1938-05-04", type = EventType.ANIMATED_FEATURE),
        testEvent("recent", "2026-07-01", type = EventType.ANIMATED_FEATURE),
        testEvent("other-type", "1993-12-04", type = EventType.BLOCKBUSTER),
    )
    private val findEquivalent = FindEquivalent(FakeRepository(events), clock)

    @Test
    fun givesTheBestCandidateAndTheOthersFromTheClosest() = runTest {
        // Frozen in the child's life: the user had the same age around late May 1993.
        val result = findEquivalent(frozen, fromBirth = child, toBirth = user)
        assertEquals(UserAge.Years(2), result.chosen.age)
        assertEquals("aladdin", result.best?.side?.event?.id)
        // Target 20 May 1993: Toy Story (March 1996) is beyond the 2 years.
        assertEquals(listOf("lion-king"), result.others.map { it.side.event.id })
        assertTrue(result.outside.isEmpty())
        assertEquals(UserAge.Years(2), result.best?.side?.age)
    }

    @Test
    fun tellsHowFarEachCandidateIsFromTheTarget() = runTest {
        val result = findEquivalent(frozen, fromBirth = child, toBirth = user)
        val best = result.best!!
        // Positive: the candidate is after the target date.
        assertEquals(LocalDate(1993, 5, 20), result.target)
        assertEquals(DatePeriod(months = 6, days = 4), best.offset)
    }

    @Test
    fun showsTheClosestEventsOutsideTheToleranceWhenThereIsNoEquivalent() = runTest {
        // Snow White (1938) in the child's life points to 1917 in the user's: nothing within 2 years.
        val result = findEquivalent(events.first { it.id == "snow-white" }, fromBirth = child, toBirth = user)
        assertNull(result.best)
        assertTrue(result.others.isEmpty())
        assertEquals(listOf("aladdin", "lion-king", "toy-story"), result.outside.map { it.side.event.id })
        assertTrue(result.outside.first().offset.years >= 2)
    }

    @Test
    fun givesNoEquivalentWhenTheTargetIsInTheFuture() = runTest {
        // Frozen in the user's life (22 years old) is 2034 for the child.
        val result = findEquivalent(frozen, fromBirth = user, toBirth = child)
        assertTrue(result.isTargetInFuture(today))
        assertNull(result.best)
        // The recent film (less than a year old) is never listed.
        assertTrue(result.outside.none { it.side.event.id == "recent" })
    }

    @Test
    fun searchesTheTitlesAndListsByFamilyWithoutRecentEvents() = runTest {
        val search = SearchEvents(FakeRepository(events), clock)
        assertTrue(search.search("t").isEmpty(), "two characters at least")
        assertEquals(listOf("toy-story"), search.search("toy").map { it.id })
        assertTrue(search.search("recent").isEmpty())
        val byFamily = search.byFamily()
        assertEquals(listOf(Family.CINEMA), byFamily.keys.toList())
        assertEquals("frozen", byFamily.getValue(Family.CINEMA).first().id)
        assertTrue(byFamily.getValue(Family.CINEMA).none { it.id == "recent" })
    }
}
