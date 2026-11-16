package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.testEvent
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WidgetSheetsTest {

    private val today = LocalDate(2026, 11, 2)

    private fun shock(vararg events: Event): DailyShock =
        Anniversaries.dailyShock(today, { day -> events.filter { it.date.month == day.month && it.date.day == day.day } }, Region.FRANCE, null)

    private fun ids(list: List<io.github.fugeekies.timeflies.domain.model.Anniversary>) = list.map { it.event.id }

    @Test
    fun goesThroughTheAnniversariesOfTheDayInDisplayOrder() {
        val shock = shock(testEvent("round", "2011-11-02"), testEvent("other", "2013-11-02"), testEvent("soon", "2001-11-04"))
        assertEquals(listOf("round", "other"), ids(WidgetSheets.of(shock)), "the 15-year one first, then the others of today")
    }

    @Test
    fun takesTheUpcomingDayWhenNothingIsToday() {
        val shock = shock(testEvent("a", "2001-11-05"), testEvent("b", "2003-11-05"), testEvent("later", "2002-11-07"))
        assertEquals(3, shock.mainInDays)
        assertEquals(listOf("a", "b"), ids(WidgetSheets.of(shock)))
    }

    @Test
    fun isEmptyWithoutAnyAnniversary() {
        assertTrue(WidgetSheets.of(shock()).isEmpty())
        assertTrue(WidgetSheets.following(shock(), 0).isEmpty())
    }

    @Test
    fun compactLinesFollowTheNextButton() {
        val shock = shock(
            testEvent("round", "2011-11-02"),
            testEvent("other", "2013-11-02"),
            testEvent("soon", "2001-11-04"),
            testEvent("later", "2002-11-06"),
        )
        assertEquals(listOf("other", "soon"), ids(WidgetSheets.following(shock, 0)))
        assertEquals(listOf("soon", "later"), ids(WidgetSheets.following(shock, 1)), "after the last of the day, the next days")
    }

    @Test
    fun nextComesBackToTheFirstAfterTheLast() {
        assertEquals(1, WidgetSheets.next(0, 3))
        assertEquals(0, WidgetSheets.next(2, 3))
        assertEquals(0, WidgetSheets.next(0, 1))
        assertEquals(0, WidgetSheets.next(5, 0))
    }
}
