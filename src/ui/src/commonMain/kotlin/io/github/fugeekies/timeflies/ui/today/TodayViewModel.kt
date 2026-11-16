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
import io.github.fugeekies.timeflies.ui.dayChanges
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

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
            combine(observeSettings(), clock.dayChanges()) { settings, _ -> settings }
                .catch { e ->
                    log.e(e) { "Settings could not be read" }
                    _state.value = TodayUiState.Error
                }
                .collectLatest { load(it) }
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
}
