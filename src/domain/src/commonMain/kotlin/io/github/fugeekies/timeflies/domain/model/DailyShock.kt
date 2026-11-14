package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate

/**
 * An event that has its anniversary on [day].
 *
 * @property eventDate date of the event in the user's region.
 * @property years number of years elapsed on [day] (at least 1).
 * @property userAge age the user had on [eventDate], in completed years; negative before their birth;
 *   null when the user's birth month and year are unknown.
 */
data class Anniversary(
    val event: Event,
    val day: LocalDate,
    val eventDate: LocalDate,
    val years: Int,
    val userAge: Int?,
) {
    /** A multiple of 5 years: shown with the "Chiffre rond" chip. */
    val isRound: Boolean get() = years % 5 == 0
}

/** Anniversaries of one upcoming day. */
data class UpcomingDay(val date: LocalDate, val inDays: Int, val anniversaries: List<Anniversary>)

/**
 * The "coup de vieux du jour" (docs/UISpec/Today.template.md).
 *
 * @property main the anniversary shown on the sheet: today's first, or the first of the next 7 days; null when none.
 * @property mainInDays 0 when [main] is today, otherwise the number of days until it.
 * @property alsoToday today's other anniversaries, in display order.
 * @property upcoming the next 7 days (without today and without [main]), at most 10 anniversaries in all.
 */
data class DailyShock(
    val today: LocalDate,
    val main: Anniversary?,
    val mainInDays: Int,
    val alsoToday: List<Anniversary>,
    val upcoming: List<UpcomingDay>,
)
