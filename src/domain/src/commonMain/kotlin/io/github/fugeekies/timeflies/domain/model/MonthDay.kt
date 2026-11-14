package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/** A day of the year without a year ("02/11"), used to find the anniversaries of a date. */
data class MonthDay(val month: Int, val day: Int) {
    init {
        require(month in 1..12) { "month must be in 1..12, was $month" }
        require(day in 1..MAX_DAYS[month - 1]) { "day $day does not exist in month $month" }
    }

    companion object {
        // February allows 29: an event of 29 February keeps that day (docs/EVENTS_DATA.md).
        private val MAX_DAYS = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        fun of(date: LocalDate): MonthDay = MonthDay(date.month.number, date.day)
    }
}
