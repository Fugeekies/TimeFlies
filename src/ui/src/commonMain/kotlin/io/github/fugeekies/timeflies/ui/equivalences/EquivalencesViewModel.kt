package io.github.fugeekies.timeflies.ui.equivalences

import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.Equivalence
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The list of the Equivalences screen.
 *
 * @property cards one card per event of the user, the most recent first, with the equivalent of each added age; empty
 *   without an added age; null while computing or in reduced mode.
 */
data class EquivalencesUiState(
    val families: Set<Family>,
    val cards: List<Equivalence>?,
    val error: Boolean = false,
)

/**
 * State of the Equivalences screen (docs/UISpec/Equivalences.template.md). The banner comes from
 * [GenerationsViewModel]; the themes are saved as settings.
 */
class EquivalencesViewModel(
    private val getEquivalences: GetEquivalences,
    observeSettings: ObserveSettings,
    private val updateSettings: UpdateSettings,
    clock: Clock,
    temporaryAges: TemporaryAgesStore,
) : GenerationsViewModel(observeSettings, clock, temporaryAges) {

    private val _state = MutableStateFlow<EquivalencesUiState?>(null)
    val state: StateFlow<EquivalencesUiState?> = _state.asStateFlow()

    init {
        start()
    }

    override suspend fun onGenerations(generations: GenerationsUiState) {
        val settings = generations.settings
        val userBirth = generations.userBirth
        val base = EquivalencesUiState(
            families = settings.equivalenceFamilies,
            // Keep the previous list on screen while the new one is computed, so the page does not flicker.
            cards = _state.value?.cards?.takeIf { userBirth != null && generations.otherBirths.isNotEmpty() },
        )
        _state.value = base
        if (userBirth == null) return
        _state.value = try {
            base.copy(cards = getEquivalences(userBirth, generations.otherBirths, settings.equivalenceFamilies, settings.region))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.e(e) { "Equivalences could not be computed" }
            base.copy(cards = null, error = true)
        }
    }

    fun setFamily(family: Family, checked: Boolean) {
        val current = _state.value ?: return
        val families = if (checked) current.families + family else current.families - family
        viewModelScope.launch { updateSettings.setEquivalenceFamilies(families) }
    }

    fun setAllFamilies(checked: Boolean) {
        viewModelScope.launch { updateSettings.setEquivalenceFamilies(if (checked) Family.THEMES.toSet() else emptySet()) }
    }
}
