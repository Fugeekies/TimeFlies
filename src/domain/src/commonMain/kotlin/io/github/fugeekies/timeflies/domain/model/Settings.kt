package io.github.fugeekies.timeflies.domain.model

import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.YearMonth

/**
 * The user's settings, kept on the device (docs/UISpec/Settings.template.md). Defaults are those of a first launch.
 *
 * @property birth the user's birth month and year, never the day (decision of 13/11/2026); null when not given.
 * @property youngAge age of the young person the user compares themselves with, from 5 to 60.
 * @property region region whose event dates are used (France only for now, decision of 15/11/2026).
 * @property onboardingDone the first launch screen was completed or skipped ("Plus tard"); it does not come back.
 */
data class Settings(
    val birth: YearMonth? = null,
    val youngAge: Int = Generations.DEFAULT_YOUNG_AGE,
    val region: Region = Region.FRANCE,
    val onboardingDone: Boolean = false,
)
