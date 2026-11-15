package io.github.fugeekies.timeflies.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.ui.components.BirthFields
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.onboarding_birth
import io.github.fugeekies.timeflies.ui.resources.onboarding_intro_1
import io.github.fugeekies.timeflies.ui.resources.onboarding_intro_2
import io.github.fugeekies.timeflies.ui.resources.onboarding_intro_3
import io.github.fugeekies.timeflies.ui.resources.onboarding_intro_friends
import io.github.fugeekies.timeflies.ui.resources.onboarding_intro_happy_days
import io.github.fugeekies.timeflies.ui.resources.onboarding_later
import io.github.fugeekies.timeflies.ui.resources.onboarding_privacy
import io.github.fugeekies.timeflies.ui.resources.onboarding_start
import io.github.fugeekies.timeflies.ui.resources.onboarding_title
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
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
                fontFamily = LocalStyle.current.handwriting,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                lineHeight = 48.sp,
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
    val bold = SpanStyle(fontWeight = FontWeight.Bold)
    val part1 = stringResource(Res.string.onboarding_intro_1)
    val friends = stringResource(Res.string.onboarding_intro_friends)
    val part2 = stringResource(Res.string.onboarding_intro_2)
    val happyDays = stringResource(Res.string.onboarding_intro_happy_days)
    val part3 = stringResource(Res.string.onboarding_intro_3)
    Text(
        buildAnnotatedString {
            append(part1)
            withStyle(bold) { append(friends) }
            append(part2)
            withStyle(bold) { append(happyDays) }
            append(part3)
        },
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}
