package io.github.fugeekies.timeflies.domain.rules

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.number

/**
 * Birth dates, ages and age gap of the user and of the other person they compare themselves with
 * (docs/UISpec/Settings.template.md). The other person can be younger or older (decision of 15/11/2026).
 */
object Generations {
    const val MIN_OTHER_AGE = 1
    const val MAX_OTHER_AGE = 100

    /** Start value of an age added in the banner (15, decision of 15/11/2026). */
    const val DEFAULT_OTHER_AGE = 15

    /** Ages that can be added next to the user's in "Quel âge ?" and Equivalences (decision of 17/11/2026). */
    const val MAX_OTHER_PEOPLE = 3

    /** Range of the ages picked on the fly on the Equivalences screen, for both people. */
    val AGE_RANGE: IntRange = MIN_OTHER_AGE..MAX_OTHER_AGE

    /** Below this gap, in either direction, the equivalences may not be meaningful: the screen warns (not forbidden). */
    const val SMALL_GAP = 5

    const val FIRST_BIRTH_YEAR = 1920

    /** The latest birth year offered is the current year minus this. */
    const val YOUNGEST_BIRTH_OFFSET = 5

    /** Birth years offered on [today]: from 1920 to the current year minus 5. */
    fun birthYears(today: LocalDate): IntRange = FIRST_BIRTH_YEAR..(today.year - YOUNGEST_BIRTH_OFFSET)

    /** The user's birth date: the 1st day of the chosen month. */
    fun userBirth(birth: YearMonth): LocalDate = LocalDate(birth.year, birth.month, 1)

    /**
     * Birth date of someone given by their age only (an age added in the banner, or a temporary age of the user).
     *
     * With [anchor], the user's birth, it falls on the same day of the year (decision of 19/11/2026): the person is
     * [age] today and the gap with the user is exactly the difference of the ages shown. Otherwise "32" would be born
     * 32 years ago to the day, while a user born in February is already near the end of their 31st year: the banner
     * said "Moins d'un an d'écart". Without [anchor]: today minus that age, to the day.
     */
    fun birthFromAge(today: LocalDate, age: Int, anchor: LocalDate? = null): LocalDate {
        if (anchor == null) return today.minus(age, DateTimeUnit.YEAR)
        // The birthday of the year: on or before today, the person already had it.
        val hadBirthday = today.month.number * 100 + today.day >= anchor.month.number * 100 + anchor.day
        val year = today.year - age - if (hadBirthday) 0 else 1
        // 29 February outside a leap year: 28 February (the user's birth is the 1st of a month today).
        val day = if (anchor.month.number == 2 && anchor.day == 29 && !Ages.isLeapYear(year)) 28 else anchor.day
        return LocalDate(year, anchor.month, day)
    }

    /** The user's age on [today], in completed years. */
    fun userAge(birth: YearMonth, today: LocalDate): Int = Ages.age(userBirth(birth), today)

    /** Gap between two birth dates in completed years, whichever is older ("20 ans d'écart"). */
    fun gap(birthA: LocalDate, birthB: LocalDate): Int =
        if (birthA <= birthB) Ages.age(birthA, birthB) else Ages.age(birthB, birthA)

    /** Whether the gap between the two births is small enough for the screen to warn (decision of 15/11/2026). */
    fun isSmallGap(birthA: LocalDate, birthB: LocalDate): Boolean = gap(birthA, birthB) < SMALL_GAP

    /** Whether [age] can be given to another person: from 1 to 100, younger or older than the user. */
    fun isOtherAgeAllowed(age: Int): Boolean = age in AGE_RANGE
}
