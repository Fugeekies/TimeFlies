package io.github.fugeekies.timeflies.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.ui.components.BirthFields
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.debug_apply
import io.github.fugeekies.timeflies.ui.resources.debug_current_region
import io.github.fugeekies.timeflies.ui.resources.debug_date
import io.github.fugeekies.timeflies.ui.resources.debug_date_fake
import io.github.fugeekies.timeflies.ui.resources.debug_date_input
import io.github.fugeekies.timeflies.ui.resources.debug_date_system
import io.github.fugeekies.timeflies.ui.resources.debug_feb28
import io.github.fugeekies.timeflies.ui.resources.debug_feb29
import io.github.fugeekies.timeflies.ui.resources.debug_invalid_date
import io.github.fugeekies.timeflies.ui.resources.debug_invalid_region
import io.github.fugeekies.timeflies.ui.resources.debug_next_day
import io.github.fugeekies.timeflies.ui.resources.debug_next_empty_day
import io.github.fugeekies.timeflies.ui.resources.debug_previous_day
import io.github.fugeekies.timeflies.ui.resources.debug_redo_onboarding
import io.github.fugeekies.timeflies.ui.resources.debug_refresh
import io.github.fugeekies.timeflies.ui.resources.debug_region_input
import io.github.fugeekies.timeflies.ui.resources.debug_reset_settings
import io.github.fugeekies.timeflies.ui.resources.debug_settings
import io.github.fugeekies.timeflies.ui.resources.debug_theme
import io.github.fugeekies.timeflies.ui.resources.debug_theme_dark
import io.github.fugeekies.timeflies.ui.resources.debug_theme_light
import io.github.fugeekies.timeflies.ui.resources.debug_theme_note
import io.github.fugeekies.timeflies.ui.resources.debug_theme_system
import io.github.fugeekies.timeflies.ui.resources.debug_stats
import io.github.fugeekies.timeflies.ui.resources.debug_stats_count
import io.github.fugeekies.timeflies.ui.resources.debug_stats_empty_days
import io.github.fugeekies.timeflies.ui.resources.debug_stats_family
import io.github.fugeekies.timeflies.ui.resources.debug_stats_poor_types
import io.github.fugeekies.timeflies.ui.resources.debug_stats_rejections
import io.github.fugeekies.timeflies.ui.resources.debug_system_clock
import io.github.fugeekies.timeflies.ui.resources.debug_title
import io.github.fugeekies.timeflies.ui.resources.debug_send_notification
import io.github.fugeekies.timeflies.ui.resources.debug_send_notification_note
import io.github.fugeekies.timeflies.ui.resources.debug_windows
import io.github.fugeekies.timeflies.ui.resources.settings_birth
import io.github.fugeekies.timeflies.ui.resources.settings_clear
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.ThemeMode
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.stringResource

/**
 * The debug panel (docs/DEVELOPMENT.md, "Panneau de débogage"), debug builds only.
 *
 * @param themeMode light or dark forced for this session; [onThemeMode] changes it.
 * @param onSendMorningNotification Windows: sends the morning notification at once; null elsewhere (no panel).
 */
