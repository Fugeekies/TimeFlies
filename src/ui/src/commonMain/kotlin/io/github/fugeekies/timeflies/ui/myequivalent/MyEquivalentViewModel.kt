package io.github.fugeekies.timeflies.ui.myequivalent

import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.EquivalentDirection
import io.github.fugeekies.timeflies.domain.model.EquivalentSearch
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.FindEquivalent
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.equivalences.GenerationsUiState
import io.github.fugeekies.timeflies.ui.equivalences.GenerationsViewModel
import io.github.fugeekies.timeflies.ui.equivalences.TemporaryAgesStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * What the Mon équivalent screen shows (docs/UISpec/MyEquivalent.template.md).
 *
 * @property results search results; null while the query has fewer than 2 characters (the list by family shows).
 * @property byFamily every event by family, the most recent first; null until loaded.
 * @property chosen the chosen event; null while searching.
 * @property search the result for [chosen]; null while computing, without a chosen event or in reduced mode.
 */
data class MyEquivalentUiState(
    val today: LocalDate,
    val region: Region = Region.FRANCE,
    val query: String = "",
    val results: List<Event>? = null,
    val byFamily: Map<Family, List<Event>>? = null,
    val chosen: Event? = null,
    val direction: EquivalentDirection = EquivalentDirection.OTHER_TO_USER,
    val search: EquivalentSearch? = null,
    val error: Boolean = false,
)

/**
 * State of the Mon équivalent screen. The banner and the temporary ages are shared with Equivalences
 * ([GenerationsViewModel], [TemporaryAgesStore]).
 */
class MyEquivalentViewModel(
    private val searchEvents: SearchEvents,
    private val findEquivalent: FindEquivalent,
    observeSettings: ObserveSettings,
    updateSettings: UpdateSettings,
    clock: Clock,
    temporaryAges: TemporaryAgesStore,
) : GenerationsViewModel(observeSettings, updateSettings, clock, temporaryAges) {

    private val _state = MutableStateFlow(MyEquivalentUiState(today = clock.today()))
    val state: StateFlow<MyEquivalentUiState> = _state.asStateFlow()

    private data class Selection(val event: Event?, val direction: EquivalentDirection)

    private val selection = MutableStateFlow(Selection(null, EquivalentDirection.OTHER_TO_USER))
    private val query = MutableStateFlow("")

    init {
        start()
        // The result: recomputed when the people, the event or the direction change.
        viewModelScope.launch {
            combine(generations.filterNotNull(), selection) { people, chosen -> people to chosen }
                .collectLatest { (people, chosen) -> computeResult(people, chosen) }
        }
        // The search: recomputed when the query or the region change.
        viewModelScope.launch {
            combine(query, generations.filterNotNull()) { text, people -> text to people.settings.region }
                .collectLatest { (text, region) ->
                    val results = if (text.trim().length < SearchEvents.MIN_QUERY_LENGTH) null else runCatchingLogged { searchEvents.search(text, region) }
                    _state.update { it.copy(query = text, results = results) }
                }
        }
    }

    override suspend fun onGenerations(generations: GenerationsUiState) {
        val region = generations.settings.region
        val current = _state.value
        if (current.byFamily == null || current.region != region) {
            val byFamily = runCatchingLogged { searchEvents.byFamily(region) }
            _state.update { it.copy(region = region, byFamily = byFamily, error = byFamily == null) }
        }
    }

    private suspend fun computeResult(people: GenerationsUiState, chosen: Selection) {
        _state.update { it.copy(today = clock.today(), chosen = chosen.event, direction = chosen.direction, search = null) }
        val event = chosen.event ?: return
        val userBirth = people.userBirth ?: return
        val (from, to) = when (chosen.direction) {
            EquivalentDirection.OTHER_TO_USER -> people.otherBirth to userBirth
            EquivalentDirection.USER_TO_OTHER -> userBirth to people.otherBirth
        }
        val search = runCatchingLogged { findEquivalent(event, from, to, people.settings.region) }
        _state.update { it.copy(search = search, error = search == null) }
    }

    private suspend fun <T> runCatchingLogged(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        log.e(e) { "Mon équivalent could not be computed" }
        null
    }

    fun onQueryChange(text: String) {
        query.value = text
        _state.update { it.copy(query = text) }
    }

    fun choose(event: Event) {
        selection.update { it.copy(event = event) }
    }

    /** Opened from a card of Today or Equivalences, with the event and the direction already chosen. */
    fun open(event: Event, direction: EquivalentDirection) {
        query.value = ""
        selection.value = Selection(event, direction)
    }

    fun setDirection(direction: EquivalentDirection) {
        selection.update { it.copy(direction = direction) }
    }

    /** "Changer d'événement": back to the search. */
    fun changeEvent() {
        selection.update { it.copy(event = null) }
    }
}
