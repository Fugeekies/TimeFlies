package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.UserAge
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenerationsTest {

    private val today = LocalDate(2026, 11, 15)
    private val may1991 = YearMonth(1991, Month.MAY)

    @Test
    fun offersBirthYearsFrom1920ToFiveYearsAgo() {
        assertEquals(1920..2021, Generations.birthYears(today))
    }

    @Test
    fun takesTheFirstDayOfTheBirthMonth() {
        assertEquals(LocalDate(1991, 5, 1), Generations.userBirth(may1991))
        assertEquals(35, Generations.userAge(may1991, today))
        // During the birth month the age is a year ahead, before the real day (decision of 13/11/2026).
        assertEquals(35, Generations.userAge(YearMonth(1991, Month.NOVEMBER), today))
    }

    @Test
    fun placesTheYoungBirthTodayMinusTheirAge() {
        assertEquals(LocalDate(2011, 11, 15), Generations.youngBirth(today, 15))
        assertEquals(20, Generations.gap(may1991, 15, today))
    }

    @Test
    fun keepsTheYoungAtLeastFiveYearsYounger() {
        assertTrue(Generations.isYoungAgeAllowed(30, may1991, today))
        assertFalse(Generations.isYoungAgeAllowed(31, may1991, today))
        // Without a birth, only the slider range applies.
        assertTrue(Generations.isYoungAgeAllowed(60, null, today))
        assertFalse(Generations.isYoungAgeAllowed(4, null, today))
        assertFalse(Generations.isYoungAgeAllowed(61, null, today))
    }

    @Test
    fun tellsTheUserAgeAtAnEvent() {
        val birth = LocalDate(1991, 5, 1)
        assertEquals(UserAge.Years(20), Ages.userAge(birth, LocalDate(2011, 11, 2)))
        assertEquals(UserAge.UnderOne, Ages.userAge(birth, LocalDate(1992, 2, 10)))
        assertEquals(UserAge.Years(1), Ages.userAge(birth, LocalDate(1992, 5, 1)))
        assertEquals(UserAge.BirthYear, Ages.userAge(birth, LocalDate(1991, 1, 3)))
        assertEquals(UserAge.BirthYear, Ages.userAge(birth, LocalDate(1991, 12, 31)))
        // Before the birth year: difference of calendar years.
        assertEquals(UserAge.BeforeBirth(1), Ages.userAge(birth, LocalDate(1990, 3, 1)))
        assertEquals(UserAge.BeforeBirth(3), Ages.userAge(birth, LocalDate(1988, 12, 31)))
    }
}
