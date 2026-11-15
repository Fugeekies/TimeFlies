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
import io.github.fugeekies.timeflies.domain.usecase.FindEquivalent
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.model.EquivalentDirection
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.debug.DebugScreen
import io.github.fugeekies.timeflies.ui.debug.DebugViewModel
import io.github.fugeekies.timeflies.ui.equivalences.EquivalencesScreen
import io.github.fugeekies.timeflies.ui.equivalences.EquivalencesViewModel
import io.github.fugeekies.timeflies.ui.equivalences.TemporaryAgesStore
import io.github.fugeekies.timeflies.ui.myequivalent.EquivalentRequest
import io.github.fugeekies.timeflies.ui.myequivalent.MyEquivalentScreen
import io.github.fugeekies.timeflies.ui.myequivalent.MyEquivalentViewModel
import io.github.fugeekies.timeflies.ui.onboarding.OnboardingScreen
import io.github.fugeekies.timeflies.ui.onboarding.OnboardingViewModel
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.nav_debug
import io.github.fugeekies.timeflies.ui.resources.nav_equivalences
import io.github.fugeekies.timeflies.ui.resources.nav_my_equivalent
import io.github.fugeekies.timeflies.ui.resources.nav_settings
import io.github.fugeekies.timeflies.ui.resources.nav_today
import io.github.fugeekies.timeflies.ui.settings.SettingsScreen
import io.github.fugeekies.timeflies.ui.settings.SettingsViewModel
import io.github.fugeekies.timeflies.ui.theme.TimeFliesIcons
import io.github.fugeekies.timeflies.ui.theme.TimeFliesTheme
import io.github.fugeekies.timeflies.ui.today.TodayScreen
import io.github.fugeekies.timeflies.ui.today.TodayViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The four destinations (docs/UI_DESIGN_SPEC.md, "Navigation"). */
private enum class Destination(val label: StringResource, val icon: ImageVector) {
    TODAY(Res.string.nav_today, TimeFliesIcons.Calendar),
    EQUIVALENCES(Res.string.nav_equivalences, TimeFliesIcons.Compare),
    MY_EQUIVALENT(Res.string.nav_my_equivalent, TimeFliesIcons.Search),
    SETTINGS(Res.string.nav_settings, TimeFliesIcons.Tune),

    /** Debug builds only (docs/DEVELOPMENT.md, "Panneau de débogage"). */
    DEBUG(Res.string.nav_debug, TimeFliesIcons.Debug),
}

/**
 * Root composable shared by the Android and Windows apps: the first launch screen until it is completed or
 * skipped, then the theme and adaptive navigation (bottom bar on a phone, rail on a wide window). The use cases
 * come from the app's composition root.
 *
 * @param appVersion version shown in Settings › À propos; null for a local development build.
 * @param debugTools tools of the debug panel; null in a release, where the panel does not exist.
 */
@Composable
fun App(
    getDailyShock: GetDailyShock,
    getEquivalences: GetEquivalences,
    findEquivalent: FindEquivalent,
    searchEvents: SearchEvents,
    observeSettings: ObserveSettings,
    updateSettings: UpdateSettings,
    completeOnboarding: CompleteOnboarding,
    countEvents: CountEvents,
    clock: Clock,
    appVersion: String? = null,
    debugTools: DebugTools? = null,
) {
    TimeFliesTheme {
        val settingsFlow = remember(observeSettings) { observeSettings() }
        val settings by settingsFlow.collectAsStateWithLifecycle(initialValue = null)
        val content = Modifier.fillMaxSize().safeDrawingPadding()
        when (val current = settings) {
            // Settings not read yet (a few milliseconds): a plain background rather than a flash of the wrong screen.
            null -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
            else -> if (!current.onboardingDone) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    OnboardingScreen(viewModel { OnboardingViewModel(completeOnboarding, clock) }, content)
                }
            } else {
                MainScreens(getDailyShock, getEquivalences, findEquivalent, searchEvents, observeSettings, updateSettings, countEvents, clock, appVersion, debugTools)
            }
        }
    }
}

@Composable
private fun MainScreens(
    getDailyShock: GetDailyShock,
    getEquivalences: GetEquivalences,
    findEquivalent: FindEquivalent,
    searchEvents: SearchEvents,
    observeSettings: ObserveSettings,
    updateSettings: UpdateSettings,
    countEvents: CountEvents,
    clock: Clock,
    appVersion: String?,
    debugTools: DebugTools?,
) {
    var current by rememberSaveable { mutableStateOf(Destination.TODAY) }
    // Ages picked on the fly, shared by Equivalences and Mon équivalent, forgotten when the app closes.
    val temporaryAges = remember { TemporaryAgesStore() }
    // A card touched on Today or Equivalences: Mon équivalent opens on its event.
    var equivalentRequest by remember { mutableStateOf<EquivalentRequest?>(null) }
    val openEquivalent = { event: Event, direction: EquivalentDirection ->
        equivalentRequest = EquivalentRequest(event, direction)
        current = Destination.MY_EQUIVALENT
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
            // Today speaks of the user's life: its events are the user's (decision of 15/11/2026).
            Destination.TODAY -> TodayScreen(
                viewModel { TodayViewModel(getDailyShock, observeSettings, clock) },
                onOpenEquivalent = { openEquivalent(it, EquivalentDirection.USER_TO_OTHER) },
                modifier = content,
            )
            Destination.SETTINGS -> SettingsScreen(
                viewModel { SettingsViewModel(observeSettings, updateSettings, countEvents, clock) },
                appVersion,
                content,
            )
            // Their reduced mode (without birth) also offers ages picked on the fly.
            Destination.EQUIVALENCES -> EquivalencesScreen(
                viewModel { EquivalencesViewModel(getEquivalences, observeSettings, updateSettings, clock, temporaryAges) },
                onOpenSettings = { current = Destination.SETTINGS },
                onOpenEquivalent = openEquivalent,
                modifier = content,
            )
            Destination.MY_EQUIVALENT -> MyEquivalentScreen(
                viewModel { MyEquivalentViewModel(searchEvents, findEquivalent, observeSettings, updateSettings, clock, temporaryAges) },
                request = equivalentRequest,
                onRequestHandled = { equivalentRequest = null },
                onOpenSettings = { current = Destination.SETTINGS },
                modifier = content,
            )
            Destination.DEBUG -> debugTools?.let { tools ->
                DebugScreen(viewModel { DebugViewModel(tools, observeSettings, clock) }, content)
            }
        }
    }
}
