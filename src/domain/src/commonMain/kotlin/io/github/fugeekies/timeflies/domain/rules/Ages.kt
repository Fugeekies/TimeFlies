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
        val beforeBirthday = at.month.number * 100 + at.day < birth.month.number * 100 + birth.day
        return at.year - birth.year - if (beforeBirthday) 1 else 0
    }

    /**
     * How old the user born on [birth] was on [at] (decision of 15/11/2026): the birth year itself and the
     * years before it are told in calendar years; after it, in completed years, "less than a year" for 0.
     */
    fun userAge(birth: LocalDate, at: LocalDate): UserAge = when {
        at.year < birth.year -> UserAge.BeforeBirth(birth.year - at.year)
        at.year == birth.year -> UserAge.BirthYear
        else -> when (val years = age(birth, at)) {
            0 -> UserAge.UnderOne
            else -> UserAge.Years(years)
        }
    }

    fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}
