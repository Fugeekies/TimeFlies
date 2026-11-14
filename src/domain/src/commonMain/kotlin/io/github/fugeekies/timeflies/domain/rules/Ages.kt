package io.github.fugeekies.timeflies.domain.rules

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

    fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}
