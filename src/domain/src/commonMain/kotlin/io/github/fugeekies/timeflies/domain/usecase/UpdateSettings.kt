package io.github.fugeekies.timeflies.domain.usecase

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
     * Saves the user's birth month and year. A young age that is no longer 5 years younger is kept
     * (decision of 15/11/2026): the Settings screen shows the message.
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
     * Saves the young person's age if it is allowed ([Generations.isYoungAgeAllowed]).
     *
     * @return true when saved, false when rejected (the stored value does not change).
     */
    suspend fun setYoungAge(age: Int): Boolean {
        val today = clock.today()
        var saved = false
        settings.update { current ->
            saved = Generations.isYoungAgeAllowed(age, current.birth, today)
            if (saved) current.copy(youngAge = age) else current
        }
        return saved
    }
}
