package io.github.fugeekies.timeflies.android

import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.data.debug.DataDebugTools
import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.data.personal.DataStorePersonalEventRepository
import io.github.fugeekies.timeflies.data.personal.WithPersonalEvents
import io.github.fugeekies.timeflies.data.release.GitHubReleases
import io.github.fugeekies.timeflies.data.settings.DataStoreSettingsRepository
import io.github.fugeekies.timeflies.data.time.AdjustableClock
import io.github.fugeekies.timeflies.data.time.SystemClock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.DebugTools
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.GetEventAges
import io.github.fugeekies.timeflies.domain.usecase.GetEventGroups
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.PersonalEvents
import io.github.fugeekies.timeflies.domain.usecase.ReleaseUpdates
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.AppUseCases
import io.github.fugeekies.timeflies.ui.readResourceFile
import kotlinx.coroutines.flow.first
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.File

/**
 * Composition root of the Android app: builds the data layer and the use cases once (docs/ARCHITECTURE.md).
 *
 * @param filesDir the app's private folder, where `settings.json` is kept (erased on uninstall).
 * @param debug debug build: fake date and debug panel (docs/DEVELOPMENT.md).
 */
class AppGraph(filesDir: File, debug: Boolean) {
    private val log = Logger.withTag("Data")

    private val adjustableClock = if (debug) AdjustableClock(SystemClock()) else null
    val clock: Clock = adjustableClock ?: SystemClock()

    private val baseEvents = JsonEventRepository(
        readFile = ::readResourceFile,
        clock = clock,
        // The groups unticked in Settings are left out of every query (decision of 16/11/2026).
        excludedGroups = { settings.settings.first().excludedEventGroups },
        onRejection = { log.w { "Événement écarté : $it" } },
    )

    private val settings = DataStoreSettingsRepository(
        fileSystem = FileSystem.SYSTEM,
        file = File(filesDir, "settings.json").toOkioPath(),
        // The class name only: the message of a parsing error may quote the file, birth included.
        onCorruption = { log.w { "settings.json illisible (${it::class.simpleName}) : valeurs par défaut" } },
    )

    // The user's own events, next to the settings (decision of 19/11/2026); never their titles in the logs.
    private val personal = DataStorePersonalEventRepository(
        fileSystem = FileSystem.SYSTEM,
        file = File(filesDir, "personal-events.json").toOkioPath(),
        onCorruption = { log.w { "personal-events.json illisible (${it::class.simpleName}) : liste vide" } },
    )

    private val events = WithPersonalEvents(baseEvents, personal)

    val personalEvents = PersonalEvents(personal, clock)
    val getDailyShock = GetDailyShock(events, clock)
    val observeSettings = ObserveSettings(settings)

    /** What the shared screens call. */
    val useCases = AppUseCases(
        getDailyShock = getDailyShock,
        getEquivalences = GetEquivalences(events, clock),
        getEventAges = GetEventAges(),
        searchEvents = SearchEvents(events, clock),
        observeSettings = observeSettings,
        updateSettings = UpdateSettings(settings, clock),
        completeOnboarding = CompleteOnboarding(settings, clock),
        countEvents = CountEvents(events),
        getEventGroups = GetEventGroups(events),
        personalEvents = personalEvents,
        clock = clock,
    )

    // New versions on GitHub (decision of 16/11/2026); a local build has no version and never asks.
    val releaseUpdates = ReleaseUpdates(GitHubReleases(::httpText), settings, clock, BuildConfig.RELEASE_VERSION)
    val debugTools: DebugTools? = adjustableClock?.let { DataDebugTools(it, settings, baseEvents) }
}
