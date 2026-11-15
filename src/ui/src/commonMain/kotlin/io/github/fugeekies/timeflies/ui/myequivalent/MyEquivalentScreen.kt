package io.github.fugeekies.timeflies.ui.myequivalent

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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.EquivalentCandidate
import io.github.fugeekies.timeflies.domain.model.EquivalentDirection
import io.github.fugeekies.timeflies.domain.model.EquivalentSearch
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.ui.equivalences.CenteredText
import io.github.fugeekies.timeflies.ui.equivalences.EventSide
import io.github.fugeekies.timeflies.ui.equivalences.GenerationsBanner
import io.github.fugeekies.timeflies.ui.equivalences.GenerationsUiState
import io.github.fugeekies.timeflies.ui.equivalences.Message
import io.github.fugeekies.timeflies.ui.equivalences.Person
import io.github.fugeekies.timeflies.ui.equivalences.PersonIcon
import io.github.fugeekies.timeflies.ui.equivalences.ScreenTitle
import io.github.fugeekies.timeflies.ui.equivalences.TypeHeader
import io.github.fugeekies.timeflies.ui.equivalences.describeSide
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.equivalences_choose_ages
import io.github.fugeekies.timeflies.ui.resources.kind_and_year
import io.github.fugeekies.timeflies.ui.resources.load_error
import io.github.fugeekies.timeflies.ui.resources.loading
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_change
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_choice
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_closest
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_direction_other_to_user
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_direction_user_to_other
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_earlier
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_equivalent
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_future
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_later
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_no_results
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_none
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_others
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_same_day
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_same_time
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_search_hint
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_title
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_too_early
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_too_late
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_within_days
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_within_months
import io.github.fugeekies.timeflies.ui.resources.my_equivalent_within_years
import io.github.fugeekies.timeflies.ui.resources.open_settings
import io.github.fugeekies.timeflies.ui.resources.reduced_mode_message
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

/** A request to open the screen on an event, from a card of Today or Equivalences. */
data class EquivalentRequest(val event: Event, val direction: EquivalentDirection)

/**
 * The Mon équivalent screen (docs/UISpec/MyEquivalent.template.md): the direction drawn with the two coloured
 * pictograms (decision of 15/11/2026), the search, then the result.
 *
 * @param request an event to open, from another screen; [onRequestHandled] is called once it is taken.
 */