@Composable
fun DebugScreen(
    viewModel: DebugViewModel,
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    onSendMorningNotification: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = state ?: return
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 640.dp).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(Res.string.debug_title),
                fontFamily = LocalStyle.current.handwriting,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            Eyebrow(stringResource(Res.string.debug_date))
            Panel { DatePanel(current, viewModel) }
            Eyebrow(stringResource(Res.string.debug_settings))
            Panel { SettingsPanel(current, viewModel) }
            Eyebrow(stringResource(Res.string.debug_theme))
            Panel { ThemePanel(themeMode, onThemeMode) }
            if (onSendMorningNotification != null) {
                Eyebrow(stringResource(Res.string.debug_windows))
                Panel {
                    OutlinedButton(onClick = onSendMorningNotification) { Text(stringResource(Res.string.debug_send_notification)) }
                    Text(stringResource(Res.string.debug_send_notification_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Eyebrow(stringResource(Res.string.debug_stats))
            Panel { StatsPanel(current, viewModel) }
        }
    }
}

/** System, light or dark, to try both themes without changing the system setting. */
@Composable
private fun ThemePanel(themeMode: ThemeMode, onThemeMode: (ThemeMode) -> Unit) {
    val options = ThemeMode.entries
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = mode == themeMode,
                onClick = { onThemeMode(mode) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
            ) {
                Text(
                    stringResource(
                        when (mode) {
                            ThemeMode.SYSTEM -> Res.string.debug_theme_system
                            ThemeMode.LIGHT -> Res.string.debug_theme_light
                            ThemeMode.DARK -> Res.string.debug_theme_dark
                        },
                    ),
                )
            }
        }
    }
    Text(stringResource(Res.string.debug_theme_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun Eyebrow(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp).semantics { heading() })
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(6.dp), shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DatePanel(state: DebugUiState, viewModel: DebugViewModel) {
    val dates = rememberDateTexts()
    val label = dates.withDayName(state.today)
    Text(
        if (state.fakeDate == null) stringResource(Res.string.debug_date_system, label) else stringResource(Res.string.debug_date_fake, label),
        style = MaterialTheme.typography.titleMedium,
        color = if (state.fakeDate == null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedButton(onClick = viewModel::previousDay) { Text(stringResource(Res.string.debug_previous_day)) }
        OutlinedButton(onClick = viewModel::nextDay) { Text(stringResource(Res.string.debug_next_day)) }
        OutlinedButton(onClick = { viewModel.setDate(LocalDate(2028, 2, 29)) }) { Text(stringResource(Res.string.debug_feb29)) }
        OutlinedButton(onClick = { viewModel.setDate(LocalDate(2027, 2, 28)) }) { Text(stringResource(Res.string.debug_feb28)) }
        OutlinedButton(onClick = viewModel::nextEmptyDay, enabled = !state.stats?.emptyDays.isNullOrEmpty()) {
            Text(stringResource(Res.string.debug_next_empty_day))
        }
    }
    var text by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(Res.string.debug_date_input)) },
            singleLine = true,
            isError = state.dateError,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = { viewModel.setDateText(text) }) { Text(stringResource(Res.string.debug_apply)) }
    }
    if (state.dateError) Text(stringResource(Res.string.debug_invalid_date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    if (state.fakeDate != null) {
        TextButton(onClick = { viewModel.setDate(null) }) { Text(stringResource(Res.string.debug_system_clock)) }
    }
}

@Composable
private fun ColumnScope.SettingsPanel(state: DebugUiState, viewModel: DebugViewModel) {
    Text(stringResource(Res.string.settings_birth), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    // Picked values wait for the other list; the birth is forced as soon as both are known.
    var month by remember { mutableStateOf<Month?>(null) }
    var year by remember { mutableStateOf<Int?>(null) }
    val shownMonth = month ?: state.settings.birth?.month
    val shownYear = year ?: state.settings.birth?.year
    BirthFields(
        month = shownMonth,
        year = shownYear,
        years = Generations.birthYears(state.today),
        onMonthPicked = { picked ->
            month = picked
            shownYear?.let { viewModel.setBirth(YearMonth(it, picked)) }
        },
        onYearPicked = { picked ->
            year = picked
            shownMonth?.let { viewModel.setBirth(YearMonth(picked, it)) }
        },
    )
    if (state.settings.birth != null) {
        TextButton(
            onClick = {
                month = null
                year = null
                viewModel.setBirth(null)
            },
            modifier = Modifier.align(Alignment.End),
        ) { Text(stringResource(Res.string.settings_clear)) }
    }

    Text(stringResource(Res.string.debug_current_region, state.settings.region.code), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    var region by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = region,
            onValueChange = { region = it },
            label = { Text(stringResource(Res.string.debug_region_input)) },
            singleLine = true,
            isError = state.regionError,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = { viewModel.setRegionText(region) }) { Text(stringResource(Res.string.debug_apply)) }
    }
    if (state.regionError) Text(stringResource(Res.string.debug_invalid_region), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)

    OutlinedButton(onClick = viewModel::redoOnboarding) { Text(stringResource(Res.string.debug_redo_onboarding)) }
    OutlinedButton(
        onClick = {
            month = null
            year = null
            viewModel.resetSettings()
        },
    ) { Text(stringResource(Res.string.debug_reset_settings)) }
}

@Composable
private fun StatsPanel(state: DebugUiState, viewModel: DebugViewModel) {
    val stats = state.stats
    val body = MaterialTheme.typography.bodyMedium
    val color = MaterialTheme.colorScheme.onSurface
    if (stats != null) {
        Text(stringResource(Res.string.debug_stats_count, stats.eventCount), style = MaterialTheme.typography.titleMedium, color = color)
        Family.entries.forEach { family ->
            Text(stringResource(Res.string.debug_stats_family, familyLabel(family), stats.eventsByFamily[family] ?: 0), style = body, color = color)
        }
        Text(stringResource(Res.string.debug_stats_empty_days, stats.emptyDays.size), style = body, color = color)
        Text(stringResource(Res.string.debug_stats_poor_types, stats.minEventsPerType, stats.typesBelowMinimum.size), style = body, color = color)
        stats.typesBelowMinimum.forEach { (type, count) ->
            Text(stringResource(Res.string.debug_stats_family, type.id, count), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(stringResource(Res.string.debug_stats_rejections, stats.rejections.size), style = body, color = color)
        stats.rejections.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
    }
    TextButton(onClick = viewModel::refreshStats) { Text(stringResource(Res.string.debug_refresh)) }
}
