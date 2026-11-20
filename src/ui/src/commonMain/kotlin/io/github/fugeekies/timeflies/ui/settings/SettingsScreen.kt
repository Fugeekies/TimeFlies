package io.github.fugeekies.timeflies.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import io.github.fugeekies.timeflies.ui.WidgetPinning
import io.github.fugeekies.timeflies.ui.components.BirthFields
import io.github.fugeekies.timeflies.ui.components.Eyebrow
import io.github.fugeekies.timeflies.ui.components.Panel
import io.github.fugeekies.timeflies.ui.components.ScreenTitle
import io.github.fugeekies.timeflies.ui.events.EventFilterTree
import io.github.fugeekies.timeflies.ui.personal.PersonalEventsPanel
import io.github.fugeekies.timeflies.ui.personal.PersonalEventsViewModel
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.about_dev_version
import io.github.fugeekies.timeflies.ui.resources.about_event_count
import io.github.fugeekies.timeflies.ui.resources.about_inspired_after
import io.github.fugeekies.timeflies.ui.resources.about_inspired_before
import io.github.fugeekies.timeflies.ui.resources.about_inspired_episode
import io.github.fugeekies.timeflies.ui.resources.about_license
import io.github.fugeekies.timeflies.ui.resources.about_privacy
import io.github.fugeekies.timeflies.ui.resources.about_report
import io.github.fugeekies.timeflies.ui.resources.about_source
import io.github.fugeekies.timeflies.ui.resources.about_third_party
import io.github.fugeekies.timeflies.ui.resources.about_version
import io.github.fugeekies.timeflies.ui.resources.cancel
import io.github.fugeekies.timeflies.ui.resources.months
import io.github.fugeekies.timeflies.ui.resources.region_fr
import io.github.fugeekies.timeflies.ui.resources.settings_about
import io.github.fugeekies.timeflies.ui.resources.settings_birth
import io.github.fugeekies.timeflies.ui.resources.settings_birth_unset
import io.github.fugeekies.timeflies.ui.resources.settings_birth_unset_help
import io.github.fugeekies.timeflies.ui.resources.settings_birth_value
import io.github.fugeekies.timeflies.ui.resources.settings_clear
import io.github.fugeekies.timeflies.ui.resources.settings_display
import io.github.fugeekies.timeflies.ui.resources.settings_events
import io.github.fugeekies.timeflies.ui.resources.settings_events_help
import io.github.fugeekies.timeflies.ui.resources.settings_generations
import io.github.fugeekies.timeflies.ui.resources.settings_keep_in_tray
import io.github.fugeekies.timeflies.ui.resources.settings_language
import io.github.fugeekies.timeflies.ui.resources.settings_launch_at_startup
import io.github.fugeekies.timeflies.ui.resources.settings_launch_at_startup_help
import io.github.fugeekies.timeflies.ui.resources.settings_morning_notification
import io.github.fugeekies.timeflies.ui.resources.settings_personal
import io.github.fugeekies.timeflies.ui.resources.settings_region
import io.github.fugeekies.timeflies.ui.resources.settings_release_check
import io.github.fugeekies.timeflies.ui.resources.settings_release_check_help
import io.github.fugeekies.timeflies.ui.resources.settings_style
import io.github.fugeekies.timeflies.ui.resources.settings_style_help
import io.github.fugeekies.timeflies.ui.resources.settings_title
import io.github.fugeekies.timeflies.ui.resources.settings_widget
import io.github.fugeekies.timeflies.ui.resources.settings_widget_add
import io.github.fugeekies.timeflies.ui.resources.settings_widget_manual
import io.github.fugeekies.timeflies.ui.resources.settings_widget_text
import io.github.fugeekies.timeflies.ui.resources.settings_windows
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.text.capitalized
import io.github.fugeekies.timeflies.ui.text.nativeName
import kotlinx.datetime.number
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

private const val REPOSITORY_URL = "https://github.com/Fugeekies/TimeFlies"
private const val ISSUES_URL = "$REPOSITORY_URL/issues"
private const val LICENSE_URL = "$REPOSITORY_URL/blob/main/LICENSE"
private const val THIRD_PARTY_LICENSES_URL = "$REPOSITORY_URL/tree/main/licenses"

