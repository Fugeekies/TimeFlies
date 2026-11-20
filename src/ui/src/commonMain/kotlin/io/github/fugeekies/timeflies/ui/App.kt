package io.github.fugeekies.timeflies.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.port.DebugTools
import io.github.fugeekies.timeflies.domain.usecase.ReleaseUpdates
import io.github.fugeekies.timeflies.ui.debug.DebugScreen
import io.github.fugeekies.timeflies.ui.debug.DebugViewModel
import io.github.fugeekies.timeflies.ui.equivalences.EquivalencesScreen
import io.github.fugeekies.timeflies.ui.equivalences.EquivalencesViewModel
import io.github.fugeekies.timeflies.ui.equivalences.TemporaryAgesStore
import io.github.fugeekies.timeflies.ui.eventages.EventAgesScreen
import io.github.fugeekies.timeflies.ui.eventages.EventAgesViewModel
import io.github.fugeekies.timeflies.ui.onboarding.OnboardingScreen
import io.github.fugeekies.timeflies.ui.onboarding.OnboardingViewModel
import io.github.fugeekies.timeflies.ui.personal.FileExchange
import io.github.fugeekies.timeflies.ui.personal.PersonalEventsViewModel
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.nav_debug
import io.github.fugeekies.timeflies.ui.resources.nav_equivalences
import io.github.fugeekies.timeflies.ui.resources.nav_event_ages
import io.github.fugeekies.timeflies.ui.resources.nav_settings
import io.github.fugeekies.timeflies.ui.resources.nav_today
import io.github.fugeekies.timeflies.ui.settings.SettingsScreen
import io.github.fugeekies.timeflies.ui.settings.SettingsViewModel
import io.github.fugeekies.timeflies.ui.text.AppLocale
import io.github.fugeekies.timeflies.ui.text.apply
import io.github.fugeekies.timeflies.ui.theme.ThemeMode
import io.github.fugeekies.timeflies.ui.theme.TimeFliesIcons
import io.github.fugeekies.timeflies.ui.theme.TimeFliesTheme
import io.github.fugeekies.timeflies.ui.theme.isDark
import io.github.fugeekies.timeflies.ui.today.TodayScreen
import io.github.fugeekies.timeflies.ui.today.TodayViewModel
import io.github.fugeekies.timeflies.ui.update.UpdateBanner
import io.github.fugeekies.timeflies.ui.update.UpdateViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The four destinations (docs/UI_DESIGN_SPEC.md, "Navigation"). */
private enum class Destination(val label: StringResource, val icon: ImageVector) {
    TODAY(Res.string.nav_today, TimeFliesIcons.Calendar),
    EVENT_AGES(Res.string.nav_event_ages, TimeFliesIcons.Search),
    EQUIVALENCES(Res.string.nav_equivalences, TimeFliesIcons.Compare),
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
 * @property sendNotification the debug panel offers to send today's Windows notification at once.
 * @property widget Android: Settings shows the "Écran d'accueil" panel; null elsewhere.
 * @property refreshWidget the debug panel offers to update the Android widget at once.
 * @property files the system file picker for the export and import of Settings › Mes événements; null hides them.
 * @property onDarkTheme the colours shown became dark or light (the VHS is always dark): Android sets the icons of its
 *   system bars to match.
 */
data class PlatformHooks(
    val screenRequest: AppScreen? = null,
    val onScreenRequestHandled: () -> Unit = {},
    val showWindowsPanel: Boolean = false,
    val sendNotification: (() -> Unit)? = null,
    val widget: WidgetPinning? = null,
    val refreshWidget: (() -> Unit)? = null,
    val files: FileExchange? = null,
    val onDarkTheme: (Boolean) -> Unit = {},
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
 * skipped, then the theme and adaptive navigation (bottom bar on a phone, rail on a wide window).
 *
 * @param useCases what the screens call, built by the app's composition root.
 * @param appVersion version shown in Settings › À propos; null for a local development build.
 * @param debugTools tools of the debug panel; null in a release, where the panel does not exist.
 * @param platform what only one platform adds (Windows tray, Android widget).
 * @param releaseUpdates the banner of a new version on Today; null where nothing is looked for (tests, previews).
 */
@Composable
fun App(
    useCases: AppUseCases,
    appVersion: String? = null,
    debugTools: DebugTools? = null,
    platform: PlatformHooks = PlatformHooks(),
    releaseUpdates: ReleaseUpdates? = null,
) {
    // Forced light or dark from the debug panel, for this session only.
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    val settingsFlow = remember(useCases) { useCases.observeSettings() }
    val settings by settingsFlow.collectAsStateWithLifecycle(initialValue = null)
    // The style chosen in Settings › Affichage; the Polaroïd until the settings are read.
    val style = settings?.displayStyle ?: DisplayStyle.POLAROID
    val systemDark = themeMode.isDark()
    val dark = style.isDark(systemDark)
    LaunchedEffect(dark) { platform.onDarkTheme(dark) }
    // The language of the texts (milestone 17): set before any text is read; when it changes, the key below reads
    // every text again. The screen shown and the ages added are kept above it, so they stay.
    val language = AppLocale.apply(settings?.language)
    val destination = rememberSaveable { mutableStateOf(Destination.TODAY) }
    // Ages picked on the fly, shared by Equivalences and "Quel âge ?", forgotten when the app closes.
    val temporaryAges = remember { TemporaryAgesStore() }
    key(language) {
        TimeFliesTheme(style = style, darkTheme = systemDark) {
            when (val current = settings) {
                // Settings not read yet (a few milliseconds): a plain background rather than a flash of the wrong screen.
                null -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                else -> if (!current.onboardingDone) {
                    // A screen asked from the tray waits for nothing: the first launch screen comes first.
                    LaunchedEffect(platform.screenRequest) { if (platform.screenRequest != null) platform.onScreenRequestHandled() }
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                        OnboardingScreen(
                            viewModel { OnboardingViewModel(useCases.completeOnboarding, useCases.getEventGroups, useCases.clock) },
                            Modifier.fillMaxSize().safeDrawingPadding(),
                        )
                    }
                } else {
                    MainScreens(
                        useCases, appVersion, debugTools, platform, releaseUpdates, themeMode,
                        onThemeMode = { themeMode = it },
                        destination = destination,
                        temporaryAges = temporaryAges,
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreens(
    useCases: AppUseCases,
    appVersion: String?,
    debugTools: DebugTools?,
    platform: PlatformHooks,
    releaseUpdates: ReleaseUpdates?,
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
    destination: MutableState<Destination>,
    temporaryAges: TemporaryAgesStore,
) {
    var current by destination
    LaunchedEffect(platform.screenRequest) {
        when (platform.screenRequest) {
            AppScreen.TODAY -> current = Destination.TODAY
            AppScreen.SETTINGS -> current = Destination.SETTINGS
            null -> return@LaunchedEffect
        }
        platform.onScreenRequestHandled()
    }
    // A card touched on Today or Equivalences: "Quel âge ?" opens on its event.
    var eventRequest by remember { mutableStateOf<Event?>(null) }
    val openEvent = { event: Event ->
        eventRequest = event
        current = Destination.EVENT_AGES
    }
    val openSettings = { current = Destination.SETTINGS }
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
        with(useCases) {
            when (current) {
                Destination.TODAY -> Column(content) {
                    // A newer version than the running one: above the sheet of the day (decision of 16/11/2026).
                    releaseUpdates?.let { UpdateBanner(viewModel { UpdateViewModel(it) }) }
                    TodayScreen(
                        viewModel { TodayViewModel(getDailyShock, observeSettings, clock, personalEvents) },
                        onOpenEvent = openEvent,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
                Destination.SETTINGS -> SettingsScreen(
                    viewModel { SettingsViewModel(observeSettings, updateSettings, countEvents, getEventGroups, clock) },
                    appVersion,
                    content,
                    showWindowsPanel = platform.showWindowsPanel,
                    widget = platform.widget,
                    personalEvents = viewModel { PersonalEventsViewModel(personalEvents, clock, platform.files) },
                )
                // Their reduced mode (without birth) also offers ages picked on the fly.
                Destination.EQUIVALENCES -> EquivalencesScreen(
                    viewModel { EquivalencesViewModel(getEquivalences, observeSettings, updateSettings, clock, temporaryAges) },
                    onOpenSettings = openSettings,
                    onOpenEvent = openEvent,
                    modifier = content,
                )
                Destination.EVENT_AGES -> EventAgesScreen(
                    viewModel { EventAgesViewModel(searchEvents, getEventAges, personalEvents, observeSettings, updateSettings, clock, temporaryAges) },
                    request = eventRequest,
                    onRequestHandled = { eventRequest = null },
                    onOpenSettings = openSettings,
                    modifier = content,
                )
                Destination.DEBUG -> debugTools?.let { tools ->
                    DebugScreen(
                        viewModel { DebugViewModel(tools, observeSettings, clock) },
                        themeMode,
                        onThemeMode,
                        content,
                        onSendNotification = platform.sendNotification,
                        onRefreshWidget = platform.refreshWidget,
                    )
                }
            }
        }
    }
}
