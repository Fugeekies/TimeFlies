package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate

/**
 * How old the people were on the date of one event (docs/UISpec/EventAges.template.md, decisions of 15/11/2026 and
 * 17/11/2026).
 *
 * Ages are in completed years and negative before a birth, like [io.github.fugeekies.timeflies.domain.rules.Ages.age]:
 * −1 for less than a year before, −14 for 13 to 14 years before ("−14 ans").
 *
 * @property date date of the event in the chosen region.
 * @property ages one age per person on [date], in the order of the births given: the user first, then the other ages.
 */
data class EventAges(
    val event: Event,
    val date: LocalDate,
    val ages: List<Int>,
)
