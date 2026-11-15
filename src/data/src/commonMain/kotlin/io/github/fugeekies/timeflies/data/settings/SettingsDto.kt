package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.serialization.Serializable

/**
 * The settings as stored in `settings.json` (docs/ARCHITECTURE.md, "Réglages"). A missing field takes its default,
 * so a file written by an older version still reads; [version] is there for future migrations.
 */
@Serializable
internal data class SettingsDto(
    val version: Int = CURRENT_VERSION,
    val birthYear: Int? = null,
    val birthMonth: Int? = null,
    val youngAge: Int = Generations.DEFAULT_YOUNG_AGE,
    val region: String = Region.FRANCE.code,
) {
    /** The domain settings; values edited by hand outside their range fall back to safe ones. */
    fun toDomain(): Settings = Settings(
        birth = if (birthYear != null && birthMonth in 1..12) YearMonth(birthYear, Month(birthMonth!!)) else null,
        youngAge = youngAge.coerceIn(Generations.MIN_YOUNG_AGE, Generations.MAX_YOUNG_AGE),
        region = Region.of(region) ?: Region.FRANCE,
    )

    companion object {
        const val CURRENT_VERSION = 1

        fun of(settings: Settings) = SettingsDto(
            birthYear = settings.birth?.year,
            birthMonth = settings.birth?.month?.number,
            youngAge = settings.youngAge,
            region = settings.region.code,
        )
    }
}
