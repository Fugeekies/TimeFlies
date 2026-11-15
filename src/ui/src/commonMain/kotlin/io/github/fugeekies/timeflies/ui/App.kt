package io.github.fugeekies.timeflies.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.coming_soon
import io.github.fugeekies.timeflies.ui.resources.nav_equivalences
import io.github.fugeekies.timeflies.ui.resources.nav_my_equivalent
import io.github.fugeekies.timeflies.ui.resources.nav_settings
import io.github.fugeekies.timeflies.ui.resources.nav_today
import io.github.fugeekies.timeflies.ui.settings.SettingsScreen
import io.github.fugeekies.timeflies.ui.settings.SettingsViewModel
import io.github.fugeekies.timeflies.ui.theme.TimeFliesTheme
import io.github.fugeekies.timeflies.ui.theme.TimeFliesIcons
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
}

/**
 * Root composable shared by the Android and Windows apps: theme and adaptive navigation (bottom bar on
 * a phone, rail on a wide window). The use cases come from the app's composition root.
 *
 * @param appVersion version shown in Settings › À propos; null for a local development build.
 */
@Composable
fun App(
    getDailyShock: GetDailyShock,
    observeSettings: ObserveSettings,
    updateSettings: UpdateSettings,
    countEvents: CountEvents,
    clock: Clock,
    appVersion: String? = null,
) {
    TimeFliesTheme {
        var current by rememberSaveable { mutableStateOf(Destination.TODAY) }
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                Destination.entries.forEach { destination ->
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
                Destination.TODAY -> TodayScreen(viewModel { TodayViewModel(getDailyShock, observeSettings, clock) }, content)
                Destination.SETTINGS -> SettingsScreen(
                    viewModel { SettingsViewModel(observeSettings, updateSettings, countEvents, clock) },
                    appVersion,
                    content,
                )
                else -> ComingSoon(content)
            }
        }
    }
}

@Composable
private fun ComingSoon(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            stringResource(Res.string.coming_soon),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp),
        )
    }
}
