package io.github.fugeekies.timeflies.android

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
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.GetEventAges
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.readResourceFile
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

    private val events = JsonEventRepository(
        readFile = ::readResourceFile,
        clock = clock,
        onRejection = { log.w { "Événement écarté : $it" } },
    )

    private val settings = DataStoreSettingsRepository(
        fileSystem = FileSystem.SYSTEM,
        file = File(filesDir, "settings.json").toOkioPath(),
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
    val debugTools: DebugTools? = adjustableClock?.let { DataDebugTools(it, settings, events) }
}
