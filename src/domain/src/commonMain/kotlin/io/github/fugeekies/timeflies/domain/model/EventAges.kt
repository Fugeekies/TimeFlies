package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate

/**
 * How old the two people were on the date of one event (docs/UISpec/EventAges.template.md, decision of 15/11/2026).
 *
 * Ages are in completed years and negative before a birth, like [io.github.fugeekies.timeflies.domain.rules.Ages.age]:
 * −1 for less than a year before, −14 for 13 to 14 years before ("−14 ans").
 *
 * @property date date of the event in the chosen region.
 * @property userAge the user's age on [date].
 * @property otherAge the other person's age on [date].
 */
data class EventAges(
    val event: Event,
    val date: LocalDate,
    val userAge: Int,
    val otherAge: Int,
)
