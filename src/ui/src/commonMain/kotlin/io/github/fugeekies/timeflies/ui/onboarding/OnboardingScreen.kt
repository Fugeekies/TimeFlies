package io.github.fugeekies.timeflies.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.components.BirthFields
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.event_count
import io.github.fugeekies.timeflies.ui.resources.onboarding_birth
import io.github.fugeekies.timeflies.ui.resources.onboarding_events
import io.github.fugeekies.timeflies.ui.resources.onboarding_events_help
import io.github.fugeekies.timeflies.ui.resources.onboarding_intro
import io.github.fugeekies.timeflies.ui.resources.onboarding_later
import io.github.fugeekies.timeflies.ui.resources.onboarding_privacy
import io.github.fugeekies.timeflies.ui.resources.onboarding_start
import io.github.fugeekies.timeflies.ui.resources.onboarding_title
import io.github.fugeekies.timeflies.ui.text.familyContents
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.accent
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The first launch screen (docs/UISpec/Onboarding.template.md), shown before Today until "C'est parti" or
 * "Plus tard". It asks for the birth month and year only (decision of 15/11/2026). On Windows the content is
 * centred in a 480 dp column. Leaving the app without choosing saves nothing: the screen comes back at the
 * next launch.
 */
@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                stringResource(Res.string.onboarding_title),
                style = LocalStyle.current.accent(48f, lineHeight = 48f),
                
                color = colors.primary,
                modifier = Modifier.semantics { heading() },
            )
            Intro()

            Text(
                stringResource(Res.string.onboarding_birth),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            BirthFields(state.month, state.year, state.birthYears, viewModel::onMonthPicked, viewModel::onYearPicked)
            ThemeChoices(state, viewModel)

            Button(
                onClick = viewModel::onStart,
                enabled = state.canStart,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).heightIn(min = 52.dp),
            ) {
                Text(stringResource(Res.string.onboarding_start), style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = viewModel::onLater, enabled = !state.saving, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(stringResource(Res.string.onboarding_later), color = colors.onBackground)
            }
            Text(
                stringResource(Res.string.onboarding_privacy),
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun Intro() {
    Text(
        stringResource(Res.string.onboarding_intro),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

/**
 * "Ce qui t'intéresse" (decisions of 16/11/2026 and 17/11/2026): one line per theme of the base, all ticked, with
 * its colour, what it holds and its number of events. Unticking one leaves out every group of it; the choice by
 * country is only in Settings › Événements.
 */
@Composable
private fun ThemeChoices(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    if (state.groups.isEmpty()) return
    val colors = MaterialTheme.colorScheme
    val counts = state.familyCounts
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            stringResource(Res.string.onboarding_events),
            style = MaterialTheme.typography.labelSmall,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp).semantics { heading() },
        )
        Text(stringResource(Res.string.onboarding_events_help), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Surface(color = colors.surface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Column {
                state.families.forEachIndexed { index, family ->
                    if (index > 0) HorizontalDivider(color = colors.outlineVariant, modifier = Modifier.padding(start = 36.dp, end = 14.dp))
                    ThemeLine(family, counts[family] ?: 0, checked = family !in state.excludedFamilies) { viewModel.onFamilyToggled(family) }
                }
            }
        }
    }
}

/** A theme: its colour dot, its name, what it holds and its number of events, and a check box; the whole line toggles. */
@Composable
private fun ThemeLine(family: Family, count: Int, checked: Boolean, onToggle: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(start = 14.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(12.dp).background(LocalStyle.current.familyColors.getValue(family), CircleShape))
                Text(familyLabel(family), style = MaterialTheme.typography.titleMedium, color = colors.onSurface, modifier = Modifier.weight(1f, fill = false))
                Text(pluralStringResource(Res.plurals.event_count, count, count), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            Text(familyContents(family), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant, modifier = Modifier.padding(start = 22.dp))
        }
        Checkbox(checked = checked, onCheckedChange = null, modifier = Modifier.padding(12.dp))
    }
}
