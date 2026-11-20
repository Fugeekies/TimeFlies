package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.UserAge
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/** Ages in completed years, like a real age (docs/ARCHITECTURE.md, "Règles de calcul"). */
object Ages {
    /**
     * Completed years between [birth] and [at]. Negative when [at] is before [birth]
     * (-1 for a date less than a year before the birth).
     */
    fun age(birth: LocalDate, at: LocalDate): Int {
        if (at < birth) return -age(at, birth) - 1
        val beforeBirthday = dayOfYearKey(at) < dayOfYearKey(birth)
        return at.year - birth.year - if (beforeBirthday) 1 else 0
    }

    /** Month and day as one comparable number (1105 for 5 November): whether a birthday has passed in a year. */
    internal fun dayOfYearKey(date: LocalDate): Int = date.month.number * 100 + date.day

    /**
     * How old the person born on [birth] was on [at]: before the birth, a negative age in completed years, as on the
     * "Quel âge ?" screen ("Tu avais −3 ans.", decision of 15/11/2026); in the birth year after the birth, "the year of
     * the birth"; then completed years, "less than a year" for 0.
     */
    fun userAge(birth: LocalDate, at: LocalDate): UserAge = when {
        at < birth -> UserAge.BeforeBirth(-age(birth, at))
        at.year == birth.year -> UserAge.BirthYear
        else -> when (val years = age(birth, at)) {
            0 -> UserAge.UnderOne
            else -> UserAge.Years(years)
        }
    }

    fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}
