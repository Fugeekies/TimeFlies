package io.github.fugeekies.timeflies.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.time.Clock as KotlinClock
import kotlin.time.Duration.Companion.seconds

sealed interface TodayUiState {
    data object Loading : TodayUiState
    data class Loaded(val shock: DailyShock) : TodayUiState
    data object Error : TodayUiState
}

/** State of the Today screen. Recomputed when the day changes while the app is open (docs/UISpec/Today.template.md). */
class TodayViewModel(
    private val getDailyShock: GetDailyShock,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow<TodayUiState>(TodayUiState.Loading)
    val state: StateFlow<TodayUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                load()
                delay(untilNextDay())
            }
        }
    }

    private suspend fun load() {
        _state.value = try {
            TodayUiState.Loaded(getDailyShock())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TodayUiState.Error
        }
    }

    private fun untilNextDay() = run {
        val zone = TimeZone.currentSystemDefault()
        val nextMidnight = clock.today().plus(1, DateTimeUnit.DAY).atStartOfDayIn(zone)
        // One second past midnight, and never less than a second, so the new day is always seen.
        maxOf((nextMidnight - KotlinClock.System.now()) + 1.seconds, 1.seconds)
    }
}
