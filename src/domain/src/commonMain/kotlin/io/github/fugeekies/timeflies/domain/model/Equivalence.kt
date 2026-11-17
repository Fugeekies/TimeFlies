package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate

/**
 * One side of an equivalence: an event in the life of one of the people.
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
 * An event in the user's life and, for each other age, the event of the same type that holds the same place in
 * that person's life (docs/EVENTS_DATA.md, "Règles d'appariement des équivalences"; decision of 17/11/2026: up to
 * three other ages side by side).
 *
 * @property user the user's event, which orders the list.
 * @property others one entry per other age, in the order of the banner; null when that person has no equivalent.
 *   At least one is not null.
 */
data class Equivalence(
    val type: EventType,
    val user: EquivalenceSide,
    val others: List<EquivalenceSide?>,
)
