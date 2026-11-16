package io.github.fugeekies.timeflies.desktop

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import io.github.fugeekies.timeflies.ui.AppScreen
import io.github.fugeekies.timeflies.ui.PlatformHooks
import io.github.fugeekies.timeflies.ui.text.ShockMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.awt.Dimension

/** The winged hourglass, for the window and the taskbar (assets/icon/timeflies-256.png, copied at build time). */
private val appIcon: Painter by lazy {
    val bytes = checkNotNull(object {}.javaClass.getResourceAsStream("/icon/timeflies-256.png")) { "icon/timeflies-256.png is missing" }
        .use { it.readBytes() }
    BitmapPainter(Image.makeFromEncoded(bytes).toComposeImageBitmap())
}

fun main(args: Array<String>) {
    // Debug panel and fake date: `./gradlew :desktopApp:run '-Dtimeflies.debug=true'`, never in the installer.
    val debug = System.getProperty("timeflies.debug") == "true"
    // Launched by Windows at startup: tray icon only, window hidden (docs/UISpec/WindowsTray.template.md).
    val background = WindowsStartup.BACKGROUND_ARGUMENT in args
    Logging.setUp(debug)
    Logger.withTag("App").i { "TimeFlies démarre${if (debug) " (débogage)" else ""}${if (background) " (au démarrage de Windows)" else ""}" }
    val graph = AppGraph(debug)
    val trayUpdater = TrayUpdater(graph.sendMorningNotification, graph.observeSettings, graph.clock)
    application {
        val scope = rememberCoroutineScope()
        val settings by remember { graph.observeSettings() }.collectAsState(initial = null)
        var tray by remember { mutableStateOf<WindowsTray?>(null) }
        var trayReady by remember { mutableStateOf(false) }
        var visible by remember { mutableStateOf(!background) }
        var screenRequest by remember { mutableStateOf<AppScreen?>(null) }
        var frontRequests by remember { mutableIntStateOf(0) }
        val windowState = rememberWindowState(size = DpSize(1000.dp, 700.dp))

        /** Shows the window and brings it to the front; [screen] is opened, if any. */
        fun show(screen: AppScreen?) {
            visible = true
            windowState.isMinimized = false
            screen?.let { screenRequest = it }
            frontRequests++
        }

        LaunchedEffect(Unit) {
            tray = WindowsTray.create(
                texts = ShockMessages.trayMenu(),
                tooltip = "TimeFlies",
                // Left click: opens the window on Today, or only brings it to the front when it is open.
                onOpen = { show(if (visible) null else AppScreen.TODAY) },
                onNotificationClicked = { show(AppScreen.TODAY) },
                onDailyShock = { tray?.let { scope.launch { trayUpdater.sendNow(it) } } },
                onSettings = { show(AppScreen.SETTINGS) },
                onQuit = ::exitApplication,
            )
            trayReady = true
        }
        // Without a notification area, the window must never stay hidden.
        LaunchedEffect(trayReady) { if (trayReady && tray == null) visible = true }
        DisposableEffect(tray) { onDispose { tray?.remove() } }
        LaunchedEffect(tray) {
            val current = tray ?: return@LaunchedEffect
            withContext(Dispatchers.Default) { trayUpdater.run(current) }
        }
        // The startup entry follows the setting, rewritten at each launch in case the app moved.
        LaunchedEffect(Unit) {
            graph.observeSettings().map { it.windows.launchAtStartup }.distinctUntilChanged().collect { enabled ->
                withContext(Dispatchers.IO) { WindowsStartup.apply(enabled) }
            }
        }

        Window(
            onCloseRequest = {
                val windows = settings?.windows
                val current = tray
                if (current != null && windows?.keepInTray != false) {
                    visible = false
                    if (windows?.trayHintShown == false) {
                        scope.launch {
                            val hint = ShockMessages.trayHint()
                            current.notify(hint.title, hint.text)
                            graph.updateSettings.markTrayHintShown()
                        }
                    }
                } else {
                    exitApplication()
                }
            },
            visible = visible,
            title = "TimeFlies",
            icon = appIcon,
            state = windowState,
        ) {
            window.minimumSize = Dimension(420, 640)
            LaunchedEffect(frontRequests) {
                if (frontRequests > 0) {
                    window.toFront()
                    window.requestFocus()
                }
            }
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
                platform = PlatformHooks(
                    screenRequest = screenRequest,
                    onScreenRequestHandled = { screenRequest = null },
                    showWindowsPanel = tray != null,
                    sendMorningNotification = tray?.let { current -> { scope.launch { trayUpdater.sendNow(current) } } },
                ),
            )
        }
    }
}
