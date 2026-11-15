package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate

/**
 * One side of an equivalence: an event in the life of one of the two people.
 *
 * @property date date of the event in the chosen region.
 * @property age how old that person was on [date], told as on the Today screen (decision of 15/11/2026).
 */
data class EquivalenceSide(
    val event: Event,
    val date: LocalDate,
    val age: UserAge,
)

/**
 * Two events of the same type that hold the same place in the lives of two people (docs/EVENTS_DATA.md,
 * "Règles d'appariement des équivalences"). [left] belongs to the person whose events are listed, [right] to the
 * other one.
 */
data class Equivalence(
    val type: EventType,
    val left: EquivalenceSide,
    val right: EquivalenceSide,
)
