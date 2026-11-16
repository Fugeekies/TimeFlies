package io.github.fugeekies.timeflies.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.GetEventGroups
import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.model.Family
import kotlinx.coroutines.CancellationException
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
    val groups: List<EventGroupCount> = emptyList(),
    val excludedFamilies: Set<Family> = emptySet(),
    val excludedOrigins: Set<String> = emptySet(),
) {
    val birthYears: IntRange get() = Generations.birthYears(today)
    val birth: YearMonth? get() = if (month != null && year != null) YearMonth(year, month) else null

    /** Families of the base, in their order. */
    val families: List<Family> get() = groups.map { it.group.family }.distinct().sorted()

    /** Origins of the base, the most represented first ("fr", "us"…). */
    val origins: List<String> get() = groups.groupBy { it.group.origin }.entries.sortedByDescending { (_, ofOrigin) -> ofOrigin.sumOf { it.count } }.map { it.key }

    /** The groups left out by the unticked families and origins (decision of 16/11/2026). */
    val excludedGroups: Set<String> get() = groups.filter { it.group.family in excludedFamilies || it.group.origin in excludedOrigins }.mapTo(HashSet()) { it.group.path }

    /** "C'est parti" is enabled once the month and the year are chosen. */
    val canStart: Boolean get() = birth != null && !saving
}

/** State of the first launch screen (docs/UISpec/Onboarding.template.md). */
class OnboardingViewModel(
    private val completeOnboarding: CompleteOnboarding,
    getEventGroups: GetEventGroups,
    clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState(today = clock.today()))
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val groups = try {
                getEventGroups()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            }
            _state.update { it.copy(groups = groups) }
        }
    }

    /** A family chip: everything of this theme is left out, or comes back. */
    fun onFamilyToggled(family: Family) = _state.update {
        it.copy(excludedFamilies = if (family in it.excludedFamilies) it.excludedFamilies - family else it.excludedFamilies + family)
    }

    /** An origin chip: everything from this country is left out, or comes back. */
    fun onOriginToggled(origin: String) = _state.update {
        it.copy(excludedOrigins = if (origin in it.excludedOrigins) it.excludedOrigins - origin else it.excludedOrigins + origin)
    }

    fun onMonthPicked(month: Month) = _state.update { it.copy(month = month) }

    fun onYearPicked(year: Int) = _state.update { it.copy(year = year) }

    /** "C'est parti": the app then opens Today, as soon as the saved settings say the first launch is done. */
    fun onStart() {
        val current = _state.value
        val birth = current.birth ?: return
        if (!current.canStart) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            if (!completeOnboarding.start(birth, current.excludedGroups)) _state.update { it.copy(saving = false) }
        }
    }

    /** "Plus tard": reduced mode, without birth. */
    fun onLater() {
        if (_state.value.saving) return
        _state.update { it.copy(saving = true) }
        val excluded = _state.value.excludedGroups
        viewModelScope.launch { completeOnboarding.later(excluded) }
    }
}
