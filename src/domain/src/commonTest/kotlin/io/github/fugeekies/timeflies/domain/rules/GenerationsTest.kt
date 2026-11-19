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
    fun placesTheOtherBirthTodayMinusTheirAge() {
        assertEquals(LocalDate(2011, 11, 15), Generations.birthFromAge(today, 15))
        val user = Generations.userBirth(may1991)
        assertEquals(20, Generations.gap(user, Generations.birthFromAge(today, 15)))
        // Older than the user: the gap is told the same way.
        assertEquals(30, Generations.gap(user, Generations.birthFromAge(today, 66)))
        assertEquals(30, Generations.gap(Generations.birthFromAge(today, 66), user))
    }

    @Test
    fun anAddedAgeFallsOnTheDayOfTheYearOfTheUsersBirth() {
        // The case of 19/11/2026: born in February 1995, 31 years old; an added "32" is one year older, not less.
        val day = LocalDate(2026, 11, 19)
        val user = Generations.userBirth(YearMonth(1995, Month.FEBRUARY))
        assertEquals(31, Ages.age(user, day))
        val older = Generations.birthFromAge(day, 32, anchor = user)
        assertEquals(LocalDate(1994, 2, 1), older)
        assertEquals(32, Ages.age(older, day))
        assertEquals(1, Generations.gap(user, older))
        // Without the anchor, the former rule: 32 years ago to the day, 2.5 months from the user.
        assertEquals(0, Generations.gap(user, Generations.birthFromAge(day, 32)))
        // Every age and every gap matches the ages shown, younger or older, whatever the birth month.
        for (month in Month.entries) {
            val birth = Generations.userBirth(YearMonth(1995, month))
            val userAge = Ages.age(birth, day)
            for (age in Generations.AGE_RANGE) {
                val other = Generations.birthFromAge(day, age, anchor = birth)
                assertEquals(age, Ages.age(other, day), "$month, $age")
                assertEquals(kotlin.math.abs(age - userAge), Generations.gap(birth, other), "$month, $age")
            }
        }
    }

    @Test
    fun anAnchorOnTheTwentyNinthOfFebruaryFallsOnTheTwentyEighthOutsideLeapYears() {
        val day = LocalDate(2026, 3, 10)
        assertEquals(LocalDate(2011, 2, 28), Generations.birthFromAge(day, 15, anchor = LocalDate(2000, 2, 29)))
        assertEquals(LocalDate(2016, 2, 29), Generations.birthFromAge(day, 10, anchor = LocalDate(2000, 2, 29)))
    }

    @Test
    fun allowsAnOtherPersonYoungerOrOlderWithAWarningUnderFiveYears() {
        // Decision of 15/11/2026: from 1 to 100, whatever the user's age.
        assertTrue(Generations.isOtherAgeAllowed(1))
        assertTrue(Generations.isOtherAgeAllowed(100))
        assertFalse(Generations.isOtherAgeAllowed(0))
        assertFalse(Generations.isOtherAgeAllowed(101))
        val user = Generations.userBirth(may1991)
        assertTrue(Generations.isSmallGap(user, Generations.birthFromAge(today, 32)))
        assertTrue(Generations.isSmallGap(user, Generations.birthFromAge(today, 38)))
        assertFalse(Generations.isSmallGap(user, Generations.birthFromAge(today, 30)))
        assertFalse(Generations.isSmallGap(user, Generations.birthFromAge(today, 41)))
    }

    @Test
    fun tellsTheUserAgeAtAnEvent() {
        val birth = LocalDate(1991, 5, 1)
        assertEquals(UserAge.Years(20), Ages.userAge(birth, LocalDate(2011, 11, 2)))
        assertEquals(UserAge.UnderOne, Ages.userAge(birth, LocalDate(1992, 2, 10)))
        assertEquals(UserAge.Years(1), Ages.userAge(birth, LocalDate(1992, 5, 1)))
        assertEquals(UserAge.BirthYear, Ages.userAge(birth, LocalDate(1991, 5, 1)))
        assertEquals(UserAge.BirthYear, Ages.userAge(birth, LocalDate(1991, 12, 31)))
        // Before the birth, even in the birth year: minus the age in completed years, as on "Quel âge ?" ("Tu avais −1 an.").
        assertEquals(UserAge.BeforeBirth(1), Ages.userAge(birth, LocalDate(1991, 1, 3)))
        assertEquals(UserAge.BeforeBirth(2), Ages.userAge(birth, LocalDate(1990, 3, 1)))
        assertEquals(Ages.age(birth, LocalDate(1990, 3, 1)), -2)
        assertEquals(UserAge.BeforeBirth(3), Ages.userAge(birth, LocalDate(1988, 12, 31)))
    }
}