/**
 * The Settings screen (docs/UISpec/Settings.template.md).
 *
 * @param appVersion version shown in "À propos"; null for a local development build.
 * @param showWindowsPanel the Windows app shows the "Notification et démarrage" panel.
 * @param widget the Android app shows the "Écran d'accueil" panel.
 * @param personalEvents the "Mes événements" panel (decision of 19/11/2026); null hides it (previews).
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    appVersion: String?,
    modifier: Modifier = Modifier,
    showWindowsPanel: Boolean = false,
    widget: WidgetPinning? = null,
    personalEvents: PersonalEventsViewModel? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = state ?: return
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ScreenTitle(stringResource(Res.string.settings_title))
            Eyebrow(stringResource(Res.string.settings_generations))
            Panel { GenerationsPanel(current, viewModel) }
            Eyebrow(stringResource(Res.string.settings_display))
            Panel { DisplayPanel(current.displayStyle, viewModel::onDisplayStyle, current.region, current.language, viewModel::onLanguage) }
            // The user's own events, kept on the device, exported to change phones (decision of 19/11/2026).
            if (personalEvents != null) {
                Eyebrow(stringResource(Res.string.settings_personal))
                Panel { PersonalEventsPanel(personalEvents) }
            }
            // What is shown at all: unticked groups disappear everywhere (decision of 16/11/2026).
            if (current.eventGroups.isNotEmpty()) {
                Eyebrow(stringResource(Res.string.settings_events))
                Panel {
                    Text(stringResource(Res.string.settings_events_help), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    EventFilterTree(current.eventGroups, current.excludedEventGroups, viewModel::onExcludedEventGroups)
                }
            }
            if (widget != null) {
                Eyebrow(stringResource(Res.string.settings_widget))
                Panel { WidgetPanel(widget) }
            }
            if (showWindowsPanel) {
                Eyebrow(stringResource(Res.string.settings_windows))
                Panel { WindowsPanel(current.windows, viewModel) }
            }
            Eyebrow(stringResource(Res.string.settings_about))
            Panel { AboutPanel(appVersion, current.eventCount, current.releaseCheck, viewModel::onReleaseCheck) }
        }
    }
}

@Composable
private fun ColumnScope.GenerationsPanel(state: SettingsUiState, viewModel: SettingsViewModel) {
    val colors = MaterialTheme.colorScheme
    val months = stringArrayResource(Res.array.months).map { it.capitalized() }

    Text(stringResource(Res.string.settings_birth), style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
    val birth = state.birth
    if (birth != null) {
        val age = state.userAge ?: 0
        Text(
            stringResource(Res.string.settings_birth_value, months[birth.month.number - 1], birth.year, pluralStringResource(Res.plurals.years_count, age, age)),
            style = MaterialTheme.typography.bodyLarge,
            color = colors.primary,
        )
    } else {
        Text(stringResource(Res.string.settings_birth_unset), style = MaterialTheme.typography.bodyLarge, color = colors.onSurfaceVariant)
        Text(stringResource(Res.string.settings_birth_unset_help), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
    }
    BirthFields(state.month, state.year, state.birthYears, viewModel::onMonthPicked, viewModel::onYearPicked)
    if (birth != null) {
        TextButton(onClick = viewModel::onClearBirth, modifier = Modifier.align(Alignment.End)) {
            Text(stringResource(Res.string.settings_clear))
        }
    }
}

@Composable
private fun ColumnScope.DisplayPanel(
    style: DisplayStyle,
    onStyle: (DisplayStyle) -> Unit,
    region: Region,
    language: AppLanguage,
    onLanguage: (AppLanguage) -> Unit,
) {
    // Polaroïd by default, VHS since milestone 14 (decision of 17/11/2026): applied at once, widget included.
    Text(stringResource(Res.string.settings_style), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    Text(stringResource(Res.string.settings_style_help), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    StylePicker(style, onStyle)
    // France only for now (decision of 15/11/2026): shown, not editable. Other regions come with the translations.
    val regionName = if (region == Region.FRANCE) stringResource(Res.string.region_fr) else region.code
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(Res.string.settings_region), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(regionName, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    LanguageRow(language, onLanguage)
}

/**
 * Settings › Affichage › Langue (docs/UISpec/Settings.template.md, milestone 17): the language shown, touched to pick
 * another one, each written in its own language.
 */
