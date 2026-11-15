package io.github.fugeekies.timeflies.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.BaseStats
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.DebugTools
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** What the debug panel shows. [stats] is null until loaded, or when the base cannot be read. */
data class DebugUiState(
    val today: LocalDate,
    val fakeDate: LocalDate?,
    val settings: Settings,
    val stats: BaseStats?,
    val dateError: Boolean,
    val regionError: Boolean,
)

/**
 * State of the debug panel (docs/DEVELOPMENT.md, "Panneau de débogage"). A developer tool, present in debug builds
 * only: it talks to [DebugTools] directly and writes settings without the checks of the screens.
 */
class DebugViewModel(
    private val tools: DebugTools,
    observeSettings: ObserveSettings,
    private val clock: Clock,
) : ViewModel() {

    private val log = Logger.withTag("Debug")
    private val stats = MutableStateFlow<BaseStats?>(null)
    private val errors = MutableStateFlow(Pair(false, false))

    val state: StateFlow<DebugUiState?> = combine(tools.fakeDate, observeSettings(), stats, errors) { fake, settings, stats, errors ->
        DebugUiState(clock.today(), fake, settings, stats, dateError = errors.first, regionError = errors.second)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            stats.value = try {
                tools.baseStats()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.e(e) { "Statistics could not be computed" }
                null
            }
        }
    }

    fun setDate(date: LocalDate?) {
        errors.value = errors.value.copy(first = false)
        tools.setFakeDate(date)
    }

    fun previousDay() = setDate(clock.today().minus(1, DateTimeUnit.DAY))

    fun nextDay() = setDate(clock.today().plus(1, DateTimeUnit.DAY))

    /** Parses "AAAA-MM-JJ"; shows an error and changes nothing when it is not a date. */
    fun setDateText(text: String) {
        val date = runCatching { LocalDate.parse(text.trim()) }.getOrNull()
        if (date == null) errors.value = errors.value.copy(first = true) else setDate(date)
    }

    /** The first day after today on which no event has its anniversary (29 February skipped in common years). */
    fun nextEmptyDay() {
        val empty = stats.value?.emptyDays?.toSet()?.takeIf { it.isNotEmpty() } ?: return
        var day = clock.today()
        repeat(366 * 4) {
            day = day.plus(1, DateTimeUnit.DAY)
            if (MonthDay.of(day) in empty) {
                setDate(day)
                return
            }
        }
    }

    fun setBirth(birth: YearMonth?) = force { it.copy(birth = birth) }

    fun setRegionText(text: String) {
        val region = Region.of(text.trim().uppercase())
        errors.value = errors.value.copy(second = region == null)
        if (region != null) force { it.copy(region = region) }
    }

    fun redoOnboarding() = force { it.copy(onboardingDone = false) }

    fun resetSettings() = force { Settings() }

    private fun force(transform: (Settings) -> Settings) {
        viewModelScope.launch {
            tools.forceSettings(transform)
            // The region changes the statistics (regional dates).
            refreshStats()
        }
    }
}
