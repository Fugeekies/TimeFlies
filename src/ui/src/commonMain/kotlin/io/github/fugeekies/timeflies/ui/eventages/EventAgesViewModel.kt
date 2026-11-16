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

/** One line of the list: an event and the two people's ages on its date; [ages] is null in reduced mode. */
data class EventLine(val event: Event, val ages: EventAges?)

/**
 * What the "Quel âge ?" screen shows (docs/UISpec/EventAges.template.md): every line carries the two ages, no need to
 * touch it (decision of 15/11/2026).
 *
 * @property results search results; null while the query has fewer than 2 characters (the list by family shows).
 * @property byFamily every event by family, the most recent first; null until loaded.
 */
data class EventAgesUiState(
    val region: Region = Region.FRANCE,
    val query: String = "",
    val results: List<EventLine>? = null,
    val byFamily: Map<Family, List<EventLine>>? = null,
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

    private val query = MutableStateFlow("")

    /** Every event by family for [cachedKey], read once per region and event filter. */
    private var cachedByFamily: Map<Family, List<Event>>? = null
    private var cachedKey: Pair<Region, Set<String>>? = null

    init {
        start()
        // The lines: recomputed when the people, the region or the query change.
        viewModelScope.launch {
            combine(generations.filterNotNull(), query) { people, text -> people to text }
                .collectLatest { (people, text) -> load(people, text) }
        }
    }

    /** Nothing to do here: [load] follows the same changes. */
    override suspend fun onGenerations(generations: GenerationsUiState) = Unit

    private suspend fun load(people: GenerationsUiState, text: String) {
        val region = people.settings.region
        // The event filter changes the list too: unticked groups are no longer in it (decision of 16/11/2026).
        val key = region to people.settings.excludedEventGroups
        val byFamily = cachedByFamily.takeIf { cachedKey == key } ?: runCatchingLogged { searchEvents.byFamily(region) }?.also {
            cachedByFamily = it
            cachedKey = key
        }
        val results = if (text.trim().length < SearchEvents.MIN_QUERY_LENGTH) null else runCatchingLogged { searchEvents.search(text, region) }
        fun line(event: Event) = EventLine(event, people.userBirth?.let { getEventAges(event, it, people.otherBirth, region) })
        _state.update {
            it.copy(
                region = region,
                query = text,
                results = results?.map(::line),
                byFamily = byFamily?.mapValues { (_, events) -> events.map(::line) },
                error = byFamily == null,
            )
        }
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

    /** Opened from a card of Today or Equivalences: the event's title in the search, so its line shows at once. */
    fun open(event: Event) = onQueryChange(event.title("fr"))
}
