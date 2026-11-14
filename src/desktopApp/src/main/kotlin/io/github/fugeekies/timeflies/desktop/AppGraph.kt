package io.github.fugeekies.timeflies.desktop

import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.data.time.SystemClock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.ui.readResourceFile

/** Composition root of the Windows app: builds the data layer and the use cases once (docs/ARCHITECTURE.md). */
class AppGraph {
    val clock: Clock = SystemClock()

    private val events = JsonEventRepository(
        readFile = ::readResourceFile,
        clock = clock,
        onRejection = { System.err.println("Événement écarté : $it") },
    )

    val getDailyShock = GetDailyShock(events, clock)
}
