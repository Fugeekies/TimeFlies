package io.github.fugeekies.timeflies.desktop

import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.data.settings.DataStoreSettingsRepository
import io.github.fugeekies.timeflies.data.time.SystemClock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.readResourceFile
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/** Composition root of the Windows app: builds the data layer and the use cases once (docs/ARCHITECTURE.md). */
class AppGraph {
    val clock: Clock = SystemClock()

    private val events = JsonEventRepository(
        readFile = ::readResourceFile,
        clock = clock,
        onRejection = { System.err.println("Événement écarté : $it") },
    )

    private val settings = DataStoreSettingsRepository(FileSystem.SYSTEM, settingsFile())

    val getDailyShock = GetDailyShock(events, clock)
    val observeSettings = ObserveSettings(settings)
    val updateSettings = UpdateSettings(settings, clock)
    val countEvents = CountEvents(events)

    private companion object {
        /** `%APPDATA%\TimeFlies\settings.json`, kept after an uninstall (docs/ARCHITECTURE.md, "Réglages"). */
        fun settingsFile(): Path {
            val base = System.getenv("APPDATA") ?: System.getProperty("user.home")
            return base.toPath() / "TimeFlies" / "settings.json"
        }
    }
}
