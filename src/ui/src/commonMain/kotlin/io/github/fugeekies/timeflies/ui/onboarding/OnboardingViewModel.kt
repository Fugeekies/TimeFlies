package io.github.fugeekies.timeflies.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth

/** What the first launch screen shows. Nothing is saved before "C'est parti" or "Plus tard". */
data class OnboardingUiState(
    val today: LocalDate,
    val month: Month? = null,
    val year: Int? = null,
    val saving: Boolean = false,
) {
    val birthYears: IntRange get() = Generations.birthYears(today)
    val birth: YearMonth? get() = if (month != null && year != null) YearMonth(year, month) else null

    /** "C'est parti" is enabled once the month and the year are chosen. */
    val canStart: Boolean get() = birth != null && !saving
}

/** State of the first launch screen (docs/UISpec/Onboarding.template.md). */
class OnboardingViewModel(
    private val completeOnboarding: CompleteOnboarding,
    clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState(today = clock.today()))
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onMonthPicked(month: Month) = _state.update { it.copy(month = month) }

    fun onYearPicked(year: Int) = _state.update { it.copy(year = year) }

    /** "C'est parti": the app then opens Today, as soon as the saved settings say the first launch is done. */
    fun onStart() {
        val current = _state.value
        val birth = current.birth ?: return
        if (!current.canStart) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            if (!completeOnboarding.start(birth)) _state.update { it.copy(saving = false) }
        }
    }

    /** "Plus tard": reduced mode, without birth. */
    fun onLater() {
        if (_state.value.saving) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch { completeOnboarding.later() }
    }
}
