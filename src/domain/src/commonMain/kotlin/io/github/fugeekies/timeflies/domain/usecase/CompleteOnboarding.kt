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
     * "C'est parti": saves the birth month and year, the event groups unticked on the first launch screen, and
     * marks the first launch as done, in one write.
     *
     * @return false, and nothing saved, when the birth year is outside the list.
     */
    suspend fun start(birth: YearMonth, excludedEventGroups: Set<String> = emptySet()): Boolean {
        if (birth.year !in Generations.birthYears(clock.today())) return false
        settings.update { it.copy(birth = birth, excludedEventGroups = excludedEventGroups, onboardingDone = true) }
        return true
    }

    /** "Plus tard": marks the first launch as done without a birth (reduced mode), keeping the unticked groups. */
    suspend fun later(excludedEventGroups: Set<String> = emptySet()) {
        settings.update { it.copy(birth = null, excludedEventGroups = excludedEventGroups, onboardingDone = true) }
    }
}
