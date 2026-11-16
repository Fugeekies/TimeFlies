package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.AppRelease
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.ReleaseSource
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Versions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * New versions (decision of 16/11/2026): GitHub is asked at most once a day, at start, and the Today screen shows a
 * banner while a newer version than the running one exists and was not put off with "Plus tard".
 *
 * @param currentVersion the running version; null for a local build, which never looks for anything.
 */
class ReleaseUpdates(
    private val source: ReleaseSource,
    private val settings: SettingsRepository,
    private val clock: Clock,
    private val currentVersion: String?,
) {
    /** Asks GitHub if it was not asked today and the check is on; keeps the answer in the settings. */
    suspend fun checkIfDue() {
        if (currentVersion == null) return
        val today = clock.today()
        val check = settings.settings.first().releaseCheck
        if (!check.enabled || check.lastCheck == today) return
        // Offline or GitHub unreachable: the day is marked anyway, so the app does not ask again and again.
        val latest = source.latest()
        settings.update { it.copy(releaseCheck = it.releaseCheck.copy(lastCheck = today, latest = latest ?: it.releaseCheck.latest)) }
    }

    /** The version to offer on the banner, or null. */
    fun banner(): Flow<AppRelease?> = settings.settings.map { it.releaseCheck }.map { check ->
        val latest = check.latest
        when {
            currentVersion == null || !check.enabled || latest == null -> null
            !Versions.isNewer(latest.version, currentVersion) -> null
            latest.version == check.dismissedVersion -> null
            else -> latest
        }
    }.distinctUntilChanged()

    /** "Plus tard": the banner of [version] does not come back; the next version will show one. */
    suspend fun dismiss(version: String) {
        settings.update { it.copy(releaseCheck = it.releaseCheck.copy(dismissedVersion = version)) }
    }
}
