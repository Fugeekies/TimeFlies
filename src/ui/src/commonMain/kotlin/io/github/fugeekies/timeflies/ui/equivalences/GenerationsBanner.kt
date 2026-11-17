package io.github.fugeekies.timeflies.ui.equivalences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.EquivalenceSide
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.cancel
import io.github.fugeekies.timeflies.ui.resources.equivalences_a11y_person_age
import io.github.fugeekies.timeflies.ui.resources.equivalences_a11y_side
import io.github.fugeekies.timeflies.ui.resources.equivalences_age_saved
import io.github.fugeekies.timeflies.ui.resources.equivalences_age_temporary
import io.github.fugeekies.timeflies.ui.resources.equivalences_back_to_settings
import io.github.fugeekies.timeflies.ui.resources.equivalences_choose_ages
import io.github.fugeekies.timeflies.ui.resources.equivalences_edit
import io.github.fugeekies.timeflies.ui.resources.equivalences_gap
import io.github.fugeekies.timeflies.ui.resources.equivalences_gap_none
import io.github.fugeekies.timeflies.ui.resources.equivalences_small_gap
import io.github.fugeekies.timeflies.ui.resources.equivalences_temporary
import io.github.fugeekies.timeflies.ui.resources.ok
import io.github.fugeekies.timeflies.ui.resources.open_settings
import io.github.fugeekies.timeflies.ui.resources.person_other
import io.github.fugeekies.timeflies.ui.resources.person_user
import io.github.fugeekies.timeflies.ui.resources.reduced_mode_message
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.personAgeText
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.accent
import io.github.fugeekies.timeflies.ui.theme.TimeFliesIcons
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** Who a pictogram stands for: its colour on screen, its name for screen readers only (decision of 15/11/2026). */
internal enum class Person {
    USER, OTHER;

    val other: Person get() = if (this == USER) OTHER else USER
}

@Composable
internal fun Person.color(): Color = if (this == Person.USER) LocalStyle.current.userColor else LocalStyle.current.otherColor

@Composable
internal fun Person.spokenName(): String = stringResource(if (this == Person.USER) Res.string.person_user else Res.string.person_other)

/** A person's pictogram, in their colour; silent for screen readers (the text next to it says who it is). */
@Composable
internal fun PersonIcon(person: Person, size: Int = 22) {
    Icon(TimeFliesIcons.Person, contentDescription = null, tint = person.color(), modifier = Modifier.size(size.dp))
}

/** Which age dialog is open. */
private enum class AgeDialog { SAVED_OTHER, TEMPORARY_USER, TEMPORARY_OTHER }

/**
 * The banner of the two people (docs/UISpec/Equivalences.template.md, "Bandeau des générations"), shared with Mon
 * équivalent: pictograms and ages (touched for temporary ages), gap, "Modifier", temporary ages, and the dialogs.
 *
 * @param trailing buttons after "Modifier" (Inverser on Equivalences).
 * @param footer lines under the banner (the themes on Equivalences).
 */
