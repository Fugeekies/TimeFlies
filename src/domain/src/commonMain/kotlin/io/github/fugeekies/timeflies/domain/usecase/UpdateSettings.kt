package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.YearMonth

/** Changes the settings; each change is saved at once (docs/UISpec/Settings.template.md). */
class UpdateSettings(
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    /**
     * Saves the user's birth month and year. The other person's age is kept even if the gap becomes small:
     * the Equivalences screen warns (decision of 15/11/2026).
     *
     * @throws IllegalArgumentException if the year is outside 1920 to the current year minus 5.
     */
    suspend fun setBirth(birth: YearMonth) {
        require(birth.year in Generations.birthYears(clock.today())) { "Birth year out of range: ${birth.year}" }
        settings.update { it.copy(birth = birth) }
    }

    /** Removes the birth month and year (reduced mode). */
    suspend fun clearBirth() {
        settings.update { it.copy(birth = null) }
    }

    /**
     * Saves the other person's age if it is allowed ([Generations.isOtherAgeAllowed]): from 1 to 100, younger or
     * older than the user. A small gap is allowed too, the screen warns (decision of 15/11/2026).
     *
     * @return true when saved, false when rejected (the stored value does not change).
     */
    suspend fun setOtherAge(age: Int): Boolean {
        if (!Generations.isOtherAgeAllowed(age)) return false
        settings.update { it.copy(otherAge = age) }
        return true
    }

    /** Saves the reading direction of the Equivalences screen (true: the user on the left). */
    suspend fun setEquivalencesReversed(reversed: Boolean) {
        settings.update { it.copy(equivalencesReversed = reversed) }
    }

    /** Saves the themes ticked on the Equivalences screen. */
    suspend fun setEquivalenceFamilies(families: Set<Family>) {
        settings.update { it.copy(equivalenceFamilies = families) }
    }
}
