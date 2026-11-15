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
 * @property reversed false: the other person on the left; true: the user on the left.
 * @property pairs the pairs, left person's events from the most recent; null while computing or in reduced mode.
 */
data class EquivalencesUiState(
    val reversed: Boolean,
    val families: Set<Family>,
    val pairs: List<Equivalence>?,
    val error: Boolean = false,
)

/**
 * State of the Equivalences screen (docs/UISpec/Equivalences.template.md). The banner comes from
 * [GenerationsViewModel]; the direction and the themes are saved as settings.
 */
class EquivalencesViewModel(
    private val getEquivalences: GetEquivalences,
    observeSettings: ObserveSettings,
    private val updateSettings: UpdateSettings,
    clock: Clock,
    temporaryAges: TemporaryAgesStore,
) : GenerationsViewModel(observeSettings, updateSettings, clock, temporaryAges) {

    private val _state = MutableStateFlow<EquivalencesUiState?>(null)
    val state: StateFlow<EquivalencesUiState?> = _state.asStateFlow()

    init {
        start()
    }

    override suspend fun onGenerations(generations: GenerationsUiState) {
        val settings = generations.settings
        val userBirth = generations.userBirth
        val base = EquivalencesUiState(
            reversed = settings.equivalencesReversed,
            families = settings.equivalenceFamilies,
            // Keep the previous list on screen while the new one is computed, so the page does not flicker.
            pairs = _state.value?.pairs?.takeIf { userBirth != null },
        )
        _state.value = base
        if (userBirth == null) return
        _state.value = try {
            val (left, right) = if (settings.equivalencesReversed) userBirth to generations.otherBirth else generations.otherBirth to userBirth
            base.copy(pairs = getEquivalences(left, right, settings.equivalenceFamilies, settings.region))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.e(e) { "Equivalences could not be computed" }
            base.copy(pairs = null, error = true)
        }
    }

    fun reverse() {
        val current = _state.value ?: return
        viewModelScope.launch { updateSettings.setEquivalencesReversed(!current.reversed) }
    }

    fun setFamily(family: Family, checked: Boolean) {
        val current = _state.value ?: return
        val families = if (checked) current.families + family else current.families - family
        viewModelScope.launch { updateSettings.setEquivalenceFamilies(families) }
    }

    fun setAllFamilies(checked: Boolean) {
        viewModelScope.launch { updateSettings.setEquivalenceFamilies(if (checked) Family.entries.toSet() else emptySet()) }
    }
}
