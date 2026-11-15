package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate

/**
 * Which way the Mon équivalent screen looks (docs/UISpec/MyEquivalent.template.md, decision of 15/11/2026): the
 * chosen event belongs to the life of the first person, its equivalent is looked for in the life of the second.
 */
enum class EquivalentDirection {
    /** The event is in the other person's life; its equivalent is looked for in the user's (the default). */
    OTHER_TO_USER,

    /** The event is in the user's life; its equivalent is looked for in the other person's. */
    USER_TO_OTHER,
    ;

    fun reversed(): EquivalentDirection = if (this == OTHER_TO_USER) USER_TO_OTHER else OTHER_TO_USER
}

/**
 * An event of the same type as the chosen one, with how far it is from the target date.
 *
 * @property offset from the target date to [EquivalenceSide.date]: negative when the candidate is earlier.
 */
data class EquivalentCandidate(
    val side: EquivalenceSide,
    val offset: DatePeriod,
)

/**
 * The result of Mon équivalent for one chosen event (docs/UISpec/MyEquivalent.template.md, "Résultat").
 *
 * @property chosen the chosen event, with the age of its person.
 * @property target the day the second person had the age the first one had at [chosen]'s date.
 * @property best the equivalent: within the tolerance, the closest to [target], then the most important; null if none.
 * @property others the next candidates within the tolerance, at most 3, the closest first.
 * @property outside when there is no equivalent: the closest events of the same type outside the tolerance, at most 3.
 */
data class EquivalentSearch(
    val chosen: EquivalenceSide,
    val target: LocalDate,
    val best: EquivalentCandidate?,
    val others: List<EquivalentCandidate>,
    val outside: List<EquivalentCandidate>,
) {
    /** The target date is in the future: no equivalent yet (docs/EVENTS_DATA.md). */
    fun isTargetInFuture(today: LocalDate): Boolean = target > today
}
