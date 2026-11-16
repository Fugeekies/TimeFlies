package io.github.fugeekies.timeflies.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.DebugTools
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.GetEventAges
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.debug.DebugScreen
import io.github.fugeekies.timeflies.ui.debug.DebugViewModel
import io.github.fugeekies.timeflies.ui.equivalences.EquivalencesScreen
import io.github.fugeekies.timeflies.ui.equivalences.EquivalencesViewModel
import io.github.fugeekies.timeflies.ui.equivalences.TemporaryAgesStore
import io.github.fugeekies.timeflies.ui.eventages.EventAgesScreen
import io.github.fugeekies.timeflies.ui.eventages.EventAgesViewModel
import io.github.fugeekies.timeflies.ui.onboarding.OnboardingScreen
import io.github.fugeekies.timeflies.ui.onboarding.OnboardingViewModel
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.nav_debug
import io.github.fugeekies.timeflies.ui.resources.nav_equivalences
import io.github.fugeekies.timeflies.ui.resources.nav_event_ages
import io.github.fugeekies.timeflies.ui.resources.nav_settings
import io.github.fugeekies.timeflies.ui.resources.nav_today
import io.github.fugeekies.timeflies.ui.settings.SettingsScreen
import io.github.fugeekies.timeflies.ui.settings.SettingsViewModel
import io.github.fugeekies.timeflies.ui.theme.TimeFliesIcons
import io.github.fugeekies.timeflies.ui.theme.ThemeMode
import io.github.fugeekies.timeflies.ui.theme.TimeFliesTheme
import io.github.fugeekies.timeflies.ui.today.TodayScreen
import io.github.fugeekies.timeflies.ui.today.TodayViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The four destinations (docs/UI_DESIGN_SPEC.md, "Navigation"). */
private enum class Destination(val label: StringResource, val icon: ImageVector) {
    TODAY(Res.string.nav_today, TimeFliesIcons.Calendar),
    EQUIVALENCES(Res.string.nav_equivalences, TimeFliesIcons.Compare),
    EVENT_AGES(Res.string.nav_event_ages, TimeFliesIcons.Search),
    SETTINGS(Res.string.nav_settings, TimeFliesIcons.Tune),

    /** Debug builds only (docs/DEVELOPMENT.md, "Panneau de débogage"). */
    DEBUG(Res.string.nav_debug, TimeFliesIcons.Debug),
}

/** A screen another part of the app asks to open (the Windows tray icon and its menu). */
enum class AppScreen { TODAY, SETTINGS }

/**
 * What only one platform adds to the shared screens; the defaults add nothing (Android for now).
 *
 * @property screenRequest a screen to open, asked from outside the window; [onScreenRequestHandled] clears it.
 * @property showWindowsPanel Settings shows the "Notification et démarrage" panel.
 * @property sendMorningNotification the debug panel offers to send the morning notification at once.
 * @property widget Android: Settings shows the "Écran d'accueil" panel; null elsewhere.
 * @property refreshWidget the debug panel offers to update the Android widget at once.
 */
data class PlatformHooks(
    val screenRequest: AppScreen? = null,
    val onScreenRequestHandled: () -> Unit = {},
    val showWindowsPanel: Boolean = false,
    val sendMorningNotification: (() -> Unit)? = null,
    val widget: WidgetPinning? = null,
    val refreshWidget: (() -> Unit)? = null,
)

/**
 * The "Ajouter le widget" button of Settings (docs/UISpec/Settings.template.md, "Widget").
 *
 * @property pin asks the launcher to pin the widget; null when the launcher does not allow it (the screen then
 *   explains how to add it by hand).
 */
data class WidgetPinning(val pin: (() -> Unit)?)

/**
 * Root composable shared by the Android and Windows apps: the first launch screen until it is completed or
 * skipped, then the theme and adaptive navigation (bottom bar on a phone, rail on a wide window). The use cases
 * come from the app's composition root.
 *
 * @param appVersion version shown in Settings › À propos; null for a local development build.
 * @param debugTools tools of the debug panel; null in a release, where the panel does not exist.
 * @param platform what only one platform adds (the Windows tray, for now).
 */
