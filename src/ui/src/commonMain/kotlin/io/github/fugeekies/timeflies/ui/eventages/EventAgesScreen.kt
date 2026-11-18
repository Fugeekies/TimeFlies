package io.github.fugeekies.timeflies.ui.eventages

import io.github.fugeekies.timeflies.ui.text.shownTitle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.ui.equivalences.CenteredText
import io.github.fugeekies.timeflies.ui.equivalences.GenerationsBanner
import io.github.fugeekies.timeflies.ui.equivalences.Message
import io.github.fugeekies.timeflies.ui.equivalences.Person
import io.github.fugeekies.timeflies.ui.equivalences.PersonIcon
import io.github.fugeekies.timeflies.ui.equivalences.ScreenTitle
import io.github.fugeekies.timeflies.ui.equivalences.ThemesFilter
import io.github.fugeekies.timeflies.ui.equivalences.color
import io.github.fugeekies.timeflies.ui.equivalences.spokenName
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.equivalences_choose_ages
import io.github.fugeekies.timeflies.ui.resources.event_ages_a11y_line
import io.github.fugeekies.timeflies.ui.resources.event_ages_no_results
import io.github.fugeekies.timeflies.ui.resources.event_ages_no_themes
import io.github.fugeekies.timeflies.ui.resources.event_ages_reduced
import io.github.fugeekies.timeflies.ui.resources.event_ages_search_hint
import io.github.fugeekies.timeflies.ui.resources.event_ages_title
import io.github.fugeekies.timeflies.ui.resources.kind_and_date
import io.github.fugeekies.timeflies.ui.resources.kind_and_year
import io.github.fugeekies.timeflies.ui.resources.load_error
import io.github.fugeekies.timeflies.ui.resources.loading
import io.github.fugeekies.timeflies.ui.resources.open_settings
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.text.signedYearsText
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.accent
import org.jetbrains.compose.resources.stringResource

/**
 * The "Quel âge ?" screen (docs/UISpec/EventAges.template.md, decision of 15/11/2026): every event of the list shows
 * how old the user and each added age were on its date, negative before a birth ("−14 ans"), without having to touch it.
 *
 * @param request an event to show, from a card of Today or Equivalences; [onRequestHandled] is called once it is taken.
 */
@Composable
fun EventAgesScreen(
    viewModel: EventAgesViewModel,
    request: Event?,
    onRequestHandled: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(request) {
        if (request != null) {
            viewModel.open(request)
            onRequestHandled()
        }
    }
    val generations by viewModel.generations.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val people = generations
    if (people == null) {
        CenteredText(stringResource(Res.string.loading), modifier)
        return
    }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.fillMaxSize().widthIn(max = 720.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item { ScreenTitle(stringResource(Res.string.event_ages_title)) }
            item {
                if (people.reducedMode) {
                    ReducedHeader(onOpenSettings, viewModel::startTemporaryAges)
                } else {
                    GenerationsBanner(viewModel, people)
                }
            }
            item {
                ThemesFilter(
                    state.families,
                    onFamily = viewModel::setFamily,
                    onAll = viewModel::setAllFamilies,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::onQueryChange,
                    placeholder = { Text(stringResource(Res.string.event_ages_search_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                )
            }
            eventList(state)
        }
    }
}

/** Search results, or every event by family when the query is shorter than 2 characters. */
private fun LazyListScope.eventList(state: EventAgesUiState) {
    val results = state.results
    val byFamily = state.byFamily
    when {
        state.families.isEmpty() -> item { Message(stringResource(Res.string.event_ages_no_themes)) }
        results != null && results.isEmpty() -> item { Message(stringResource(Res.string.event_ages_no_results)) }
        results != null -> items(results, key = { "r:${it.event.id}" }) { LineRow(it, state.region) }
        state.error && byFamily == null -> item { Message(stringResource(Res.string.load_error)) }
        byFamily == null -> item { Message(stringResource(Res.string.loading)) }
        else -> byFamily.forEach { (family, lines) ->
            item(key = "f:$family") {
                Row(Modifier.padding(top = 12.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(family), CircleShape))
                    Text(
                        familyLabel(family).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics { heading() },
                    )
                }
            }
            items(lines, key = { "e:${it.event.id}" }) { LineRow(it, state.region) }
        }
    }
}

/**
 * Family dot, title, "nature · année", and under them the ages side by side in the order of the banner (decision of
 * 17/11/2026): "👤 6 ans  👤 −14 ans  👤 36 ans".
 */
@Composable
private fun LineRow(line: EventLine, region: Region) {
    val colors = MaterialTheme.colorScheme
    val event = line.event
    val date = line.ages?.date ?: event.dateFor(region)
    val dates = rememberDateTexts()
    val base = "${event.shownTitle}, ${stringResource(Res.string.kind_and_date, kindLabel(event.kind), dates.long(date))}."
    val ages = line.ages
    val spoken = ages?.ages?.mapIndexed { index, years ->
        stringResource(Res.string.event_ages_a11y_line, Person(index).spokenName(), signedYearsText(years))
    }
    val description = if (spoken == null) base else (listOf(base) + spoken).joinToString(" ")
    Column {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp).clearAndSetSemantics { contentDescription = description },
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(Modifier.padding(top = 7.dp).size(10.dp).background(LocalStyle.current.familyColors.getValue(event.family), CircleShape))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(event.shownTitle, style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    stringResource(Res.string.kind_and_year, kindLabel(event.kind), date.year),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
                if (ages != null) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ages.ages.forEachIndexed { index, years -> AgeAtEvent(Person(index), years) }
                    }
                }
            }
        }
        HorizontalDivider(color = colors.outlineVariant)
    }
}

/** A person's pictogram and their age on the event's date, handwritten in their colour. */
@Composable
private fun AgeAtEvent(person: Person, years: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        PersonIcon(person, size = 16)
        Text(signedYearsText(years), style = LocalStyle.current.accent(20f), color = person.color())
    }
}

/** Without birth nor temporary ages: the list shows without ages, under this message. */
@Composable
private fun ReducedHeader(onOpenSettings: () -> Unit, onChooseAges: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(Res.string.event_ages_reduced),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onOpenSettings) { Text(stringResource(Res.string.open_settings)) }
            OutlinedButton(onClick = onChooseAges) { Text(stringResource(Res.string.equivalences_choose_ages)) }
        }
    }
}
