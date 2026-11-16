package io.github.fugeekies.timeflies.desktop

import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.data.debug.DataDebugTools
import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.data.settings.DataStoreSettingsRepository
import io.github.fugeekies.timeflies.data.time.AdjustableClock
import io.github.fugeekies.timeflies.data.time.SystemClock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.DebugTools
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetEventGroups
import kotlinx.coroutines.flow.first
import io.github.fugeekies.timeflies.domain.usecase.ReleaseUpdates
import io.github.fugeekies.timeflies.data.release.GitHubReleases
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.GetEventAges
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.usecase.SendMorningNotification
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.readResourceFile
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Composition root of the Windows app: builds the data layer and the use cases once (docs/ARCHITECTURE.md).
 *
 * @param debug launched with `-Dtimeflies.debug=true`: fake date and debug panel (docs/DEVELOPMENT.md).
 */
class AppGraph(debug: Boolean) {
    private val log = Logger.withTag("Data")

    private val adjustableClock = if (debug) AdjustableClock(SystemClock()) else null
    val clock: Clock = adjustableClock ?: SystemClock()

    private val events = JsonEventRepository(
        readFile = ::readResourceFile,
        clock = clock,
        // The groups unticked in Settings are left out of every query (decision of 16/11/2026).
        excludedGroups = { settings.settings.first().excludedEventGroups },
        onRejection = { log.w { "Événement écarté : $it" } },
    )

    private val settings = DataStoreSettingsRepository(
        fileSystem = FileSystem.SYSTEM,
        file = AppFolder.path() / "settings.json",
        // The class name only: the message of a parsing error may quote the file, birth included.
        onCorruption = { log.w { "settings.json illisible (${it::class.simpleName}) : valeurs par défaut" } },
    )

    val getDailyShock = GetDailyShock(events, clock)
    val getEquivalences = GetEquivalences(events, clock)
    val getEventAges = GetEventAges()
    val searchEvents = SearchEvents(events, clock)
    val observeSettings = ObserveSettings(settings)
    val updateSettings = UpdateSettings(settings, clock)
    val completeOnboarding = CompleteOnboarding(settings, clock)
    val countEvents = CountEvents(events)
    val getEventGroups = GetEventGroups(events)
    // New versions on GitHub (decision of 16/11/2026); a local build has no version and never asks.
    val releaseUpdates = ReleaseUpdates(GitHubReleases(::httpText), settings, clock, System.getProperty("timeflies.version"))
    val sendMorningNotification = SendMorningNotification(getDailyShock, settings, clock)
    val debugTools: DebugTools? = adjustableClock?.let { DataDebugTools(it, settings, events) }
}

/** `%APPDATA%\TimeFlies`: settings and logs, kept after an uninstall (docs/ARCHITECTURE.md, "Réglages"). */
internal object AppFolder {
    fun path(): Path {
        val base = System.getenv("APPDATA") ?: System.getProperty("user.home")
        return base.toPath() / "TimeFlies"
    }
}
