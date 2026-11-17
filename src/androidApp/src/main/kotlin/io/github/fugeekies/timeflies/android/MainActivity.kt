package io.github.fugeekies.timeflies.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.fugeekies.timeflies.android.widget.WidgetUpdates
import io.github.fugeekies.timeflies.ui.App
import io.github.fugeekies.timeflies.ui.AppScreen
import io.github.fugeekies.timeflies.ui.PlatformHooks
import io.github.fugeekies.timeflies.ui.WidgetPinning
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    /** A screen asked from outside: the widget opens Today. */
    private var screenRequest by mutableStateOf<AppScreen?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) handle(intent)
        val app = application as TimeFliesApplication
        val graph = app.graph
        val widget = WidgetPinning(pin = if (WidgetUpdates.canPin(this)) ({ WidgetUpdates.pin(this) }) else null)
        val refreshWidget = graph.debugTools?.let { { app.scope.launch { WidgetUpdates.refresh(app) }; Unit } }
        // At most once a day, on opening the app (never from the widget): a new version on GitHub?
        app.scope.launch { graph.releaseUpdates.checkIfDue() }
        setContent {
            App(
                getDailyShock = graph.getDailyShock,
                getEquivalences = graph.getEquivalences,
                getEventAges = graph.getEventAges,
                searchEvents = graph.searchEvents,
                observeSettings = graph.observeSettings,
                updateSettings = graph.updateSettings,
                completeOnboarding = graph.completeOnboarding,
                countEvents = graph.countEvents,
                getEventGroups = graph.getEventGroups,
                clock = graph.clock,
                debugTools = graph.debugTools,
                appVersion = BuildConfig.RELEASE_VERSION,
                releaseUpdates = graph.releaseUpdates,
                platform = PlatformHooks(
                    screenRequest = screenRequest,
                    onScreenRequestHandled = { screenRequest = null },
                    widget = widget,
                    refreshWidget = refreshWidget,
                    onDarkTheme = ::applySystemBars,
                ),
            )
        }
    }

    /**
     * Icons of the status and navigation bars light on dark colours, dark on light ones: the VHS is always dark, even
     * when the system is light. Same scrims as the default of [enableEdgeToEdge].
     */
    private fun applySystemBars(dark: Boolean) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
            navigationBarStyle = SystemBarStyle.auto(LIGHT_SCRIM, DARK_SCRIM) { dark },
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
    }

    private fun handle(intent: Intent?) {
        if (intent?.getStringExtra(EXTRA_SCREEN) == SCREEN_TODAY) screenRequest = AppScreen.TODAY
    }

    companion object {
        const val EXTRA_SCREEN = "io.github.fugeekies.timeflies.SCREEN"
        const val SCREEN_TODAY = "today"

        // The scrims androidx puts behind a three-button navigation bar.
        private val LIGHT_SCRIM = Color.argb(0xE6, 0xFF, 0xFF, 0xFF)
        private val DARK_SCRIM = Color.argb(0x80, 0x1B, 0x1B, 0x1B)
    }
}
