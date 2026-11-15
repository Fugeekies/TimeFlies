package io.github.fugeekies.timeflies.domain.rules

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus

/**
 * Birth dates, ages and age gap of the user and of the other person they compare themselves with
 * (docs/UISpec/Settings.template.md). The other person can be younger or older (decision of 15/11/2026).
 */
object Generations {
    const val MIN_OTHER_AGE = 1
    const val MAX_OTHER_AGE = 100
    const val DEFAULT_OTHER_AGE = 15

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

    /** Birth date of someone given by their age only (the other person, or an age picked on the fly): today minus that age, to the day. */
    fun birthFromAge(today: LocalDate, age: Int): LocalDate = today.minus(age, DateTimeUnit.YEAR)

    /** The user's age on [today], in completed years. */
    fun userAge(birth: YearMonth, today: LocalDate): Int = Ages.age(userBirth(birth), today)

    /** Gap between two birth dates in completed years, whichever is older ("20 ans d'écart"). */
    fun gap(birthA: LocalDate, birthB: LocalDate): Int =
        if (birthA <= birthB) Ages.age(birthA, birthB) else Ages.age(birthB, birthA)

    /** Whether the gap between the two births is small enough for the screen to warn (decision of 15/11/2026). */
    fun isSmallGap(birthA: LocalDate, birthB: LocalDate): Boolean = gap(birthA, birthB) < SMALL_GAP

    /** Whether [age] can be saved as the other person's age: from 1 to 100, younger or older than the user. */
    fun isOtherAgeAllowed(age: Int): Boolean = age in AGE_RANGE
}
