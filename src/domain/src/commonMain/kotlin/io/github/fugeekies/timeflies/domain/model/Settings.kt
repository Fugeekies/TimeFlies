package io.github.fugeekies.timeflies.domain.model

import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.YearMonth

/**
 * The user's settings, kept on the device (docs/UISpec/Settings.template.md). Defaults are those of a first launch.
 *
 * @property birth the user's birth month and year, never the day (decision of 13/11/2026); null when not given.
 * @property otherAge age of the other person the user compares themselves with, from 1 to 100, younger or older
 *   (decision of 15/11/2026). Set on the Equivalences screen only.
 * @property region region whose event dates are used (France only for now, decision of 15/11/2026).
 * @property onboardingDone the first launch screen was completed or skipped ("Plus tard"); it does not come back.
 * @property equivalencesReversed reading direction of the Equivalences screen: false puts the other person on the
 *   left, true puts the user on the left.
 * @property equivalenceFamilies themes ticked on the Equivalences screen (all by default).
 */
data class Settings(
    val birth: YearMonth? = null,
    val otherAge: Int = Generations.DEFAULT_OTHER_AGE,
    val region: Region = Region.FRANCE,
    val onboardingDone: Boolean = false,
    val equivalencesReversed: Boolean = false,
    val equivalenceFamilies: Set<Family> = Family.entries.toSet(),
)
