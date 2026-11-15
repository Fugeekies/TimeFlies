package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.YearMonth

/**
 * Ends the first launch screen (docs/UISpec/Onboarding.template.md); it does not come back afterwards.
 * It asks for the birth only: the age of the other generation is set on the Equivalences screen
 * (decision of 15/11/2026).
 */
class CompleteOnboarding(
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    /**
     * "C'est parti": saves the birth month and year and marks the first launch as done, in one write.
     *
     * @return false, and nothing saved, when the birth year is outside the list.
     */
    suspend fun start(birth: YearMonth): Boolean {
        if (birth.year !in Generations.birthYears(clock.today())) return false
        settings.update { it.copy(birth = birth, onboardingDone = true) }
        return true
    }

    /** "Plus tard": marks the first launch as done without a birth (reduced mode). */
    suspend fun later() {
        settings.update { it.copy(birth = null, onboardingDone = true) }
    }
}