@Composable
fun App(
    getDailyShock: GetDailyShock,
    getEquivalences: GetEquivalences,
    getEventAges: GetEventAges,
    searchEvents: SearchEvents,
    observeSettings: ObserveSettings,
    updateSettings: UpdateSettings,
    completeOnboarding: CompleteOnboarding,
    countEvents: CountEvents,
    clock: Clock,
    appVersion: String? = null,
    debugTools: DebugTools? = null,
    platform: PlatformHooks = PlatformHooks(),
) {
    // Forced light or dark from the debug panel, for this session only.
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    TimeFliesTheme(darkTheme = themeMode.isDark()) {
        val settingsFlow = remember(observeSettings) { observeSettings() }
        val settings by settingsFlow.collectAsStateWithLifecycle(initialValue = null)
        val content = Modifier.fillMaxSize().safeDrawingPadding()
        when (val current = settings) {
            // Settings not read yet (a few milliseconds): a plain background rather than a flash of the wrong screen.
            null -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
            else -> if (!current.onboardingDone) {
                // A screen asked from the tray waits for nothing: the first launch screen comes first.
                LaunchedEffect(platform.screenRequest) { if (platform.screenRequest != null) platform.onScreenRequestHandled() }
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    OnboardingScreen(viewModel { OnboardingViewModel(completeOnboarding, clock) }, content)
                }
            } else {
                MainScreens(
                    getDailyShock, getEquivalences, getEventAges, searchEvents, observeSettings, updateSettings, countEvents, clock,
                    appVersion, debugTools, platform, themeMode, onThemeMode = { themeMode = it },
                )
            }
        }
    }
}

@Composable
private fun MainScreens(
    getDailyShock: GetDailyShock,
    getEquivalences: GetEquivalences,
    getEventAges: GetEventAges,
    searchEvents: SearchEvents,
    observeSettings: ObserveSettings,
    updateSettings: UpdateSettings,
    countEvents: CountEvents,
    clock: Clock,
    appVersion: String?,
    debugTools: DebugTools?,
    platform: PlatformHooks,
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
) {
    var current by rememberSaveable { mutableStateOf(Destination.TODAY) }
    LaunchedEffect(platform.screenRequest) {
        when (platform.screenRequest) {
            AppScreen.TODAY -> current = Destination.TODAY
            AppScreen.SETTINGS -> current = Destination.SETTINGS
            null -> return@LaunchedEffect
        }
        platform.onScreenRequestHandled()
    }
    // Ages picked on the fly, shared by Equivalences and "Quel âge ?", forgotten when the app closes.
    val temporaryAges = remember { TemporaryAgesStore() }
    // A card touched on Today or Equivalences: "Quel âge ?" opens on its event.
    var eventRequest by remember { mutableStateOf<Event?>(null) }
    val openEvent = { event: Event ->
        eventRequest = event
        current = Destination.EVENT_AGES
    }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            Destination.entries.filter { it != Destination.DEBUG || debugTools != null }.forEach { destination ->
                item(
                    selected = destination == current,
                    onClick = { current = destination },
                    icon = { Icon(destination.icon, contentDescription = null) },
                    label = { Text(stringResource(destination.label)) },
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        val content = Modifier.fillMaxSize().safeDrawingPadding()
        when (current) {
            Destination.TODAY -> TodayScreen(
                viewModel { TodayViewModel(getDailyShock, observeSettings, clock) },
                onOpenEvent = openEvent,
                modifier = content,
            )
            Destination.SETTINGS -> SettingsScreen(
                viewModel { SettingsViewModel(observeSettings, updateSettings, countEvents, clock) },
                appVersion,
                content,
                showWindowsPanel = platform.showWindowsPanel,
                widget = platform.widget,
            )
            // Their reduced mode (without birth) also offers ages picked on the fly.
            Destination.EQUIVALENCES -> EquivalencesScreen(
                viewModel { EquivalencesViewModel(getEquivalences, observeSettings, updateSettings, clock, temporaryAges) },
                onOpenSettings = { current = Destination.SETTINGS },
                onOpenEvent = openEvent,
                modifier = content,
            )
            Destination.EVENT_AGES -> EventAgesScreen(
                viewModel { EventAgesViewModel(searchEvents, getEventAges, observeSettings, updateSettings, clock, temporaryAges) },
                request = eventRequest,
                onRequestHandled = { eventRequest = null },
                onOpenSettings = { current = Destination.SETTINGS },
                modifier = content,
            )
            Destination.DEBUG -> debugTools?.let { tools ->
                DebugScreen(
                    viewModel { DebugViewModel(tools, observeSettings, clock) },
                    themeMode,
                    onThemeMode,
                    content,
                    onSendMorningNotification = platform.sendMorningNotification,
                    onRefreshWidget = platform.refreshWidget,
                )
            }
        }
    }
}
