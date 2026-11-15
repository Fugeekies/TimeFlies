package io.github.fugeekies.timeflies.ui.equivalences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.Equivalence
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Ages picked on the fly (docs/UISpec/Equivalences.template.md, "Âges à la volée"): never saved, forgotten when the
 * app closes.
 */
data class TemporaryAges(val user: Int, val other: Int)

/**
 * What the Equivalences screen shows.
 *
 * @property userAge the user's age shown in the banner; null in reduced mode (no birth and no temporary ages).
 * @property otherAge the other person's age shown in the banner.
 * @property savedOtherAge the other person's saved age, the start value of "Modifier".
 * @property gap gap between the two births in completed years; null in reduced mode.
 * @property reversed false: the other person on the left; true: the user on the left.
 * @property pairs the pairs, left person's events from the most recent; null while computing or in reduced mode.
 */
data class EquivalencesUiState(
    val userAge: Int?,
    val otherAge: Int,
    val savedOtherAge: Int,
    val temporary: Boolean,
    val gap: Int?,
    val reversed: Boolean,
    val families: Set<Family>,
    val pairs: List<Equivalence>?,
    val error: Boolean = false,
) {
    val reducedMode: Boolean get() = userAge == null
    val isSmallGap: Boolean get() = gap != null && gap < Generations.SMALL_GAP
}

/**
 * State of the Equivalences screen (docs/UISpec/Equivalences.template.md). The other person's age is saved from
 * here only (decision of 15/11/2026); the direction and the themes are saved as settings; ages picked on the fly are
 * kept in memory only.
 */
class EquivalencesViewModel(
    private val getEquivalences: GetEquivalences,
    observeSettings: ObserveSettings,
    private val updateSettings: UpdateSettings,
    private val clock: Clock,
) : ViewModel() {

    private val log = Logger.withTag("Equivalences")

    private val temporaryAges = MutableStateFlow<TemporaryAges?>(null)

    private val _state = MutableStateFlow<EquivalencesUiState?>(null)
    val state: StateFlow<EquivalencesUiState?> = _state.asStateFlow()

    private val _smallGapWarning = MutableStateFlow(false)

    /** The "petit écart" window is open. */
    val smallGapWarning: StateFlow<Boolean> = _smallGapWarning.asStateFlow()

    /** Shown at most once per launch when the screen opens on a small gap; again after each age change giving one. */
    private var warnedOnOpen = false
    private var warnAfterChange = false

    init {
        viewModelScope.launch {
            combine(observeSettings(), temporaryAges, clock.manualChanges().onStart { emit(Unit) }) { settings, temporary, _ ->
                settings to temporary
            }
                .catch { e ->
                    log.e(e) { "Settings could not be read" }
                    _state.value = _state.value?.copy(error = true)
                }
                .collectLatest { (settings, temporary) -> load(settings, temporary) }
        }
    }

    private suspend fun load(settings: Settings, temporary: TemporaryAges?) {
        val today = clock.today()
        val userBirth = userBirth(settings, temporary, today)
        val otherBirth = Generations.birthFromAge(today, temporary?.other ?: settings.otherAge)
        val base = EquivalencesUiState(
            userAge = when {
                temporary != null -> temporary.user
                settings.birth != null -> Generations.userAge(settings.birth!!, today)
                else -> null
            },
            otherAge = temporary?.other ?: settings.otherAge,
            savedOtherAge = settings.otherAge,
            temporary = temporary != null,
            gap = userBirth?.let { Generations.gap(it, otherBirth) },
            reversed = settings.equivalencesReversed,
            families = settings.equivalenceFamilies,
            pairs = null,
        )
        // Keep the previous list on screen while the new one is computed, so the banner does not flicker.
        _state.value = base.copy(pairs = _state.value?.pairs?.takeIf { userBirth != null })
        maybeWarn(base)
        if (userBirth == null) return
        _state.value = try {
            val (left, right) = if (settings.equivalencesReversed) userBirth to otherBirth else otherBirth to userBirth
            base.copy(pairs = getEquivalences(left, right, settings.equivalenceFamilies, settings.region))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.e(e) { "Equivalences could not be computed" }
            base.copy(error = true)
        }
    }

    private fun maybeWarn(state: EquivalencesUiState) {
        if (state.reducedMode) return
        val opening = !warnedOnOpen
        warnedOnOpen = true
        val afterChange = warnAfterChange
        warnAfterChange = false
        if (state.isSmallGap && (opening || afterChange)) _smallGapWarning.value = true
    }

    private fun userBirth(settings: Settings, temporary: TemporaryAges?, today: LocalDate): LocalDate? = when {
        temporary != null -> Generations.birthFromAge(today, temporary.user)
        else -> settings.birth?.let(Generations::userBirth)
    }

    fun dismissSmallGapWarning() {
        _smallGapWarning.value = false
    }

    /** "Modifier": saves the other person's age (the only place where it is set, decision of 15/11/2026). */
    fun saveOtherAge(age: Int) {
        val current = _state.value ?: return
        if (age == current.savedOtherAge) {
            // Nothing changes, so no new state comes: warn now if needed.
            if (current.isSmallGap && !current.temporary) _smallGapWarning.value = true
            return
        }
        warnAfterChange = true
        viewModelScope.launch {
            if (!updateSettings.setOtherAge(age)) warnAfterChange = false
        }
    }

    /** Touching the user's age in the banner: a temporary age, not saved. */
    fun setTemporaryUserAge(age: Int) = setTemporary { it.copy(user = age) }

    /** Touching the other person's age in the banner: a temporary age, not saved. */
    fun setTemporaryOtherAge(age: Int) = setTemporary { it.copy(other = age) }

    private fun setTemporary(change: (TemporaryAges) -> TemporaryAges) {
        val current = _state.value ?: return
        val start = temporaryAges.value
            ?: TemporaryAges(user = current.userAge ?: DEFAULT_TEMPORARY_USER_AGE, other = current.otherAge)
        val next = change(start).let {
            TemporaryAges(it.user.coerceIn(Generations.AGE_RANGE), it.other.coerceIn(Generations.AGE_RANGE))
        }
        if (next == temporaryAges.value) {
            // Nothing changes, so no new state comes: warn now if needed.
            if (current.isSmallGap) _smallGapWarning.value = true
            return
        }
        warnAfterChange = true
        temporaryAges.value = next
    }

    /** "Choisir des âges" without a birth: starts the temporary ages, as the screen's example (35 and the saved age). */
    fun startTemporaryAges() = setTemporary { it }

    /** "Revenir à mes réglages". */
    fun clearTemporaryAges() {
        temporaryAges.value = null
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

    private companion object {
        /** Start value of the user's temporary age when no birth is saved: the age of the mock-ups' example. */
        const val DEFAULT_TEMPORARY_USER_AGE = 35
    }
}
