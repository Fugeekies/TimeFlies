package io.github.fugeekies.timeflies.ui.equivalences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Equivalence
import io.github.fugeekies.timeflies.domain.model.EquivalenceSide
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.cancel
import io.github.fugeekies.timeflies.ui.resources.equivalences_a11y_person_age
import io.github.fugeekies.timeflies.ui.resources.equivalences_a11y_side
import io.github.fugeekies.timeflies.ui.resources.equivalences_age_saved
import io.github.fugeekies.timeflies.ui.resources.equivalences_age_temporary
import io.github.fugeekies.timeflies.ui.resources.equivalences_back_to_settings
import io.github.fugeekies.timeflies.ui.resources.equivalences_check_all
import io.github.fugeekies.timeflies.ui.resources.equivalences_choose_ages
import io.github.fugeekies.timeflies.ui.resources.equivalences_edit
import io.github.fugeekies.timeflies.ui.resources.equivalences_empty
import io.github.fugeekies.timeflies.ui.resources.equivalences_gap
import io.github.fugeekies.timeflies.ui.resources.equivalences_gap_none
import io.github.fugeekies.timeflies.ui.resources.equivalences_reverse
import io.github.fugeekies.timeflies.ui.resources.equivalences_small_gap
import io.github.fugeekies.timeflies.ui.resources.equivalences_temporary
import io.github.fugeekies.timeflies.ui.resources.equivalences_themes
import io.github.fugeekies.timeflies.ui.resources.equivalences_themes_summary
import io.github.fugeekies.timeflies.ui.resources.equivalences_title
import io.github.fugeekies.timeflies.ui.resources.equivalences_uncheck_all
import io.github.fugeekies.timeflies.ui.resources.load_error
import io.github.fugeekies.timeflies.ui.resources.loading
import io.github.fugeekies.timeflies.ui.resources.ok
import io.github.fugeekies.timeflies.ui.resources.open_settings
import io.github.fugeekies.timeflies.ui.resources.person_other
import io.github.fugeekies.timeflies.ui.resources.person_user
import io.github.fugeekies.timeflies.ui.resources.reduced_mode_message
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.personAgeText
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.text.typeLabel
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.TimeFliesIcons
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** Who a pictogram stands for: its colour on screen, its name for screen readers only. */
private enum class Person { USER, OTHER }

@Composable
private fun Person.color(): Color = if (this == Person.USER) LocalStyle.current.userColor else LocalStyle.current.otherColor

@Composable
private fun Person.spokenName(): String = stringResource(if (this == Person.USER) Res.string.person_user else Res.string.person_other)

/** Which age dialog is open. */
private enum class AgeDialog { SAVED_OTHER, TEMPORARY_USER, TEMPORARY_OTHER }

/**
 * The Equivalences screen (docs/UISpec/Equivalences.template.md): the two people shown by a coloured pictogram and
 * their age, no name (decision of 15/11/2026).
 *
 * @param onOpenSettings "Ouvrir les réglages", in reduced mode.
 */
@Composable
fun EquivalencesScreen(viewModel: EquivalencesViewModel, onOpenSettings: () -> Unit, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val smallGapWarning by viewModel.smallGapWarning.collectAsStateWithLifecycle()
    var ageDialog by remember { mutableStateOf<AgeDialog?>(null) }
    var themesOpen by remember { mutableStateOf(false) }

    val current = state
    if (current == null) {
        CenteredText(stringResource(Res.string.loading), modifier)
        return
    }
    if (current.reducedMode) {
        ReducedMode(onOpenSettings, onChooseAges = viewModel::startTemporaryAges, modifier)
        return
    }

    val dates = rememberDateTexts()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val columns = if (maxWidth >= 840.dp) 2 else 1
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    stringResource(Res.string.equivalences_title),
                    fontFamily = LocalStyle.current.handwriting,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() },
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Banner(
                    current,
                    onUserAge = { ageDialog = AgeDialog.TEMPORARY_USER },
                    onOtherAge = { ageDialog = AgeDialog.TEMPORARY_OTHER },
                    onEdit = { ageDialog = AgeDialog.SAVED_OTHER },
                    onReverse = viewModel::reverse,
                    onBackToSettings = viewModel::clearTemporaryAges,
                    onThemes = { themesOpen = true },
                )
            }
            val pairs = current.pairs
            when {
                current.error -> item(span = { GridItemSpan(maxLineSpan) }) { Message(stringResource(Res.string.load_error)) }
                pairs == null -> item(span = { GridItemSpan(maxLineSpan) }) { Message(stringResource(Res.string.loading)) }
                pairs.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) { Message(stringResource(Res.string.equivalences_empty)) }
                else -> items(pairs, key = { "${it.left.event.id}>${it.right.event.id}" }) { pair ->
                    PairCard(pair, leftPerson = if (current.reversed) Person.USER else Person.OTHER, dates)
                }
            }
        }
    }

    when (ageDialog) {
        AgeDialog.SAVED_OTHER -> AgePickerDialog(
            person = Person.OTHER,
            initial = current.savedOtherAge,
            note = stringResource(Res.string.equivalences_age_saved),
            onConfirm = { viewModel.saveOtherAge(it); ageDialog = null },
            onDismiss = { ageDialog = null },
        )
        AgeDialog.TEMPORARY_USER -> AgePickerDialog(
            person = Person.USER,
            initial = current.userAge ?: Generations.DEFAULT_OTHER_AGE,
            note = stringResource(Res.string.equivalences_age_temporary),
            onConfirm = { viewModel.setTemporaryUserAge(it); ageDialog = null },
            onDismiss = { ageDialog = null },
        )
        AgeDialog.TEMPORARY_OTHER -> AgePickerDialog(
            person = Person.OTHER,
            initial = current.otherAge,
            note = stringResource(Res.string.equivalences_age_temporary),
            onConfirm = { viewModel.setTemporaryOtherAge(it); ageDialog = null },
            onDismiss = { ageDialog = null },
        )
        null -> Unit
    }
    if (themesOpen) {
        ThemesDialog(current.families, onFamily = viewModel::setFamily, onAll = viewModel::setAllFamilies, onDismiss = { themesOpen = false })
    }
    if (smallGapWarning) {
        AlertDialog(
            onDismissRequest = viewModel::dismissSmallGapWarning,
            confirmButton = { TextButton(onClick = viewModel::dismissSmallGapWarning) { Text(stringResource(Res.string.ok)) } },
            text = { Text(stringResource(Res.string.equivalences_small_gap), style = MaterialTheme.typography.bodyLarge) },
        )
    }
}

