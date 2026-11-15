package io.github.fugeekies.timeflies.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Region
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
import io.github.fugeekies.timeflies.ui.resources.months
import io.github.fugeekies.timeflies.ui.resources.region_fr
import io.github.fugeekies.timeflies.ui.resources.settings_about
import io.github.fugeekies.timeflies.ui.resources.settings_birth
import io.github.fugeekies.timeflies.ui.resources.settings_birth_unset
import io.github.fugeekies.timeflies.ui.resources.settings_birth_unset_help
import io.github.fugeekies.timeflies.ui.resources.settings_birth_value
import io.github.fugeekies.timeflies.ui.resources.settings_clear
import io.github.fugeekies.timeflies.ui.resources.settings_display
import io.github.fugeekies.timeflies.ui.resources.settings_generations
import io.github.fugeekies.timeflies.ui.resources.settings_region
import io.github.fugeekies.timeflies.ui.resources.settings_title
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.components.BirthFields
import io.github.fugeekies.timeflies.ui.text.capitalized
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import kotlinx.datetime.number
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

private const val REPOSITORY_URL = "https://github.com/Fugeekies/TimeFlies"
private const val ISSUES_URL = "$REPOSITORY_URL/issues"
private const val LICENSE_URL = "$REPOSITORY_URL/blob/main/LICENSE"
private const val THIRD_PARTY_LICENSES_URL = "$REPOSITORY_URL/tree/main/licenses"

/**
 * The Settings screen (docs/UISpec/Settings.template.md). The Android widget and Windows panels come with
 * their milestones; the style choice comes with the Éphéméride.
 *
 * @param appVersion version shown in "À propos"; null for a local development build.
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, appVersion: String?, modifier: Modifier = Modifier) {
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
            Text(
                stringResource(Res.string.settings_title),
                fontFamily = LocalStyle.current.handwriting,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            Eyebrow(stringResource(Res.string.settings_generations))
            Panel { GenerationsPanel(current, viewModel) }
            Eyebrow(stringResource(Res.string.settings_display))
            Panel { DisplayPanel(current.region) }
            Eyebrow(stringResource(Res.string.settings_about))
            Panel { AboutPanel(appVersion, current.eventCount) }
        }
    }
}

@Composable
private fun Eyebrow(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp).semantics { heading() },
    )
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
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
private fun DisplayPanel(region: Region) {
    // France only for now (decision of 15/11/2026): shown, not editable. Other regions come with the translations.
    val regionName = if (region == Region.FRANCE) stringResource(Res.string.region_fr) else region.code
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(Res.string.settings_region), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(regionName, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AboutPanel(appVersion: String?, eventCount: Int?) {
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
    Text(stringResource(Res.string.about_privacy), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
}
