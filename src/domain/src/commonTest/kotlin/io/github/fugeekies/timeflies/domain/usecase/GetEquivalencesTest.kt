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
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetEquivalencesTest {

    /** Same contract as the data layer: events of a type in a date range, sorted by regional date. */
    private class FakeRepository(private val events: List<Event>) : EventRepository {
        override suspend fun onMonthDay(monthDay: MonthDay, region: Region) = emptyList<Event>()
        override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region) =
            events.filter { it.type == type && it.dateFor(region) in from..to }.sortedBy { it.dateFor(region) }
        override suspend fun search(query: String, limit: Int) = emptyList<Event>()
        override suspend fun count() = events.size
    }

    private val today = LocalDate(2026, 11, 15)
    private val user = LocalDate(1991, 5, 1)
    private val child = LocalDate(2011, 11, 15)
    private val parent = LocalDate(1961, 5, 1)

    private val events = listOf(
        testEvent("sitcom-2004", "2004-09-22", type = EventType.SITCOM_HIT),
        testEvent("sitcom-1984", "1984-01-10", type = EventType.SITCOM_HIT),
        testEvent("sitcom-1954", "1954-06-01", type = EventType.SITCOM_HIT),
        testEvent("death-2017", "2017-12-05", type = EventType.MUSIC_ICON_DEATH),
        testEvent("death-1997", "1997-11-24", type = EventType.MUSIC_ICON_DEATH),
        testEvent("film-2020", "2020-01-01", type = EventType.BLOCKBUSTER),
    )
    private val getEquivalences = GetEquivalences(FakeRepository(events), Clock { today })

    @Test
    fun pairsTheEventsOfTheChildWithThoseOfTheUserMostRecentFirst() = runTest {
        val pairs = getEquivalences(leftBirth = child, rightBirth = user, families = Family.entries.toSet())
        assertEquals(listOf("death-2017" to "death-1997", "sitcom-2004" to "sitcom-1984"), pairs.map { it.left.event.id to it.right.event.id })
        val death = pairs.first()
        assertEquals(EventType.MUSIC_ICON_DEATH, death.type)
        assertEquals(UserAge.Years(6), death.left.age)
        assertEquals(UserAge.Years(6), death.right.age)
        // The child was not born yet in 2004: 7 years and 2 months before, "−8 ans".
        assertEquals(UserAge.BeforeBirth(8), pairs[1].left.age)
    }

    @Test
    fun worksWithAnOlderPerson() = runTest {
        // The user compares with a parent born 30 years earlier: their 1984 sitcom is the parent's 1954 one.
        val pairs = getEquivalences(leftBirth = user, rightBirth = parent, families = setOf(Family.SERIES_TV))
        assertTrue("sitcom-1984" to "sitcom-1954" in pairs.map { it.left.event.id to it.right.event.id })
        // The other way round, the target of the 1954 sitcom is in 1984.
        val reversed = getEquivalences(leftBirth = parent, rightBirth = user, families = setOf(Family.SERIES_TV))
        assertTrue("sitcom-1954" to "sitcom-1984" in reversed.map { it.left.event.id to it.right.event.id })
    }

    @Test
    fun skipsEventsWhoseTargetIsInTheFuture() = runTest {
        // Seen from the user, the 2017 death points to 2044 in the child's life: no pair.
        val pairs = getEquivalences(leftBirth = user, rightBirth = child, families = setOf(Family.MUSIC))
        assertEquals(listOf("death-1997" to "death-2017"), pairs.map { it.left.event.id to it.right.event.id })
    }

    @Test
    fun keepsOnlyTheTickedFamilies() = runTest {
        val pairs = getEquivalences(leftBirth = child, rightBirth = user, families = setOf(Family.MUSIC))
        assertEquals(listOf("death-2017"), pairs.map { it.left.event.id })
        assertTrue(getEquivalences(leftBirth = child, rightBirth = user, families = emptySet()).isEmpty())
    }

    @Test
    fun allowsASmallGap() = runTest {
        // Same birth: an event pairs with another of its type close by, never with itself.
        val twins = GetEquivalences(
            FakeRepository(listOf(testEvent("a", "2000-01-01"), testEvent("b", "2001-01-01"))),
            Clock { today },
        )
        val pairs = twins(leftBirth = user, rightBirth = user, families = setOf(Family.CINEMA))
        assertEquals(listOf("b" to "a", "a" to "b"), pairs.map { it.left.event.id to it.right.event.id })
    }
}
