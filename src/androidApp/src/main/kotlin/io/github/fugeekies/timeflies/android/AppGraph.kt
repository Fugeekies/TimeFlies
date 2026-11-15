package io.github.fugeekies.timeflies.android

import android.util.Log
import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.data.settings.DataStoreSettingsRepository
import io.github.fugeekies.timeflies.data.time.SystemClock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
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
 */
class AppGraph(filesDir: File) {
    val clock: Clock = SystemClock()

    private val events = JsonEventRepository(
        readFile = ::readResourceFile,
        clock = clock,
        onRejection = { Log.w("TimeFlies", "Événement écarté : $it") },
    )

    private val settings = DataStoreSettingsRepository(FileSystem.SYSTEM, File(filesDir, "settings.json").toOkioPath())

    val getDailyShock = GetDailyShock(events, clock)
    val observeSettings = ObserveSettings(settings)
    val updateSettings = UpdateSettings(settings, clock)
    val completeOnboarding = CompleteOnboarding(settings, clock)
    val countEvents = CountEvents(events)
}
