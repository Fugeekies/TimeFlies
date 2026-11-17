package io.github.fugeekies.timeflies.ui.equivalences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Equivalence
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.equivalences_check_all
import io.github.fugeekies.timeflies.ui.resources.equivalences_empty
import io.github.fugeekies.timeflies.ui.resources.equivalences_no_other
import io.github.fugeekies.timeflies.ui.resources.equivalences_themes
import io.github.fugeekies.timeflies.ui.resources.equivalences_themes_summary
import io.github.fugeekies.timeflies.ui.resources.equivalences_title
import io.github.fugeekies.timeflies.ui.resources.equivalences_uncheck_all
import io.github.fugeekies.timeflies.ui.resources.load_error
import io.github.fugeekies.timeflies.ui.resources.loading
import io.github.fugeekies.timeflies.ui.resources.ok
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.text.typeLabel
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.accent
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The Equivalences screen (docs/UISpec/Equivalences.template.md): the user and up to three added ages, each shown by
 * a coloured pictogram and their age, no name (decisions of 15/11/2026 and 17/11/2026).
 *
 * @param onOpenSettings "Ouvrir les réglages", in reduced mode.
 * @param onOpenEvent a card touched: "Quel âge ?" on the user's event.
 */
@Composable
fun EquivalencesScreen(
    viewModel: EquivalencesViewModel,
    onOpenSettings: () -> Unit,
    onOpenEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    val generations by viewModel.generations.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var themesOpen by remember { mutableStateOf(false) }

    val people = generations
    val list = state
    if (people == null || list == null) {
        CenteredText(stringResource(Res.string.loading), modifier)
        return
    }
    if (people.reducedMode) {
        GenerationsReducedMode(onOpenSettings, onChooseAges = viewModel::startTemporaryAges, modifier)
        return
    }

    val dates = rememberDateTexts()
    BoxWithConstraints(modifier.fillMaxSize()) {
        // Two cards side by side on a wide window, when each card has only two columns.
        val columns = if (maxWidth >= 840.dp && people.otherAges.size <= 1) 2 else 1
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { ScreenTitle(stringResource(Res.string.equivalences_title)) }
            item(span = { GridItemSpan(maxLineSpan) }) {
                GenerationsBanner(
                    viewModel,
                    people,
                    footer = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { themesOpen = true }) { Text(stringResource(Res.string.equivalences_themes)) }
                            Text(
                                pluralStringResource(Res.plurals.equivalences_themes_summary, list.families.size, list.families.size, Family.entries.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )
            }
            val cards = list.cards
            when {
                people.otherAges.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) { Message(stringResource(Res.string.equivalences_no_other)) }
                list.error -> item(span = { GridItemSpan(maxLineSpan) }) { Message(stringResource(Res.string.load_error)) }
                cards == null -> item(span = { GridItemSpan(maxLineSpan) }) { Message(stringResource(Res.string.loading)) }
                cards.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) { Message(stringResource(Res.string.equivalences_empty)) }
                else -> items(cards, key = { it.user.event.id }) { card ->
                    EquivalenceCard(card, dates, onClick = { onOpenEvent(card.user.event) })
                }
            }
        }
    }

    if (themesOpen) {
        ThemesDialog(list.families, onFamily = viewModel::setFamily, onAll = viewModel::setAllFamilies, onDismiss = { themesOpen = false })
    }
}

@Composable
internal fun ScreenTitle(text: String) {
    Text(
        text,
        style = LocalStyle.current.accent(34f),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.semantics { heading() },
    )
}

@Composable
internal fun CenteredText(text: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun Message(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 8.dp),
    )
}

/** Family dot and type name, at the top of a card. */
@Composable
internal fun TypeHeader(event: Event) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(event.family), CircleShape))
        Text(typeLabel(event.type), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * One event of the user and, side by side in the order of the banner, the equivalent of each added age ("—" when
 * there is none, decision of 17/11/2026).
 */
@Composable
private fun EquivalenceCard(card: Equivalence, dates: DateTexts, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val description = buildString {
        append(typeLabel(card.type)).append(". ").append(describeSide(Person.USER, card.user, dates))
        card.others.forEachIndexed { index, side -> append(' ').append(describeSide(Person.other(index), side, dates)) }
    }
    // Up to four columns: from three on, the compact sides (smaller title, short date).
    val compact = card.others.size > 1
    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(6.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = description
                onClick { onClick(); true }
            },
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TypeHeader(card.user.event)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EventSide(Person.USER, card.user, dates, Modifier.weight(1f), compact)
                card.others.forEachIndexed { index, side ->
                    // The arrow between the two columns of a pair, as before; not between three or four.
                    if (!compact) Text("⇄", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                    if (side == null) MissingSide(Person.other(index), Modifier.weight(1f)) else EventSide(Person.other(index), side, dates, Modifier.weight(1f), compact)
                }
            }
        }
    }
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
