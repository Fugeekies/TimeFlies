package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.UpcomingDay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus

/** Rules of the "coup de vieux du jour" (docs/EVENTS_DATA.md, "Règles du coup de vieux du jour"). */
object Anniversaries {
    const val UPCOMING_DAYS = 7
    const val MAX_UPCOMING = 10

    /**
     * The days of the year whose events celebrate their anniversary on [day]: its own day, plus
     * 29 February on 28 February of a non-leap year (decision of 13/11/2026).
     */
    fun celebratedOn(day: LocalDate): List<MonthDay> {
        val own = MonthDay.of(day)
        val isFeb28OfCommonYear = day.month.number == 2 && day.day == 28 && !Ages.isLeapYear(day.year)
        return if (isFeb28OfCommonYear) listOf(own, MonthDay(2, 29)) else listOf(own)
    }

    /**
     * Anniversaries on [day] among [candidates], in display order: multiples of 10 years, then of 5,
     * then the others; within each group, highest importance, then most recent event first.
     */
    fun on(day: LocalDate, candidates: List<Event>, region: Region, birth: LocalDate?): List<Anniversary> {
        val celebrated = celebratedOn(day).toSet()
        return candidates
            .asSequence()
            .map { it to it.dateFor(region) }
            .filter { (_, date) -> MonthDay.of(date) in celebrated }
            .map { (event, date) -> Anniversary(event, day, date, day.year - date.year, birth?.let { Ages.age(it, date) }) }
            .filter { it.years >= 1 }
            .sortedWith(displayOrder)
            .toList()
    }

    /**
     * Builds the daily shock from the anniversaries of [today] and of the next [UPCOMING_DAYS] days.
     *
     * @param candidatesByDay events whose day of the year matches each day (see [celebratedOn]), keyed by day.
     */
    fun dailyShock(
        today: LocalDate,
        candidatesByDay: (LocalDate) -> List<Event>,
        region: Region,
        birth: LocalDate?,
    ): DailyShock {
        val todayList = on(today, candidatesByDay(today), region, birth)
        val nextDays = (1..UPCOMING_DAYS).map { offset ->
            val date = today.plus(offset, DateTimeUnit.DAY)
            UpcomingDay(date, offset, on(date, candidatesByDay(date), region, birth))
        }

        val main: Anniversary?
        val mainInDays: Int
        if (todayList.isNotEmpty()) {
            main = todayList.first()
            mainInDays = 0
        } else {
            val firstDay = nextDays.firstOrNull { it.anniversaries.isNotEmpty() }
            main = firstDay?.anniversaries?.first()
            mainInDays = firstDay?.inDays ?: 0
        }

        var remaining = MAX_UPCOMING
        val upcoming = nextDays.mapNotNull { day ->
            val list = day.anniversaries.filter { it != main }.take(remaining)
            remaining -= list.size
            if (list.isEmpty()) null else day.copy(anniversaries = list)
        }

        return DailyShock(
            today = today,
            main = main,
            mainInDays = mainInDays,
            alsoToday = if (mainInDays == 0) todayList.drop(1) else emptyList(),
            upcoming = upcoming,
        )
    }

    private fun roundRank(years: Int): Int = when {
        years % 10 == 0 -> 0
        years % 5 == 0 -> 1
        else -> 2
    }

    private val displayOrder: Comparator<Anniversary> =
        compareBy<Anniversary> { roundRank(it.years) }
            .thenByDescending { it.event.importance }
            .thenByDescending { it.eventDate }
            .thenBy { it.event.id }
}
