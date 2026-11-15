package io.github.fugeekies.timeflies.ui.eventages

import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventAges
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.GetEventAges
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

/**
 * What the "Quel âge ?" screen shows (docs/UISpec/EventAges.template.md).
 *
 * @property results search results; null while the query has fewer than 2 characters (the list by family shows).
 * @property byFamily every event by family, the most recent first; null until loaded.
 * @property chosen the chosen event; null while searching.
 * @property ages the two people's ages on [chosen]'s date; null without a chosen event or in reduced mode.
 */
data class EventAgesUiState(
    val region: Region = Region.FRANCE,
    val query: String = "",
    val results: List<Event>? = null,
    val byFamily: Map<Family, List<Event>>? = null,
    val chosen: Event? = null,
    val ages: EventAges? = null,
    val error: Boolean = false,
)

/**
 * State of the "Quel âge ?" screen. The banner and the temporary ages are shared with Equivalences
 * ([GenerationsViewModel], [TemporaryAgesStore]).
 */
class EventAgesViewModel(
    private val searchEvents: SearchEvents,
    private val getEventAges: GetEventAges,
    observeSettings: ObserveSettings,
    updateSettings: UpdateSettings,
    clock: Clock,
    temporaryAges: TemporaryAgesStore,
) : GenerationsViewModel(observeSettings, updateSettings, clock, temporaryAges) {

    private val _state = MutableStateFlow(EventAgesUiState())
    val state: StateFlow<EventAgesUiState> = _state.asStateFlow()

    private val chosen = MutableStateFlow<Event?>(null)
    private val query = MutableStateFlow("")

    init {
        start()
        // The ages: recomputed when the people or the event change.
        viewModelScope.launch {
            combine(generations.filterNotNull(), chosen) { people, event -> people to event }
                .collectLatest { (people, event) -> computeAges(people, event) }
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

    private fun computeAges(people: GenerationsUiState, event: Event?) {
        val userBirth = people.userBirth
        val ages = if (event != null && userBirth != null) getEventAges(event, userBirth, people.otherBirth, people.settings.region) else null
        _state.update { it.copy(chosen = event, ages = ages) }
    }

    private suspend fun <T> runCatchingLogged(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        log.e(e) { "Events could not be read" }
        null
    }

    fun onQueryChange(text: String) {
        query.value = text
        _state.update { it.copy(query = text) }
    }

    /** An event chosen in the list, or opened from a card of Today or Equivalences. */
    fun choose(event: Event) {
        chosen.value = event
    }

    /** "Changer d'événement": back to the search. */
    fun changeEvent() {
        chosen.value = null
    }
}
