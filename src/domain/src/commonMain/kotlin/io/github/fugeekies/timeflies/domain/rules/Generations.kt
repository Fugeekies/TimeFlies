package io.github.fugeekies.timeflies.domain.rules

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus

/** Birth dates, ages and age gap of the user and of the young person (docs/UISpec/Settings.template.md, decision of 13/11/2026). */
object Generations {
    const val MIN_YOUNG_AGE = 5
    const val MAX_YOUNG_AGE = 60
    const val DEFAULT_YOUNG_AGE = 15

    /** The young person must be at least this many years younger than the user. */
    const val MIN_GAP = 5

    const val FIRST_BIRTH_YEAR = 1920

    /** The latest birth year offered is the current year minus this. */
    const val YOUNGEST_BIRTH_OFFSET = 5

    /** Birth years offered on [today]: from 1920 to the current year minus 5. */
    fun birthYears(today: LocalDate): IntRange = FIRST_BIRTH_YEAR..(today.year - YOUNGEST_BIRTH_OFFSET)

    /** The user's birth date: the 1st day of the chosen month. */
    fun userBirth(birth: YearMonth): LocalDate = LocalDate(birth.year, birth.month, 1)

    /** The young person's birth date: today minus their age, to the day. */
    fun youngBirth(today: LocalDate, youngAge: Int): LocalDate = today.minus(youngAge, DateTimeUnit.YEAR)

    /** The user's age on [today], in completed years. */
    fun userAge(birth: YearMonth, today: LocalDate): Int = Ages.age(userBirth(birth), today)

    /** Gap between the two birth dates, in completed years ("20 ans d'écart"). */
    fun gap(birth: YearMonth, youngAge: Int, today: LocalDate): Int =
        Ages.age(userBirth(birth), youngBirth(today, youngAge))

    /**
     * Whether [youngAge] can be saved: between 5 and 60, and at least 5 years younger than the user
     * when their birth is known.
     */
    fun isYoungAgeAllowed(youngAge: Int, birth: YearMonth?, today: LocalDate): Boolean =
        youngAge in MIN_YOUNG_AGE..MAX_YOUNG_AGE && (birth == null || gap(birth, youngAge, today) >= MIN_GAP)
}
