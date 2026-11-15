package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.YearMonth

/** Ends the first launch screen (docs/UISpec/Onboarding.template.md); it does not come back afterwards. */
class CompleteOnboarding(
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    /**
     * "C'est parti": saves the birth month and year and the young age, and marks the first launch as done,
     * in one write.
     *
     * @return false, and nothing saved, when the birth year is outside the list or the young age is not allowed
     *   ([Generations.isYoungAgeAllowed]): the button is disabled in that case.
     */
    suspend fun start(birth: YearMonth, youngAge: Int): Boolean {
        val today = clock.today()
        if (birth.year !in Generations.birthYears(today) || !Generations.isYoungAgeAllowed(youngAge, birth, today)) return false
        settings.update { it.copy(birth = birth, youngAge = youngAge, onboardingDone = true) }
        return true
    }

    /**
     * "Plus tard": marks the first launch as done without a birth (reduced mode). The young age chosen on the
     * slider is kept when it is within 5 to 60.
     */
    suspend fun later(youngAge: Int) {
        val today = clock.today()
        settings.update {
            val age = if (Generations.isYoungAgeAllowed(youngAge, null, today)) youngAge else it.youngAge
            it.copy(birth = null, youngAge = age, onboardingDone = true)
        }
    }
}
