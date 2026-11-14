package io.github.fugeekies.timeflies.domain.rules

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AgesTest {

    private val birth = LocalDate(1991, 5, 1)

    @Test
    fun countsCompletedYears() {
        assertEquals(20, Ages.age(birth, LocalDate(2011, 11, 2)))
        assertEquals(35, Ages.age(birth, LocalDate(2026, 11, 14)))
    }

    @Test
    fun theBirthdayItselfCounts() {
        assertEquals(19, Ages.age(birth, LocalDate(2011, 4, 30)))
        assertEquals(20, Ages.age(birth, LocalDate(2011, 5, 1)))
    }

    @Test
    fun theYearOfBirthIsZero() {
        assertEquals(0, Ages.age(birth, LocalDate(1991, 5, 1)))
        assertEquals(0, Ages.age(birth, LocalDate(1991, 12, 31)))
    }

    @Test
    fun beforeBirthIsNegative() {
        assertEquals(-1, Ages.age(birth, LocalDate(1991, 4, 30)))
        assertEquals(-1, Ages.age(birth, LocalDate(1990, 5, 2)))
        assertEquals(-2, Ages.age(birth, LocalDate(1989, 11, 9)))
    }

    @Test
    fun knowsLeapYears() {
        assertTrue(Ages.isLeapYear(2000))
        assertTrue(Ages.isLeapYear(2024))
        assertFalse(Ages.isLeapYear(1900))
        assertFalse(Ages.isLeapYear(2026))
    }
}
