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
import io.github.fugeekies.timeflies.ui.text.AppLocale
import io.github.fugeekies.timeflies.ui.text.ShockMessages
import io.github.fugeekies.timeflies.ui.text.apply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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
    // One instance only, checked before anything opens the settings file (decision of 16/11/2026).
    val showRequests = MutableStateFlow(0)
    if (!SingleInstance.acquire(AppFolder.path().toFile(), showWindow = !background) { showRequests.update { it + 1 } }) return
    val graph = AppGraph(debug)
    val trayUpdater = TrayUpdater(graph.sendLaunchNotification, graph.observeSettings, graph.clock, graph.releaseUpdates, graph.personalEvents)
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
            // The menu is written in the language chosen in Settings (milestone 17), read before the icon is made.
            AppLocale.apply(graph.observeSettings().first().language)
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
        // TimeFlies opened again from the Start menu or a shortcut: this window comes back instead of a second app.
        LaunchedEffect(Unit) {
            showRequests.collect { if (it > 0) show(if (visible) null else AppScreen.TODAY) }
        }
        // Without a notification area, the window must never stay hidden, and the tray loop does not look for
        // new versions: the app does it once here.
        LaunchedEffect(trayReady) {
            if (trayReady && tray == null) {
                visible = true
                runCatching { graph.releaseUpdates.checkIfDue() }
            }
        }
        DisposableEffect(tray) {
            // The icon of this key: reading the state in onDispose would give the new icon and remove it at once.
            val current = tray
            onDispose { current?.remove() }
        }
        LaunchedEffect(tray) {
            val current = tray ?: return@LaunchedEffect
            withContext(Dispatchers.Default) { trayUpdater.run(current) }
        }
        // Another language chosen in Settings: the menu follows (the tooltip follows in the tray loop).
        val language = settings?.language
        LaunchedEffect(tray, language) {
            val current = tray ?: return@LaunchedEffect
            AppLocale.apply(language)
            current.setMenuTexts(ShockMessages.trayMenu())
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
                // A debug run quits when its window closes: hidden in the tray, it would keep the debugger's port
                // and the next launch from the IDE would fail at once (docs/DEVELOPMENT.md).
                if (!debug && current != null && windows?.keepInTray != false) {
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
                useCases = graph.useCases,
                debugTools = graph.debugTools,
                // Given by the release build only (desktopApp/build.gradle.kts); null says "version de développement".
                appVersion = System.getProperty("timeflies.version"),
                releaseUpdates = graph.releaseUpdates,
                platform = PlatformHooks(
                    screenRequest = screenRequest,
                    onScreenRequestHandled = { screenRequest = null },
                    showWindowsPanel = tray != null,
                    sendNotification = tray?.let { current -> { scope.launch { trayUpdater.sendNow(current) } } },
                    files = remember(window) { DesktopFileExchange(window) },
                ),
            )
        }
    }
}
