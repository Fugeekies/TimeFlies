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
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.EquivalenceSide
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.cancel
import io.github.fugeekies.timeflies.ui.resources.equivalences_a11y_none
import io.github.fugeekies.timeflies.ui.resources.equivalences_a11y_person_age
import io.github.fugeekies.timeflies.ui.resources.equivalences_a11y_side
import io.github.fugeekies.timeflies.ui.resources.equivalences_age_temporary
import io.github.fugeekies.timeflies.ui.resources.equivalences_back_to_settings
import io.github.fugeekies.timeflies.ui.resources.equivalences_choose_ages
import io.github.fugeekies.timeflies.ui.resources.equivalences_gap
import io.github.fugeekies.timeflies.ui.resources.equivalences_gap_none
import io.github.fugeekies.timeflies.ui.resources.equivalences_small_gap
import io.github.fugeekies.timeflies.ui.resources.equivalences_temporary
import io.github.fugeekies.timeflies.ui.resources.ok
import io.github.fugeekies.timeflies.ui.resources.open_settings
import io.github.fugeekies.timeflies.ui.resources.people_add
import io.github.fugeekies.timeflies.ui.resources.people_add_hint
import io.github.fugeekies.timeflies.ui.resources.people_remove
import io.github.fugeekies.timeflies.ui.resources.person_number
import io.github.fugeekies.timeflies.ui.resources.person_user
import io.github.fugeekies.timeflies.ui.resources.reduced_mode_message
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.personAgeText
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.TimeFliesIcons
import io.github.fugeekies.timeflies.ui.theme.accent
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Who a pictogram stands for, by its place in the banner: 0 is the user, 1 to 3 the ages added (decision of
 * 17/11/2026). Its colour on screen, its name for screen readers only (decision of 15/11/2026).
 */
@Immutable
internal data class Person(val index: Int) {
    companion object {
        val USER = Person(0)

        /** The person of the [index]-th added age (from 0). */
        fun other(index: Int) = Person(index + 1)
    }
}

@Composable
internal fun Person.color(): Color = LocalStyle.current.personColors[index]

/** "Toi", or "Personne 2" to "Personne 4" for the ages added. */
@Composable
internal fun Person.spokenName(): String =
    if (this == Person.USER) stringResource(Res.string.person_user) else stringResource(Res.string.person_number, index + 1)

/** A person's pictogram, in their colour; silent for screen readers (the text next to it says who it is). */
@Composable
internal fun PersonIcon(person: Person, size: Int = 22) {
    Icon(TimeFliesIcons.Person, contentDescription = null, tint = person.color(), modifier = Modifier.size(size.dp))
}

/** Which age dialog is open: the user's temporary age, an added age to change, or a new one. */
private sealed interface AgeDialog {
    data object User : AgeDialog
    data class Other(val index: Int) : AgeDialog
    data object Add : AgeDialog
}

/**
 * The banner of the ages (docs/UISpec/Equivalences.template.md, "Bandeau des âges"), shared by "Quel âge ?" and
 * Equivalences: the user's age, then up to three ages added with "+", each with its colour (decision of 17/11/2026);
 * the gap with each, the temporary ages and the dialogs.
 *
 * @param footer lines under the banner (the themes on Equivalences).
 */
