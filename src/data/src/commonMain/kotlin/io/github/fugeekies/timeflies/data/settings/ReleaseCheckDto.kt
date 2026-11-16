package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.AppRelease
import io.github.fugeekies.timeflies.domain.model.ReleaseCheck
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/** The check for new versions as stored in `settings.json` (version 4, decision of 16/11/2026). */
@Serializable
internal data class ReleaseCheckDto(
    val enabled: Boolean = true,
    val lastCheckDate: String? = null,
    val latestVersion: String? = null,
    val latestUrl: String? = null,
    val dismissedVersion: String? = null,
) {
    fun toDomain() = ReleaseCheck(
        enabled = enabled,
        lastCheck = lastCheckDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        latest = if (latestVersion != null && latestUrl != null) AppRelease(latestVersion, latestUrl) else null,
        dismissedVersion = dismissedVersion,
    )

    companion object {
        fun of(check: ReleaseCheck) = ReleaseCheckDto(
            enabled = check.enabled,
            lastCheckDate = check.lastCheck?.toString(),
            latestVersion = check.latest?.version,
            latestUrl = check.latest?.url,
            dismissedVersion = check.dismissedVersion,
        )
    }
}