@Composable
private fun CenteredText(text: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Message(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 8.dp),
    )
}

/** Without birth nor temporary ages: the message, "Ouvrir les réglages" and "Choisir des âges". */
@Composable
private fun ReducedMode(onOpenSettings: () -> Unit, onChooseAges: () -> Unit, modifier: Modifier) {
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

/** A person's pictogram, in their colour; silent for screen readers (the text next to it says who it is). */
@Composable
private fun PersonIcon(person: Person, size: Int = 22) {
    Icon(TimeFliesIcons.Person, contentDescription = null, tint = person.color(), modifier = Modifier.size(size.dp))
}

@Composable
private fun Banner(
    state: EquivalencesUiState,
    onUserAge: () -> Unit,
    onOtherAge: () -> Unit,
    onEdit: () -> Unit,
    onReverse: () -> Unit,
    onBackToSettings: () -> Unit,
    onThemes: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Surface(color = colors.surface, shape = RoundedCornerShape(6.dp), shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BannerAge(Person.USER, state.userAge ?: 0, onUserAge)
                Text("·", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant)
                BannerAge(Person.OTHER, state.otherAge, onOtherAge)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                val gap = state.gap ?: 0
                Text(
                    if (gap == 0) stringResource(Res.string.equivalences_gap_none) else pluralStringResource(Res.plurals.equivalences_gap, gap, gap),
                    fontFamily = LocalStyle.current.handwriting,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = colors.primary,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onEdit) { Text(stringResource(Res.string.equivalences_edit)) }
                IconButton(onClick = onReverse) {
                    Icon(TimeFliesIcons.Compare, contentDescription = stringResource(Res.string.equivalences_reverse), tint = colors.onSurface)
                }
            }
            if (state.temporary) {
                FlowRow(verticalArrangement = Arrangement.Center, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(Res.string.equivalences_temporary),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    TextButton(onClick = onBackToSettings) { Text(stringResource(Res.string.equivalences_back_to_settings)) }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onThemes) { Text(stringResource(Res.string.equivalences_themes)) }
                Text(
                    pluralStringResource(Res.plurals.equivalences_themes_summary, state.families.size, state.families.size, Family.entries.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
        }
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

@Composable
private fun PairCard(pair: Equivalence, leftPerson: Person, dates: DateTexts) {
    val colors = MaterialTheme.colorScheme
    val rightPerson = if (leftPerson == Person.USER) Person.OTHER else Person.USER
    val type = typeLabel(pair.type)
    val description = "$type. ${describe(leftPerson, pair.left, dates)} ${describe(rightPerson, pair.right, dates)}"
    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(6.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = description },
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(pair.type.family), CircleShape))
                Text(type, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Side(leftPerson, pair.left, dates, Modifier.weight(1f))
                Text("⇄", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                Side(rightPerson, pair.right, dates, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Side(person: Person, side: EquivalenceSide, dates: DateTexts, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(side.event.title("fr"), style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Text(kindLabel(side.event.kind), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Text(dates.long(side.date), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PersonIcon(person, size = 18)
            Text(
                personAgeText(side.age),
                fontFamily = LocalStyle.current.handwriting,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = person.color(),
            )
        }
    }
}

/** "Toi : Titanic, Sortie au cinéma, 7 janvier 1998, 6 ans." */
@Composable
private fun describe(person: Person, side: EquivalenceSide, dates: DateTexts): String = stringResource(
    Res.string.equivalences_a11y_side,
    person.spokenName(),
    side.event.title("fr"),
    "${kindLabel(side.event.kind)}, ${dates.long(side.date)}",
    personAgeText(side.age),
)

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

/** The themes as check boxes, saved at once (docs/UISpec/Equivalences.template.md, "Filtres"). */
@Composable
private fun ThemesDialog(checked: Set<Family>, onFamily: (Family, Boolean) -> Unit, onAll: (Boolean) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.ok)) } },
        title = { Text(stringResource(Res.string.equivalences_themes)) },
        text = {
            Column {
                Family.entries.forEach { family ->
                    val isChecked = family in checked
                    Row(
                        Modifier.fillMaxWidth().clickable(role = Role.Checkbox) { onFamily(family, !isChecked) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(checked = isChecked, onCheckedChange = null)
                        Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(family), CircleShape))
                        Text(familyLabel(family), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onAll(true) }) { Text(stringResource(Res.string.equivalences_check_all)) }
                    TextButton(onClick = { onAll(false) }) { Text(stringResource(Res.string.equivalences_uncheck_all)) }
                }
            }
        },
    )
}
