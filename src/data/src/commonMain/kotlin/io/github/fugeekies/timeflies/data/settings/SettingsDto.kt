package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.serialization.Serializable

/**
 * The settings as stored in `settings.json` (docs/ARCHITECTURE.md, "Réglages"). A missing field takes its default,
 * so a file written by an older version still reads; [version] is there for future migrations.
 *
 * Version 2 (milestone 7) renamed `youngAge` to `otherAge` (the other person can be older, decision of 15/11/2026).
 * Version 3 (milestone 10) added the Windows panel ([windows]); version 4 the check for new versions ([releaseCheck]);
 * version 5 the event filter ([excludedEventGroups]); version 6 the display style ([style], decision of 17/11/2026).
 * An older file gets their defaults. Version 7 (milestone 16, decision of 17/11/2026) dropped `otherAge` and
 * `equivalencesReversed`: the other ages are forgotten when the app closes and the user's column comes first; an older
 * file still reads, those fields being ignored. Version 8 (milestone 17) added the language of the interface
 * ([language]).
 */
@Serializable
internal data class SettingsDto(
    val version: Int = CURRENT_VERSION,
    val birthYear: Int? = null,
    val birthMonth: Int? = null,
    val region: String = Region.FRANCE.code,
    val onboardingDone: Boolean = false,
    /** Ticked themes by family id; null (or a file without the field) means all of them. */
    val equivalenceFamilies: List<String>? = null,
    /** Version 3: the Windows panel, ignored on Android. */
    val windows: WindowsSettingsDto = WindowsSettingsDto(),
    /** Version 4: the check for new versions. */
    val releaseCheck: ReleaseCheckDto = ReleaseCheckDto(),
    /** Version 5: paths of the unticked event groups ("music/rap-album-hit/us"). */
    val excludedEventGroups: List<String> = emptyList(),
    /** Version 6: the display style ("polaroid", "vhs", "ephemeride" since milestone 15); an unknown value falls back to the Polaroïd. */
    val style: String = DisplayStyle.POLAROID.id,
    /** Version 8: the language chosen in Settings ("fr", "en", "de", "it", "es"); null or unknown: the system's. */
    val language: String? = null,
) {
    /** The domain settings; values edited by hand outside their range fall back to safe ones. */
    fun toDomain(): Settings = Settings(
        birth = if (birthYear != null && birthMonth in 1..12) YearMonth(birthYear, Month(birthMonth!!)) else null,
        region = Region.of(region) ?: Region.FRANCE,
        onboardingDone = onboardingDone,
        equivalenceFamilies = equivalenceFamilies?.mapNotNull { id -> Family.THEMES.firstOrNull { it.id == id } }?.toSet() ?: Family.THEMES.toSet(),
        windows = windows.toDomain(),
        releaseCheck = releaseCheck.toDomain(),
        excludedEventGroups = excludedEventGroups.toSet(),
        displayStyle = DisplayStyle.of(style) ?: DisplayStyle.POLAROID,
        language = language?.let(AppLanguage::of),
    )

    companion object {
        const val CURRENT_VERSION = 8

        fun of(settings: Settings) = SettingsDto(
            birthYear = settings.birth?.year,
            birthMonth = settings.birth?.month?.number,
            region = settings.region.code,
            onboardingDone = settings.onboardingDone,
            // "series", "cinema", "games-tech"…: the folder names of the base, readable by hand.
            equivalenceFamilies = Family.THEMES.filter { it in settings.equivalenceFamilies }.map { it.id },
            windows = WindowsSettingsDto.of(settings.windows),
            releaseCheck = ReleaseCheckDto.of(settings.releaseCheck),
            excludedEventGroups = settings.excludedEventGroups.sorted(),
            style = settings.displayStyle.id,
            language = settings.language?.tag,
        )
    }
}
