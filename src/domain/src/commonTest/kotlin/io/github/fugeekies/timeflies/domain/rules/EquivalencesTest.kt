package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.testEvent
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EquivalencesTest {

    private val today = LocalDate(2026, 11, 15)
    private val region = Region.FRANCE

    // The user born on 1 May 1991, the other person 15 years old (born 15 November 2011).
    private val user = LocalDate(1991, 5, 1)
    private val other = LocalDate(2011, 11, 15)

    @Test
    fun shiftsTheDateByTheGapBetweenTheBirthsInBothDirections() {
        val fromOther = Equivalences.targetDate(LocalDate(2018, 7, 15), fromBirth = other, toBirth = user)
        // The same number of days after each birth (2 434), not the same calendar months.
        assertEquals(LocalDate(1997, 12, 29), fromOther)
        // Back again: the same day.
        assertEquals(LocalDate(2018, 7, 15), Equivalences.targetDate(fromOther, fromBirth = user, toBirth = other))
    }

    @Test
    fun keepsTheClosestCandidateWithinTwoYears() {
        val event = testEvent("young", "2018-07-15")
        val target = LocalDate(1997, 12, 1)
        val candidates = listOf(
            testEvent("far", "1995-01-01"),
            testEvent("close", "1998-03-01"),
            testEvent("closer", "1997-10-01"),
        )
        assertEquals("closer", Equivalences.bestMatch(event, target, candidates, region, today)?.id)
        assertNull(Equivalences.bestMatch(event, target, listOf(testEvent("far", "1995-01-01")), region, today))
    }

    @Test
    fun breaksTiesWithTheHighestImportance() {
        val event = testEvent("young", "2018-07-15")
        val target = LocalDate(1997, 12, 1)
        val candidates = listOf(testEvent("known", "1997-11-01", importance = 2), testEvent("famous", "1997-12-31", importance = 3))
        // Both 30 days away.
        assertEquals("famous", Equivalences.bestMatch(event, target, candidates, region, today)?.id)
    }

    @Test
    fun pairsOnlyTheSameTypeAndNeverTheEventItself() {
        val event = testEvent("self", "2000-01-01", type = EventType.SITCOM_HIT)
        val candidates = listOf(event, testEvent("other-type", "2000-01-02", type = EventType.BLOCKBUSTER))
        assertNull(Equivalences.bestMatch(event, LocalDate(2000, 1, 1), candidates, region, today))
    }

    @Test
    fun givesNoEquivalentForATargetInTheFuture() {
        val event = testEvent("old", "2020-01-01")
        assertNull(Equivalences.bestMatch(event, LocalDate(2027, 1, 1), listOf(testEvent("x", "2025-06-01")), region, today))
    }

    @Test
    fun ignoresEventsLessThanAYearOld() {
        assertTrue(Equivalences.isOldEnough(LocalDate(2025, 11, 15), today))
        assertFalse(Equivalences.isOldEnough(LocalDate(2025, 11, 16), today))
        val event = testEvent("old", "2010-01-01")
        assertNull(Equivalences.bestMatch(event, LocalDate(2025, 6, 1), listOf(testEvent("recent", "2026-01-01")), region, today))
    }

    @Test
    fun usesTheRegionalDate() {
        val event = testEvent("young", "2018-07-15")
        val candidate = testEvent("regional", "1990-01-01", regionalDates = mapOf(region to LocalDate(1997, 12, 1)))
        assertEquals("regional", Equivalences.bestMatch(event, LocalDate(1997, 12, 1), listOf(candidate), region, today)?.id)
    }
}
