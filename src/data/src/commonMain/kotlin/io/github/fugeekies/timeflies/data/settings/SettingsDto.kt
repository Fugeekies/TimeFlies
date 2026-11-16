package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.rules.Generations
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * The settings as stored in `settings.json` (docs/ARCHITECTURE.md, "Réglages"). A missing field takes its default,
 * so a file written by an older version still reads; [version] is there for future migrations.
 *
 * Version 2 (milestone 7) renamed `youngAge` to [otherAge] (the other person can be older, decision of 15/11/2026):
 * a version 1 file is read through [youngAge], which is never written again. Version 3 (milestone 10) added the
 * Windows panel ([windows]); version 4 the check for new versions ([releaseCheck]). An older file gets their defaults.
 */
@Serializable
internal data class SettingsDto(
    val version: Int = CURRENT_VERSION,
    val birthYear: Int? = null,
    val birthMonth: Int? = null,
    val otherAge: Int? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val youngAge: Int? = null,
    val region: String = Region.FRANCE.code,
    val onboardingDone: Boolean = false,
    val equivalencesReversed: Boolean = false,
    /** Ticked themes by family id; null (or a file without the field) means all of them. */
    val equivalenceFamilies: List<String>? = null,
    /** Version 3: the Windows panel, ignored on Android. */
    val windows: WindowsSettingsDto = WindowsSettingsDto(),
    /** Version 4: the check for new versions. */
    val releaseCheck: ReleaseCheckDto = ReleaseCheckDto(),
) {
    /** The domain settings; values edited by hand outside their range fall back to safe ones. */
    fun toDomain(): Settings = Settings(
        birth = if (birthYear != null && birthMonth in 1..12) YearMonth(birthYear, Month(birthMonth!!)) else null,
        otherAge = (otherAge ?: youngAge ?: Generations.DEFAULT_OTHER_AGE)
            .coerceIn(Generations.MIN_OTHER_AGE, Generations.MAX_OTHER_AGE),
        region = Region.of(region) ?: Region.FRANCE,
        onboardingDone = onboardingDone,
        equivalencesReversed = equivalencesReversed,
        equivalenceFamilies = equivalenceFamilies?.mapNotNull(::familyOf)?.toSet() ?: Family.entries.toSet(),
        windows = windows.toDomain(),
        releaseCheck = releaseCheck.toDomain(),
    )

    companion object {
        const val CURRENT_VERSION = 4

        fun of(settings: Settings) = SettingsDto(
            birthYear = settings.birth?.year,
            birthMonth = settings.birth?.month?.number,
            otherAge = settings.otherAge,
            region = settings.region.code,
            onboardingDone = settings.onboardingDone,
            equivalencesReversed = settings.equivalencesReversed,
            equivalenceFamilies = Family.entries.filter { it in settings.equivalenceFamilies }.map(::idOf),
            windows = WindowsSettingsDto.of(settings.windows),
            releaseCheck = ReleaseCheckDto.of(settings.releaseCheck),
        )

        /** "series", "cinema", "games-tech"…: the name of the family's event file, readable by hand. */
        private fun idOf(family: Family): String = family.fileName.removeSuffix(".json")

        private fun familyOf(id: String): Family? = Family.entries.firstOrNull { idOf(it) == id }
    }
}
