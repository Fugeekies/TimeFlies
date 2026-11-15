package io.github.fugeekies.timeflies.desktop

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.ui.App
import org.jetbrains.skia.Image
import java.awt.Dimension

/** The winged hourglass, for the window and the taskbar (assets/icon/timeflies-256.png, copied at build time). */
private val appIcon: Painter by lazy {
    val bytes = checkNotNull(object {}.javaClass.getResourceAsStream("/icon/timeflies-256.png")) { "icon/timeflies-256.png is missing" }
        .use { it.readBytes() }
    BitmapPainter(Image.makeFromEncoded(bytes).toComposeImageBitmap())
}

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
            icon = appIcon,
            state = rememberWindowState(size = DpSize(1000.dp, 700.dp)),
        ) {
            window.minimumSize = Dimension(420, 640)
            App(
                getDailyShock = graph.getDailyShock,
                getEquivalences = graph.getEquivalences,
                getEventAges = graph.getEventAges,
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