@Composable
internal fun GenerationsBanner(
    viewModel: GenerationsViewModel,
    generations: GenerationsUiState,
    trailing: @Composable () -> Unit = {},
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    var ageDialog by remember { mutableStateOf<AgeDialog?>(null) }
    val smallGapWarning by viewModel.smallGapWarning.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    Surface(color = colors.surface, shape = RoundedCornerShape(6.dp), shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BannerAge(Person.USER, generations.userAge ?: 0) { ageDialog = AgeDialog.TEMPORARY_USER }
                Text("·", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant)
                BannerAge(Person.OTHER, generations.otherAge) { ageDialog = AgeDialog.TEMPORARY_OTHER }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                val gap = generations.gap ?: 0
                Text(
                    if (gap == 0) stringResource(Res.string.equivalences_gap_none) else pluralStringResource(Res.plurals.equivalences_gap, gap, gap),
                    style = LocalStyle.current.accent(30f),
                    color = colors.primary,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = { ageDialog = AgeDialog.SAVED_OTHER }) { Text(stringResource(Res.string.equivalences_edit)) }
                trailing()
            }
            if (generations.temporary) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(Res.string.equivalences_temporary),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    TextButton(onClick = viewModel::clearTemporaryAges) { Text(stringResource(Res.string.equivalences_back_to_settings)) }
                }
            }
            footer()
        }
    }

    when (ageDialog) {
        AgeDialog.SAVED_OTHER -> AgePickerDialog(
            person = Person.OTHER,
            initial = generations.savedOtherAge,
            note = stringResource(Res.string.equivalences_age_saved),
            onConfirm = { viewModel.saveOtherAge(it); ageDialog = null },
            onDismiss = { ageDialog = null },
        )
        AgeDialog.TEMPORARY_USER -> AgePickerDialog(
            person = Person.USER,
            initial = generations.userAge ?: Generations.DEFAULT_OTHER_AGE,
            note = stringResource(Res.string.equivalences_age_temporary),
            onConfirm = { viewModel.setTemporaryUserAge(it); ageDialog = null },
            onDismiss = { ageDialog = null },
        )
        AgeDialog.TEMPORARY_OTHER -> AgePickerDialog(
            person = Person.OTHER,
            initial = generations.otherAge,
            note = stringResource(Res.string.equivalences_age_temporary),
            onConfirm = { viewModel.setTemporaryOtherAge(it); ageDialog = null },
            onDismiss = { ageDialog = null },
        )
        null -> Unit
    }
    if (smallGapWarning) {
        AlertDialog(
            onDismissRequest = viewModel::dismissSmallGapWarning,
            confirmButton = { TextButton(onClick = viewModel::dismissSmallGapWarning) { Text(stringResource(Res.string.ok)) } },
            text = { Text(stringResource(Res.string.equivalences_small_gap), style = MaterialTheme.typography.bodyLarge) },
        )
    }
}

/** Pictogram and age, touched to pick a temporary age. */
@Composable
private fun BannerAge(person: Person, age: Int, onClick: () -> Unit) {
    val ageText = pluralStringResource(Res.plurals.years_count, age, age)
    val description = stringResource(Res.string.equivalences_a11y_person_age, person.spokenName(), ageText)
    Row(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PersonIcon(person, size = 26)
        Text(ageText, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Slider from 1 to 100 for one person's age; the value is applied on "OK". */
@Composable
private fun AgePickerDialog(person: Person, initial: Int, note: String, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    val range = Generations.AGE_RANGE
    var value by remember { mutableFloatStateOf(initial.coerceIn(range).toFloat()) }
    val age = value.roundToInt()
    val name = person.spokenName()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onConfirm(age) }) { Text(stringResource(Res.string.ok)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonIcon(person, size = 30)
                Text(pluralStringResource(Res.plurals.years_count, age, age), style = MaterialTheme.typography.headlineMedium)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    valueRange = range.first.toFloat()..range.last.toFloat(),
                    steps = range.last - range.first - 1,
                    modifier = Modifier.semantics { contentDescription = name },
                )
                Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
    )
}

/** Without birth nor temporary ages: the message, "Ouvrir les réglages" and "Choisir des âges". */
@Composable
internal fun GenerationsReducedMode(onOpenSettings: () -> Unit, onChooseAges: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(
            Modifier.widthIn(max = 420.dp).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                stringResource(Res.string.reduced_mode_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onOpenSettings) { Text(stringResource(Res.string.open_settings)) }
            OutlinedButton(onClick = onChooseAges) { Text(stringResource(Res.string.equivalences_choose_ages)) }
        }
    }
}

/** One person's event: title, kind, date, and their pictogram before their age. */
@Composable
internal fun EventSide(person: Person, side: EquivalenceSide, dates: DateTexts, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(side.event.title("fr"), style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        Text(kindLabel(side.event.kind), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Text(dates.long(side.date), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PersonIcon(person, size = 18)
            Text(
                personAgeText(side.age),
                style = LocalStyle.current.accent(20f),
                color = person.color(),
            )
        }
    }
}

/** "Toi : Titanic, Sortie au cinéma, 7 janvier 1998, 6 ans." */
@Composable
internal fun describeSide(person: Person, side: EquivalenceSide, dates: DateTexts): String = stringResource(
    Res.string.equivalences_a11y_side,
    person.spokenName(),
    side.event.title("fr"),
    "${kindLabel(side.event.kind)}, ${dates.long(side.date)}",
    personAgeText(side.age),
)