@Composable
private fun LanguageRow(language: AppLanguage, onLanguage: (AppLanguage) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val title = stringResource(Res.string.settings_language)
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { open = true }
            .padding(vertical = 8.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(language.nativeName, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            confirmButton = { TextButton(onClick = { open = false }) { Text(stringResource(Res.string.cancel)) } },
            title = { Text(title) },
            text = {
                Column(Modifier.selectableGroup()) {
                    AppLanguage.entries.forEach { choice ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(choice == language, role = Role.RadioButton) { open = false; onLanguage(choice) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = choice == language, onClick = null, modifier = Modifier.padding(12.dp))
                            Text(choice.nativeName, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
        )
    }
}

/** Android only: the button that asks the launcher to pin the widget, or how to add it by hand. */
@Composable
private fun ColumnScope.WidgetPanel(widget: WidgetPinning) {
    Text(stringResource(Res.string.settings_widget_text), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    val pin = widget.pin
    if (pin != null) {
        Button(onClick = pin, modifier = Modifier.align(Alignment.End)) { Text(stringResource(Res.string.settings_widget_add)) }
    } else {
        Text(stringResource(Res.string.settings_widget_manual), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Windows only: startup, morning notification and tray (defaults decided on 13/11/2026). */
@Composable
private fun WindowsPanel(windows: WindowsSettings, viewModel: SettingsViewModel) {
    CheckRow(stringResource(Res.string.settings_launch_at_startup), windows.launchAtStartup, viewModel::onLaunchAtStartup)
    Text(
        stringResource(Res.string.settings_launch_at_startup_help),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 48.dp),
    )
    CheckRow(stringResource(Res.string.settings_morning_notification), windows.launchNotification, viewModel::onLaunchNotification)
    CheckRow(stringResource(Res.string.settings_keep_in_tray), windows.keepInTray, viewModel::onKeepInTray)
}

/** A check box and its label, the whole row clickable. */
@Composable
private fun CheckRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(value = checked, role = Role.Checkbox, onValueChange = onChange),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null, modifier = Modifier.padding(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AboutPanel(appVersion: String?, eventCount: Int?, releaseCheck: Boolean, onReleaseCheck: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    Text(
        if (appVersion == null) stringResource(Res.string.about_dev_version) else stringResource(Res.string.about_version, appVersion),
        style = MaterialTheme.typography.titleMedium,
        color = colors.onSurface,
    )
    val episode = stringResource(Res.string.about_inspired_episode)
    val before = stringResource(Res.string.about_inspired_before)
    val after = stringResource(Res.string.about_inspired_after)
    Text(
        buildAnnotatedString {
            append(before)
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(episode) }
            append(after)
        },
        style = MaterialTheme.typography.bodyMedium,
        color = colors.onSurface,
    )
    if (eventCount != null) {
        Text(pluralStringResource(Res.plurals.about_event_count, eventCount, eventCount), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
    }
    Column {
        listOf(
            Res.string.about_source to REPOSITORY_URL,
            Res.string.about_report to ISSUES_URL,
            Res.string.about_license to LICENSE_URL,
            Res.string.about_third_party to THIRD_PARTY_LICENSES_URL,
        ).forEach { (label, url) ->
            TextButton(onClick = { uriHandler.openUri(url) }) { Text("${stringResource(label)} ›") }
        }
    }
    CheckRow(stringResource(Res.string.settings_release_check), releaseCheck, onReleaseCheck)
    Text(stringResource(Res.string.settings_release_check_help), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant, modifier = Modifier.padding(start = 48.dp))
    Text(stringResource(Res.string.about_privacy), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
}
