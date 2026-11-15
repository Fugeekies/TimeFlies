package io.github.fugeekies.timeflies.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock as KotlinClock
import kotlin.time.Duration.Companion.seconds

sealed interface TodayUiState {
    data object Loading : TodayUiState
    data class Loaded(val shock: DailyShock) : TodayUiState
    data object Error : TodayUiState
}

/**
 * State of the Today screen. Recomputed when the day changes while the app is open, and when the settings
 * change (birth, region) (docs/UISpec/Today.template.md).
 */
class TodayViewModel(
    private val getDailyShock: GetDailyShock,
    private val observeSettings: ObserveSettings,
    private val clock: Clock,
) : ViewModel() {

    private val log = Logger.withTag("Today")

    private val _state = MutableStateFlow<TodayUiState>(TodayUiState.Loading)
    val state: StateFlow<TodayUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(observeSettings(), days(), clock.manualChanges().onStart { emit(Unit) }) { settings, _, _ -> settings }
                .catch { e ->
                    log.e(e) { "Settings could not be read" }
                    _state.value = TodayUiState.Error
                }
                .collectLatest { load(it) }
        }
    }

    /** Today's date, then the new date one second past each midnight. */
    private fun days() = flow<LocalDate> {
        while (true) {
            emit(clock.today())
            delay(untilNextDay())
        }
    }

    private suspend fun load(settings: Settings) {
        _state.value = try {
            TodayUiState.Loaded(getDailyShock(settings.region, settings.birth?.let(Generations::userBirth)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.e(e) { "Daily shock could not be computed" }
            TodayUiState.Error
        }
    }

    /** The day the photo last developed: once a day, not at each return to the screen (docs/UI_DESIGN_SPEC.md). */
    private var developedOn: LocalDate? = null

    fun shouldDevelop(day: LocalDate): Boolean = developedOn != day

    fun onDeveloped(day: LocalDate) {
        developedOn = day
    }

    private fun untilNextDay() = run {
        val zone = TimeZone.currentSystemDefault()
        // The system midnight, not the one of clock.today(): a fake date of the debug panel does not move on its own.
        val nextMidnight = KotlinClock.System.todayIn(zone).plus(1, DateTimeUnit.DAY).atStartOfDayIn(zone)
        // One second past midnight, and never less than a second, so the new day is always seen.
        maxOf((nextMidnight - KotlinClock.System.now()) + 1.seconds, 1.seconds)
    }
}
