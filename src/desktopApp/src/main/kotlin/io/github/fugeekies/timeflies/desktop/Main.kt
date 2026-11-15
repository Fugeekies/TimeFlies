package io.github.fugeekies.timeflies.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.ui.App
import java.awt.Dimension

fun main() {
    // Debug panel and fake date: `./gradlew :desktopApp:run '-Dtimeflies.debug=true'`, never in the installer.
    val debug = System.getProperty("timeflies.debug") == "true"
    Logging.setUp(debug)
    Logger.withTag("App").i { "TimeFlies démarre${if (debug) " (débogage)" else ""}" }
    val graph = AppGraph(debug)
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "TimeFlies",
            state = rememberWindowState(size = DpSize(1000.dp, 700.dp)),
        ) {
            window.minimumSize = Dimension(420, 640)
            App(
                getDailyShock = graph.getDailyShock,
                getEquivalences = graph.getEquivalences,
                findEquivalent = graph.findEquivalent,
                searchEvents = graph.searchEvents,
                observeSettings = graph.observeSettings,
                updateSettings = graph.updateSettings,
                completeOnboarding = graph.completeOnboarding,
                countEvents = graph.countEvents,
                clock = graph.clock,
                debugTools = graph.debugTools,
            )
        }
    }
}
