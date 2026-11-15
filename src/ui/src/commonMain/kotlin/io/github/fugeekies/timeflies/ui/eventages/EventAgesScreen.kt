package io.github.fugeekies.timeflies.ui.eventages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventAges
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.ui.equivalences.CenteredText
import io.github.fugeekies.timeflies.ui.equivalences.GenerationsBanner
import io.github.fugeekies.timeflies.ui.equivalences.GenerationsUiState
import io.github.fugeekies.timeflies.ui.equivalences.Message
import io.github.fugeekies.timeflies.ui.equivalences.Person
import io.github.fugeekies.timeflies.ui.equivalences.PersonIcon
import io.github.fugeekies.timeflies.ui.equivalences.ScreenTitle
import io.github.fugeekies.timeflies.ui.equivalences.TypeHeader
import io.github.fugeekies.timeflies.ui.equivalences.color
import io.github.fugeekies.timeflies.ui.equivalences.spokenName
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.equivalences_choose_ages
import io.github.fugeekies.timeflies.ui.resources.event_ages_a11y_person
import io.github.fugeekies.timeflies.ui.resources.event_ages_change
import io.github.fugeekies.timeflies.ui.resources.event_ages_no_results
import io.github.fugeekies.timeflies.ui.resources.event_ages_search_hint
import io.github.fugeekies.timeflies.ui.resources.event_ages_title
import io.github.fugeekies.timeflies.ui.resources.kind_and_date
import io.github.fugeekies.timeflies.ui.resources.kind_and_year
import io.github.fugeekies.timeflies.ui.resources.load_error
import io.github.fugeekies.timeflies.ui.resources.loading
import io.github.fugeekies.timeflies.ui.resources.open_settings
import io.github.fugeekies.timeflies.ui.resources.event_ages_reduced
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.text.signedYearsText
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import org.jetbrains.compose.resources.stringResource

/**
 * The "Quel âge ?" screen (docs/UISpec/EventAges.template.md, decision of 15/11/2026): choose an event, see how old
 * the two people were on its date, negative before a birth ("−14 ans").
 *
 * @param request an event to open, from a card of Today or Equivalences; [onRequestHandled] is called once it is taken.
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
            viewModel.choose(request)
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
    val dates = rememberDateTexts()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.fillMaxSize().widthIn(max = 720.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ScreenTitle(stringResource(Res.string.event_ages_title)) }
            if (!people.reducedMode) item { GenerationsBanner(viewModel, people) }
            val chosen = state.chosen
            if (chosen == null) {
                item {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::onQueryChange,
                        placeholder = { Text(stringResource(Res.string.event_ages_search_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                eventList(state, onChoose = viewModel::choose)
            } else {
                result(state, people, chosen, dates, viewModel, onOpenSettings)
            }
        }
    }
}

/** Search results, or every event by family when the query is shorter than 2 characters. */
private fun LazyListScope.eventList(state: EventAgesUiState, onChoose: (Event) -> Unit) {
    val results = state.results
    val byFamily = state.byFamily
    when {
        results != null && results.isEmpty() -> item { Message(stringResource(Res.string.event_ages_no_results)) }
        results != null -> items(results, key = { "r:${it.id}" }) { EventRow(it, state.region, onChoose) }
        state.error && byFamily == null -> item { Message(stringResource(Res.string.load_error)) }
        byFamily == null -> item { Message(stringResource(Res.string.loading)) }
        else -> byFamily.forEach { (family, events) ->
            item(key = "f:$family") {
                Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(family), CircleShape))
                    Text(
                        familyLabel(family).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics { heading() },
                    )
                }
            }
            items(events, key = { "e:${it.id}" }) { EventRow(it, state.region, onChoose) }
        }
    }
}

/** Family dot, title, "nature · année". */
@Composable
private fun EventRow(event: Event, region: Region, onChoose: (Event) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button) { onChoose(event) }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(event.family), CircleShape))
        Column(Modifier.weight(1f)) {
            Text(event.title("fr"), style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                stringResource(Res.string.kind_and_year, kindLabel(event.kind), event.dateFor(region).year),
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

private fun LazyListScope.result(
    state: EventAgesUiState,
    people: GenerationsUiState,
    chosen: Event,
    dates: DateTexts,
    viewModel: EventAgesViewModel,
    onOpenSettings: () -> Unit,
) {
    val ages = state.ages
    when {
        people.reducedMode -> {
            item { EventCard(chosen, chosen.dateFor(state.region), dates, ages = null, people = people) }
            item { ReducedResult(onOpenSettings, viewModel::startTemporaryAges) }
        }
        ages == null || ages.event.id != chosen.id -> item { Message(stringResource(Res.string.loading)) }
        else -> item { EventCard(chosen, ages.date, dates, ages, people) }
    }
    item {
        OutlinedButton(onClick = viewModel::changeEvent, modifier = Modifier.padding(top = 8.dp)) {
            Text(stringResource(Res.string.event_ages_change))
        }
    }
}

/** The chosen event, then for each person: pictogram, age today → age on the event's date. */
@Composable
private fun EventCard(event: Event, date: kotlinx.datetime.LocalDate, dates: DateTexts, ages: EventAges?, people: GenerationsUiState) {
    val colors = MaterialTheme.colorScheme
    Surface(color = colors.surface, shape = RoundedCornerShape(6.dp), shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TypeHeader(event)
            Text(event.title("fr"), style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
            Text(
                stringResource(Res.string.kind_and_date, kindLabel(event.kind), dates.long(date)),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
            if (ages != null) {
                PersonAge(Person.USER, today = people.userAge ?: 0, atEvent = ages.userAge)
                PersonAge(Person.OTHER, today = people.otherAge, atEvent = ages.otherAge)
            }
        }
    }
}

/** "👤 35 ans → 6 ans": the age today, small, then the age on the event's date, handwritten in the person's colour. */
@Composable
private fun PersonAge(person: Person, today: Int, atEvent: Int) {
    val todayText = signedYearsText(today)
    val atEventText = signedYearsText(atEvent)
    val description = stringResource(Res.string.event_ages_a11y_person, person.spokenName(), todayText, atEventText)
    Row(
        Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PersonIcon(person, size = 28)
        Text(todayText, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("→", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(atEventText, fontFamily = LocalStyle.current.handwriting, fontWeight = FontWeight.Bold, fontSize = 34.sp, color = person.color())
    }
}

/** Without birth nor temporary ages, in place of the ages; the search stays usable. */
@Composable
private fun ReducedResult(onOpenSettings: () -> Unit, onChooseAges: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(Res.string.event_ages_reduced),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onOpenSettings) { Text(stringResource(Res.string.open_settings)) }
        OutlinedButton(onClick = onChooseAges) { Text(stringResource(Res.string.equivalences_choose_ages)) }
    }
}
