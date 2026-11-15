package io.github.fugeekies.timeflies.ui.equivalences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
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
 * The temporary ages, shared by the Equivalences and Mon équivalent screens (docs/UISpec/MyEquivalent.template.md).
 * One instance per app, kept in memory only.
 */
class TemporaryAgesStore {
    val ages = MutableStateFlow<TemporaryAges?>(null)
}

/**
 * The two people of the banner (docs/UISpec/Equivalences.template.md, "Bandeau des générations").
 *
 * @property userAge the user's age shown; null in reduced mode (no birth and no temporary ages).
 * @property otherAge the other person's age shown.
 * @property savedOtherAge the other person's saved age, the start value of "Modifier".
 * @property gap gap between the two births in completed years; null in reduced mode.
 * @property userBirth the user's birth used for the computations; null in reduced mode.
 * @property settings the saved settings these values come from.
 */
data class GenerationsUiState(
    val userAge: Int?,
    val otherAge: Int,
    val savedOtherAge: Int,
    val temporary: Boolean,
    val gap: Int?,
    val userBirth: LocalDate?,
    val otherBirth: LocalDate,
    val settings: Settings,
) {
    val reducedMode: Boolean get() = userBirth == null
    val isSmallGap: Boolean get() = gap != null && gap < Generations.SMALL_GAP
}

/**
 * The banner logic shared by the Equivalences and Mon équivalent screens: the two people's ages from the settings or
 * from the temporary ages, "Modifier" (the only place where the other person's age is saved, decision of 15/11/2026)
 * and the small-gap warning. Subclasses call [start] at the end of their `init` and compute their page in
 * [onGenerations].
 */
abstract class GenerationsViewModel(
    private val observeSettings: ObserveSettings,
    private val updateSettings: UpdateSettings,
    protected val clock: Clock,
    private val temporaryAges: TemporaryAgesStore,
) : ViewModel() {

    protected val log = Logger.withTag(this::class.simpleName ?: "Generations")

    private val _generations = MutableStateFlow<GenerationsUiState?>(null)
    val generations: StateFlow<GenerationsUiState?> = _generations.asStateFlow()

    private val _smallGapWarning = MutableStateFlow(false)

    /** The "petit écart" window is open. */
    val smallGapWarning: StateFlow<Boolean> = _smallGapWarning.asStateFlow()

    /** Shown at most once when the screen opens on a small gap; again after each age change giving one. */
    private var warnedOnOpen = false
    private var warnAfterChange = false

    /** Starts listening to the settings and the temporary ages; called by the subclass once it is built. */
    protected fun start() {
        viewModelScope.launch {
            combine(observeSettings(), temporaryAges.ages, clock.manualChanges().onStart { emit(Unit) }) { settings, temporary, _ ->
                generations(settings, temporary, clock.today())
            }
                .catch { e -> log.e(e) { "Settings could not be read" } }
                .collectLatest { state ->
                    _generations.value = state
                    maybeWarn(state)
                    onGenerations(state)
                }
        }
    }

    /** Recomputes the page for new ages or settings; cancelled when newer ones come. */
    protected abstract suspend fun onGenerations(generations: GenerationsUiState)

    private fun generations(settings: Settings, temporary: TemporaryAges?, today: LocalDate): GenerationsUiState {
        val userBirth = when {
            temporary != null -> Generations.birthFromAge(today, temporary.user)
            else -> settings.birth?.let(Generations::userBirth)
        }
        val otherBirth = Generations.birthFromAge(today, temporary?.other ?: settings.otherAge)
        return GenerationsUiState(
            userAge = when {
                temporary != null -> temporary.user
                settings.birth != null -> Generations.userAge(settings.birth!!, today)
                else -> null
            },
            otherAge = temporary?.other ?: settings.otherAge,
            savedOtherAge = settings.otherAge,
            temporary = temporary != null,
            gap = userBirth?.let { Generations.gap(it, otherBirth) },
            userBirth = userBirth,
            otherBirth = otherBirth,
            settings = settings,
        )
    }

    private fun maybeWarn(state: GenerationsUiState) {
        if (state.reducedMode) return
        val opening = !warnedOnOpen
        warnedOnOpen = true
        val afterChange = warnAfterChange
        warnAfterChange = false
        if (state.isSmallGap && (opening || afterChange)) _smallGapWarning.value = true
    }

    fun dismissSmallGapWarning() {
        _smallGapWarning.value = false
    }

    /** "Modifier": saves the other person's age. */
    fun saveOtherAge(age: Int) {
        val current = _generations.value ?: return
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

    /** "Choisir des âges" without a birth: starts the temporary ages (35, the mock-ups' example, and the saved age). */
    fun startTemporaryAges() = setTemporary { it }

    /** "Revenir à mes réglages". */
    fun clearTemporaryAges() {
        temporaryAges.ages.value = null
    }

    private fun setTemporary(change: (TemporaryAges) -> TemporaryAges) {
        val current = _generations.value ?: return
        val start = temporaryAges.ages.value
            ?: TemporaryAges(user = current.userAge ?: DEFAULT_TEMPORARY_USER_AGE, other = current.otherAge)
        val next = change(start).let {
            TemporaryAges(it.user.coerceIn(Generations.AGE_RANGE), it.other.coerceIn(Generations.AGE_RANGE))
        }
        if (next == temporaryAges.ages.value) {
            // Nothing changes, so no new state comes: warn now if needed.
            if (current.isSmallGap) _smallGapWarning.value = true
            return
        }
        warnAfterChange = true
        temporaryAges.ages.value = next
    }

    private companion object {
        /** Start value of the user's temporary age when no birth is saved: the age of the mock-ups' example. */
        const val DEFAULT_TEMPORARY_USER_AGE = 35
    }
}
