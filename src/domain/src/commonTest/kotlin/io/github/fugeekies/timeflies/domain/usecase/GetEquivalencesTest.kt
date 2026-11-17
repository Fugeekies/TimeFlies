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

    private fun ids(cards: List<io.github.fugeekies.timeflies.domain.model.Equivalence>) =
        cards.map { card -> listOf(card.user.event.id) + card.others.map { it?.event?.id } }

    @Test
    fun pairsTheEventsOfTheUserWithThoseOfAYoungerPersonMostRecentFirst() = runTest {
        val cards = getEquivalences(user, listOf(child), Family.entries.toSet())
        assertEquals(listOf(listOf("death-1997", "death-2017"), listOf("sitcom-1984", "sitcom-2004")), ids(cards))
        val death = cards.first()
        assertEquals(EventType.MUSIC_ICON_DEATH, death.type)
        assertEquals(UserAge.Years(6), death.user.age)
        assertEquals(UserAge.Years(6), death.others.single()!!.age)
        // The user was not born yet in January 1984: 7 years and 4 months before, "−8 ans".
        assertEquals(UserAge.BeforeBirth(8), cards[1].user.age)
    }

    @Test
    fun worksWithAnOlderPerson() = runTest {
        // The user compares with a parent born 30 years earlier: their 1984 sitcom is the parent's 1954 one.
        val cards = getEquivalences(user, listOf(parent), setOf(Family.SERIES_TV))
        assertEquals(listOf(listOf("sitcom-1984", "sitcom-1954")), ids(cards))
    }

    @Test
    fun putsEveryOtherPersonSideBySideInTheirOrder() = runTest {
        val series = getEquivalences(user, listOf(child, parent), setOf(Family.SERIES_TV))
        assertEquals(listOf(listOf("sitcom-1984", "sitcom-2004", "sitcom-1954")), ids(series))
        // A card shows as soon as one person has an equivalent; the others get null.
        val music = getEquivalences(user, listOf(child, parent), setOf(Family.MUSIC))
        assertEquals(listOf(listOf("death-1997", "death-2017", null)), ids(music))
    }

    @Test
    fun skipsEventsWhoseTargetIsInTheFuture() = runTest {
        // The 2017 death points to 2037 in the child's life: no card.
        val cards = getEquivalences(user, listOf(child), setOf(Family.MUSIC))
        assertEquals(listOf("death-1997"), cards.map { it.user.event.id })
    }

    @Test
    fun keepsOnlyTheTickedFamiliesAndNeedsAnotherAge() = runTest {
        assertEquals(listOf("death-1997"), getEquivalences(user, listOf(child), setOf(Family.MUSIC)).map { it.user.event.id })
        assertTrue(getEquivalences(user, listOf(child), emptySet()).isEmpty())
        assertTrue(getEquivalences(user, emptyList(), Family.entries.toSet()).isEmpty(), "the user alone has no equivalence")
    }

    @Test
    fun allowsASmallGap() = runTest {
        // Same birth: an event pairs with another of its type close by, never with itself.
        val twins = GetEquivalences(
            FakeRepository(listOf(testEvent("a", "2000-01-01"), testEvent("b", "2001-01-01"))),
            Clock { today },
        )
        assertEquals(listOf(listOf("b", "a"), listOf("a", "b")), ids(twins(user, listOf(user), setOf(Family.CINEMA))))
    }
}
