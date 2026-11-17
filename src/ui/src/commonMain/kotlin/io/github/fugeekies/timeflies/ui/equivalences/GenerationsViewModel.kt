package io.github.fugeekies.timeflies.ui.equivalences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.ui.dayChanges
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Ages picked in the banner, never saved and forgotten when the app closes (docs/UISpec/Equivalences.template.md,
 * decisions of 13/11/2026 and 17/11/2026).
 *
 * @property user the user's age picked on the fly instead of their birth ("Âges temporaires"); null: the settings.
 * @property others the ages added next to the user's, up to [Generations.MAX_OTHER_PEOPLE], in their order.
 */
data class TemporaryAges(val user: Int? = null, val others: List<Int> = emptyList())

/**
 * The ages of the banner, shared by the "Quel âge ?" and Equivalences screens (docs/UISpec/EventAges.template.md).
 * One instance per app, kept in memory only.
 */
class TemporaryAgesStore {
    val ages = MutableStateFlow(TemporaryAges())
}

/**
 * The people of the banner (docs/UISpec/Equivalences.template.md, "Bandeau des âges"): the user, then the ages added.
 *
 * @property userAge the user's age shown; null in reduced mode (no birth and no temporary age).
 * @property temporary the user's age was picked on the fly.
 * @property otherAges the ages added, in their order (empty by default, decision of 17/11/2026).
 * @property gaps gap between the user's birth and each other birth, in completed years; empty in reduced mode.
 * @property userBirth the user's birth used for the computations; null in reduced mode.
 * @property otherBirths the birth of each added age: today minus that age.
 * @property settings the saved settings these values come from.
 */
data class GenerationsUiState(
    val userAge: Int?,
    val temporary: Boolean,
    val otherAges: List<Int>,
    val gaps: List<Int>,
    val userBirth: LocalDate?,
    val otherBirths: List<LocalDate>,
    val settings: Settings,
) {
    val reducedMode: Boolean get() = userBirth == null
    val isSmallGap: Boolean get() = gaps.any { it < Generations.SMALL_GAP }
    val canAddAge: Boolean get() = otherAges.size < Generations.MAX_OTHER_PEOPLE

    /** Every birth, the user first, in the order of the banner; empty in reduced mode. */
    val births: List<LocalDate> get() = userBirth?.let { listOf(it) + otherBirths }.orEmpty()
}

/**
 * The banner logic shared by the "Quel âge ?" and Equivalences screens: the user's age from the settings or picked
 * on the fly, the ages added (up to three, forgotten when the app closes, decision of 17/11/2026) and the small-gap
 * warning. Subclasses call [start] at the end of their `init` and compute their page in [onGenerations].
 */
abstract class GenerationsViewModel(
    private val observeSettings: ObserveSettings,
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
            // Ages and target dates move with the day: recomputed at midnight too, not only at the opening.
            combine(observeSettings(), temporaryAges.ages, clock.dayChanges()) { settings, temporary, _ ->
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

    private fun generations(settings: Settings, temporary: TemporaryAges, today: LocalDate): GenerationsUiState {
        val userBirth = when {
            temporary.user != null -> Generations.birthFromAge(today, temporary.user)
            else -> settings.birth?.let(Generations::userBirth)
        }
        val otherBirths = temporary.others.map { Generations.birthFromAge(today, it) }
        return GenerationsUiState(
            userAge = when {
                temporary.user != null -> temporary.user
                settings.birth != null -> Generations.userAge(settings.birth!!, today)
                else -> null
            },
            temporary = temporary.user != null,
            otherAges = temporary.others,
            gaps = if (userBirth == null) emptyList() else otherBirths.map { Generations.gap(userBirth, it) },
            userBirth = userBirth,
            otherBirths = otherBirths,
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

    /** Touching the user's age in the banner: a temporary age, not saved. */
    fun setTemporaryUserAge(age: Int) = change { it.copy(user = age.coerceIn(Generations.AGE_RANGE)) }

    /** "+": adds an age after the others, up to three (decision of 17/11/2026). */
    fun addOtherAge(age: Int) = change {
        if (it.others.size >= Generations.MAX_OTHER_PEOPLE) it else it.copy(others = it.others + age.coerceIn(Generations.AGE_RANGE))
    }

    /** Touching an added age: changes it, keeping its place and colour. */
    fun setOtherAge(index: Int, age: Int) = change {
        if (index !in it.others.indices) it else it.copy(others = it.others.toMutableList().apply { set(index, age.coerceIn(Generations.AGE_RANGE)) })
    }

    /** "Retirer": removes an added age; the next ones move up a place. */
    fun removeOtherAge(index: Int) {
        temporaryAges.ages.update { if (index !in it.others.indices) it else it.copy(others = it.others.filterIndexed { i, _ -> i != index }) }
    }

    /** "Choisir des âges" without a birth: a temporary age for the user (35, the mock-ups' example). */
    fun startTemporaryAges() = change { it.copy(user = it.user ?: _generations.value?.userAge ?: DEFAULT_TEMPORARY_USER_AGE) }

    /** "Revenir à mes réglages": the user's age comes from the settings again; the added ages stay. */
    fun clearTemporaryAges() {
        temporaryAges.ages.update { it.copy(user = null) }
    }

    private fun change(transform: (TemporaryAges) -> TemporaryAges) {
        val current = _generations.value ?: return
        val next = transform(temporaryAges.ages.value)
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