@Composable
internal fun GenerationsBanner(
    viewModel: GenerationsViewModel,
    generations: GenerationsUiState,
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    var ageDialog by remember { mutableStateOf<AgeDialog?>(null) }
    val smallGapWarning by viewModel.smallGapWarning.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    Surface(color = colors.surface, shape = RoundedCornerShape(6.dp), shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BannerAge(Person.USER, generations.userAge ?: 0) { ageDialog = AgeDialog.User }
                generations.otherAges.forEachIndexed { index, age ->
                    BannerAge(Person.other(index), age) { ageDialog = AgeDialog.Other(index) }
                }
                if (generations.canAddAge) {
                    TextButton(onClick = { ageDialog = AgeDialog.Add }) {
                        Icon(TimeFliesIcons.Plus, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(stringResource(Res.string.people_add), modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
            if (generations.otherAges.isEmpty()) {
                Text(stringResource(Res.string.people_add_hint), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            // The gap with each added age; large when there is only one, as before (decision of 15/11/2026).
            val gapSize = if (generations.gaps.size == 1) 30f else 22f
            generations.gaps.forEachIndexed { index, gap ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PersonIcon(Person.other(index), size = 20)
                    Text(
                        if (gap == 0) stringResource(Res.string.equivalences_gap_none) else pluralStringResource(Res.plurals.equivalences_gap, gap, gap),
                        style = LocalStyle.current.accent(gapSize),
                        color = colors.primary,
                    )
                }
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

    val note = stringResource(Res.string.equivalences_age_temporary)
    when (val dialog = ageDialog) {
        AgeDialog.User -> AgePickerDialog(
            person = Person.USER,
            initial = generations.userAge ?: Generations.DEFAULT_OTHER_AGE,
            note = note,
            onConfirm = { viewModel.setTemporaryUserAge(it); ageDialog = null },
            onDismiss = { ageDialog = null },
        )
        is AgeDialog.Other -> AgePickerDialog(
            person = Person.other(dialog.index),
            initial = generations.otherAges.getOrElse(dialog.index) { Generations.DEFAULT_OTHER_AGE },
            note = note,
            onConfirm = { viewModel.setOtherAge(dialog.index, it); ageDialog = null },
            onDismiss = { ageDialog = null },
            onRemove = { viewModel.removeOtherAge(dialog.index); ageDialog = null },
        )
        AgeDialog.Add -> AgePickerDialog(
            person = Person.other(generations.otherAges.size),
            initial = Generations.DEFAULT_OTHER_AGE,
            note = note,
            onConfirm = { viewModel.addOtherAge(it); ageDialog = null },
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

/** Pictogram and age, touched to change it. */
@Composable
private fun BannerAge(person: Person, age: Int, onClick: () -> Unit) {
    val ageText = pluralStringResource(Res.plurals.years_count, age, age)
    val description = stringResource(Res.string.equivalences_a11y_person_age, person.spokenName(), ageText)
    Row(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PersonIcon(person, size = 26)
        Text(ageText, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

/**
 * Slider from 1 to 100 for one person's age; the value is applied on "OK".
 *
 * @param onRemove "Retirer", for an added age only.
 */
@Composable
private fun AgePickerDialog(
    person: Person,
    initial: Int,
    note: String,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    onRemove: (() -> Unit)? = null,
) {
    val range = Generations.AGE_RANGE
    var value by remember { mutableFloatStateOf(initial.coerceIn(range).toFloat()) }
    val age = value.roundToInt()
    val name = person.spokenName()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onConfirm(age) }) { Text(stringResource(Res.string.ok)) } },
        dismissButton = {
            Row {
                if (onRemove != null) TextButton(onClick = onRemove) { Text(stringResource(Res.string.people_remove)) }
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
            }
        },
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

/**
 * One person's event: title, kind, date, and their pictogram before their age.
 *
 * @param compact more than two columns side by side: a smaller title and the short date ("02/11/2011").
 */
@Composable
internal fun EventSide(person: Person, side: EquivalenceSide, dates: DateTexts, modifier: Modifier = Modifier, compact: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            side.event.title("fr"),
            style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
            color = colors.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (!compact) Text(kindLabel(side.event.kind), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Text(if (compact) dates.short(side.date) else dates.long(side.date), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PersonIcon(person, size = if (compact) 14 else 18)
            Text(
                personAgeText(side.age),
                style = LocalStyle.current.accent(if (compact) 17f else 20f),
                color = person.color(),
            )
        }
    }
}

/** An added age without an equivalent for this event: its pictogram and a dash. */
@Composable
internal fun MissingSide(person: Person, modifier: Modifier = Modifier) {
    Row(modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        PersonIcon(person, size = 14)
        Text("—", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** "Toi : Titanic, Sortie au cinéma, 7 janvier 1998, 6 ans.", or "Personne 3 : pas d'équivalent." */
@Composable
internal fun describeSide(person: Person, side: EquivalenceSide?, dates: DateTexts): String =
    if (side == null) {
        stringResource(Res.string.equivalences_a11y_none, person.spokenName())
    } else {
        stringResource(
            Res.string.equivalences_a11y_side,
            person.spokenName(),
            side.event.title("fr"),
            "${kindLabel(side.event.kind)}, ${dates.long(side.date)}",
            personAgeText(side.age),
        )
    }
