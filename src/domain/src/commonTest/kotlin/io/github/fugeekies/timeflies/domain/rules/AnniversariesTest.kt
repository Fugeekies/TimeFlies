package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.testEvent
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnniversariesTest {

    private val france = Region.FRANCE
    private val today = LocalDate(2026, 11, 2)

    private fun ids(day: LocalDate, events: List<Event>, birth: LocalDate? = null) =
        Anniversaries.on(day, events, france, birth).map { it.event.id }

    @Test
    fun keepsOnlyEventsOfThatDayAndAtLeastOneYearOld() {
        val events = listOf(
            testEvent("same-day", "2011-11-02"),
            testEvent("other-day", "2011-11-03"),
            testEvent("this-year", "2026-11-02"),
        )
        assertEquals(listOf("same-day"), ids(today, events))
        assertEquals(15, Anniversaries.on(today, events, france, null).single().years)
    }

    @Test
    fun putsMultiplesOfTenFirstThenOfFive() {
        val events = listOf(
            testEvent("years-12", "2014-11-02", importance = 3),
            testEvent("years-15", "2011-11-02", importance = 1),
            testEvent("years-20", "2006-11-02", importance = 1),
            testEvent("years-25", "2001-11-02", importance = 3),
            testEvent("years-30", "1996-11-02", importance = 2),
        )
        assertEquals(listOf("years-30", "years-20", "years-25", "years-15", "years-12"), ids(today, events))
    }

    @Test
    fun thenHighestImportanceThenMostRecent() {
        val events = listOf(
            testEvent("old-important", "1983-11-02", importance = 3),
            testEvent("recent-minor", "2023-11-02", importance = 1),
            testEvent("recent-important", "2019-11-02", importance = 3),
        )
        assertEquals(listOf("recent-important", "old-important", "recent-minor"), ids(today, events))
    }

    @Test
    fun usesTheRegionalDate() {
        val event = testEvent("film", "2011-09-23", regionalDates = mapOf(france to LocalDate(2011, 11, 2)))
        assertEquals(listOf("film"), ids(today, listOf(event)))
        assertTrue(Anniversaries.on(today, listOf(event), Region.of("US")!!, null).isEmpty())
    }

    @Test
    fun celebratesFebruary29OnFebruary28OfACommonYear() {
        val leapEvent = listOf(testEvent("leap", "2000-02-29"))
        assertEquals(listOf(MonthDay(2, 28), MonthDay(2, 29)), Anniversaries.celebratedOn(LocalDate(2026, 2, 28)))
        assertEquals(listOf("leap"), ids(LocalDate(2026, 2, 28), leapEvent))
        assertEquals(26, Anniversaries.on(LocalDate(2026, 2, 28), leapEvent, france, null).single().years)
        // In a leap year it keeps its own day.
        assertEquals(listOf(MonthDay(2, 28)), Anniversaries.celebratedOn(LocalDate(2028, 2, 28)))
        assertTrue(ids(LocalDate(2028, 2, 28), leapEvent).isEmpty())
        assertEquals(listOf("leap"), ids(LocalDate(2028, 2, 29), leapEvent))
    }

    @Test
    fun givesTheUserAgeWhenTheBirthIsKnown() {
        val events = listOf(testEvent("film", "2011-11-02"), testEvent("before", "1989-11-02"))
        val birth = LocalDate(1991, 5, 1)
        val byId = Anniversaries.on(today, events, france, birth).associateBy { it.event.id }
        assertEquals(20, byId.getValue("film").userAge)
        assertEquals(-2, byId.getValue("before").userAge)
        assertNull(Anniversaries.on(today, events, france, null).first().userAge)
    }

    @Test
    fun marksRoundAnniversaries() {
        val list = Anniversaries.on(today, listOf(testEvent("a", "2011-11-02"), testEvent("b", "2008-11-02")), france, null)
        assertEquals(mapOf("a" to true, "b" to false), list.associate { it.event.id to it.isRound })
    }

    private fun shock(events: List<Event>, day: LocalDate = today) =
        Anniversaries.dailyShock(day, { d -> events.filter { e -> MonthDay.of(e.date) in Anniversaries.celebratedOn(d) } }, france, null)

    @Test
    fun theMainAnniversaryIsTodaysFirst() {
        val result = shock(
            listOf(
                testEvent("today-15", "2011-11-02"),
                testEvent("today-8", "2018-11-02"),
                testEvent("in-2-days", "2008-11-04"),
            ),
        )
        assertEquals("today-15", result.main?.event?.id)
        assertEquals(0, result.mainInDays)
        assertEquals(listOf("today-8"), result.alsoToday.map { it.event.id })
        assertEquals(listOf(2), result.upcoming.map { it.inDays })
        assertEquals(listOf("in-2-days"), result.upcoming.single().anniversaries.map { it.event.id })
    }

    @Test
    fun fallsBackOnTheNextSevenDaysInTheUsualOrder() {
        val result = shock(
            listOf(
                testEvent("in-3-days-12", "2014-11-05", importance = 3),
                testEvent("in-3-days-25", "2001-11-05", importance = 1),
                testEvent("in-7-days", "1989-11-09"),
                testEvent("in-8-days", "1999-11-10"),
            ),
        )
        assertEquals("in-3-days-25", result.main?.event?.id)
        assertEquals(3, result.mainInDays)
        assertTrue(result.alsoToday.isEmpty())
        assertEquals(listOf(3, 7), result.upcoming.map { it.inDays })
        assertEquals(listOf("in-3-days-12"), result.upcoming.first().anniversaries.map { it.event.id })
    }

    @Test
    fun nothingInTheNextSevenDays() {
        val result = shock(listOf(testEvent("far", "2000-12-25")))
        assertNull(result.main)
        assertTrue(result.alsoToday.isEmpty())
        assertTrue(result.upcoming.isEmpty())
    }

    @Test
    fun keepsAtMostTenUpcomingAnniversaries() {
        val events = (1..15).map { testEvent("e$it", "${1990 + it}-11-03") }
        val result = shock(events + testEvent("today", "2011-11-02"))
        assertEquals(10, result.upcoming.sumOf { it.anniversaries.size })
    }

    @Test
    fun upcomingDaysCrossTheEndOfTheYear() {
        val result = shock(listOf(testEvent("new-year", "2000-01-01")), day = LocalDate(2026, 12, 30))
        assertEquals("new-year", result.main?.event?.id)
        assertEquals(2, result.mainInDays)
        assertEquals(27, result.main?.years)
    }
}