@Composable
fun MyEquivalentScreen(
    viewModel: MyEquivalentViewModel,
    request: EquivalentRequest?,
    onRequestHandled: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(request) {
        if (request != null) {
            viewModel.open(request.event, request.direction)
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
            item { ScreenTitle(stringResource(Res.string.my_equivalent_title)) }
            if (!people.reducedMode) item { GenerationsBanner(viewModel, people) }
            item { DirectionChoice(state.direction, viewModel::setDirection) }
            val chosen = state.chosen
            if (chosen == null) {
                item {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::onQueryChange,
                        placeholder = { Text(stringResource(Res.string.my_equivalent_search_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                eventList(state, dates, onChoose = viewModel::choose)
            } else {
                result(state, people, chosen, dates, viewModel, onOpenSettings)
            }
        }
    }
}

/** The two directions, each drawn "pictogram → pictogram" without text; read aloud by screen readers. */
@Composable
private fun DirectionChoice(direction: EquivalentDirection, onChange: (EquivalentDirection) -> Unit) {
    val options = EquivalentDirection.entries
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val (from, to) = people(option)
            val description = stringResource(
                if (option == EquivalentDirection.OTHER_TO_USER) Res.string.my_equivalent_direction_other_to_user else Res.string.my_equivalent_direction_user_to_other,
            )
            SegmentedButton(
                selected = option == direction,
                onClick = { onChange(option) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = {},
                modifier = Modifier.semantics { contentDescription = description },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PersonIcon(from, size = 22)
                    Text("→", style = MaterialTheme.typography.titleMedium)
                    PersonIcon(to, size = 22)
                }
            }
        }
    }
}

/** The person of the chosen event, then the person of the equivalent. */
private fun people(direction: EquivalentDirection): Pair<Person, Person> = when (direction) {
    EquivalentDirection.OTHER_TO_USER -> Person.OTHER to Person.USER
    EquivalentDirection.USER_TO_OTHER -> Person.USER to Person.OTHER
}

/** Search results, or every event by family when the query is shorter than 2 characters. */
private fun LazyListScope.eventList(state: MyEquivalentUiState, dates: DateTexts, onChoose: (Event) -> Unit) {
    val results = state.results
    when {
        results != null && results.isEmpty() -> item { Message(stringResource(Res.string.my_equivalent_no_results)) }
        results != null -> items(results, key = { "r:${it.id}" }) { EventRow(it, state.region, onChoose) }
        state.error && state.byFamily == null -> item { Message(stringResource(Res.string.load_error)) }
        state.byFamily == null -> item { Message(stringResource(Res.string.loading)) }
        else -> state.byFamily.forEach { (family, events) ->
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
    state: MyEquivalentUiState,
    people: GenerationsUiState,
    chosen: Event,
    dates: DateTexts,
    viewModel: MyEquivalentViewModel,
    onOpenSettings: () -> Unit,
) {
    val (fromPerson, toPerson) = people(state.direction)
    val search = state.search
    when {
        people.reducedMode -> item { ReducedResult(onOpenSettings, viewModel::startTemporaryAges) }
        state.error && search == null -> item { Message(stringResource(Res.string.load_error)) }
        search == null || search.chosen.event.id != chosen.id -> item { Message(stringResource(Res.string.loading)) }
        else -> {
            item { SectionLabel(stringResource(Res.string.my_equivalent_choice)) }
            item { ResultCard(fromPerson, search.chosen.event, search.chosen, dates) }
            item { GapLine(search, people.gap ?: 0) }
            resultBody(search, state, toPerson, dates)
        }
    }
    item {
        OutlinedButton(onClick = viewModel::changeEvent, modifier = Modifier.padding(top = 8.dp)) {
            Text(stringResource(Res.string.my_equivalent_change))
        }
    }
}

private fun LazyListScope.resultBody(search: EquivalentSearch, state: MyEquivalentUiState, toPerson: Person, dates: DateTexts) {
    val best = search.best
    when {
        search.isTargetInFuture(state.today) -> item { Message(stringResource(Res.string.my_equivalent_future)) }
        best != null -> {
            item { SectionLabel(stringResource(Res.string.my_equivalent_equivalent)) }
            item { ResultCard(toPerson, best.side.event, best.side, dates) }
            if (search.others.isNotEmpty()) {
                item { SectionLabel(stringResource(Res.string.my_equivalent_others)) }
                items(search.others, key = { "o:${it.side.event.id}" }) { CandidateRow(it, toPerson, dates, within = true) }
            }
        }
        else -> {
            item { Message(stringResource(Res.string.my_equivalent_none)) }
            if (search.outside.isNotEmpty()) {
                item { SectionLabel(stringResource(Res.string.my_equivalent_closest)) }
                items(search.outside, key = { "x:${it.side.event.id}" }) { CandidateRow(it, toPerson, dates, within = false) }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp).semantics { heading() },
    )
}

@Composable
private fun ResultCard(person: Person, event: Event, side: io.github.fugeekies.timeflies.domain.model.EquivalenceSide, dates: DateTexts) {
    val description = describeSide(person, side, dates)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = description },
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TypeHeader(event)
            EventSide(person, side, dates)
        }
    }
}

/** "↓ 20 ans plus tôt", from the chosen event to the target date. */
@Composable
private fun GapLine(search: EquivalentSearch, gap: Int) {
    val text = when {
        gap == 0 -> stringResource(Res.string.my_equivalent_same_time)
        search.target < search.chosen.date -> pluralStringResource(Res.plurals.my_equivalent_earlier, gap, gap)
        else -> pluralStringResource(Res.plurals.my_equivalent_later, gap, gap)
    }
    Text(
        "↓  $text",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** A candidate: title, nature · year, and how far it is from the target ("à 8 mois près", "4 ans trop tôt"). */
@Composable
private fun CandidateRow(candidate: EquivalentCandidate, person: Person, dates: DateTexts, within: Boolean) {
    val colors = MaterialTheme.colorScheme
    val event = candidate.side.event
    val offset = candidate.offset
    val years = abs(offset.years)
    val months = abs(offset.months)
    val days = abs(offset.days)
    // A DatePeriod keeps one sign for all its parts: negative means the candidate is before the target date.
    val earlier = offset.years < 0 || offset.months < 0 || offset.days < 0
    val distance = when {
        !within && earlier -> pluralStringResource(Res.plurals.my_equivalent_too_early, years, years)
        !within -> pluralStringResource(Res.plurals.my_equivalent_too_late, years, years)
        years > 0 -> pluralStringResource(Res.plurals.my_equivalent_within_years, years, years)
        months > 0 -> pluralStringResource(Res.plurals.my_equivalent_within_months, months, months)
        days > 0 -> pluralStringResource(Res.plurals.my_equivalent_within_days, days, days)
        else -> stringResource(Res.string.my_equivalent_same_day)
    }
    val description = "${describeSide(person, candidate.side, dates)} $distance."
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp).clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(event.family), CircleShape))
        Column(Modifier.weight(1f)) {
            Text(event.title("fr"), style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                stringResource(Res.string.kind_and_year, kindLabel(event.kind), candidate.side.date.year),
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Text(distance, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
    }
}

/** Without birth nor temporary ages, in place of the result; the search stays usable. */
@Composable
private fun ReducedResult(onOpenSettings: () -> Unit, onChooseAges: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
