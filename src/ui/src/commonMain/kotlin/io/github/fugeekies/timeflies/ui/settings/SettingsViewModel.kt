package io.github.fugeekies.timeflies.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth

/**
 * What the Settings screen shows.
 *
 * @property month month picked while the birth is not saved yet (both lists are needed to save it).
 * @property year year picked while the birth is not saved yet.
 * @property youngAge the young age shown by the slider: the one being dragged, or the saved one.
 * @property eventCount number of events in the base; null until counted or when the base cannot be read.
 */
data class SettingsUiState(
    val today: LocalDate,
    val birth: YearMonth?,
    val month: Month?,
    val year: Int?,
    val youngAge: Int,
    val region: Region,
    val eventCount: Int?,
) {
    val birthYears: IntRange get() = Generations.birthYears(today)
    val userAge: Int? get() = birth?.let { Generations.userAge(it, today) }
    val gap: Int? get() = birth?.let { Generations.gap(it, youngAge, today) }

    /** "Le jeune doit avoir au moins 5 ans de moins que toi." is shown. */
    val youngTooOld: Boolean get() = !Generations.isYoungAgeAllowed(youngAge, birth, today)
}

/** State of the Settings screen (docs/UISpec/Settings.template.md). Every change is saved at once. */
class SettingsViewModel(
    observeSettings: ObserveSettings,
    private val updateSettings: UpdateSettings,
    countEvents: CountEvents,
    private val clock: Clock,
) : ViewModel() {

    private data class Draft(val month: Month? = null, val year: Int? = null, val youngAge: Int? = null)

    private val draft = MutableStateFlow(Draft())
    private val eventCount = MutableStateFlow<Int?>(null)

    val state: StateFlow<SettingsUiState?> = combine(observeSettings(), draft, eventCount) { settings, draft, count ->
        uiState(settings, draft, count)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch {
            eventCount.value = try {
                countEvents()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun uiState(settings: Settings, draft: Draft, count: Int?) = SettingsUiState(
        today = clock.today(),
        birth = settings.birth,
        month = settings.birth?.month ?: draft.month,
        year = settings.birth?.year ?: draft.year,
        youngAge = draft.youngAge ?: settings.youngAge,
        region = settings.region,
        eventCount = count,
    )

    fun onMonthPicked(month: Month) = onBirthPicked(month = month)

    fun onYearPicked(year: Int) = onBirthPicked(year = year)

    /** Saves the birth as soon as both the month and the year are known. */
    private fun onBirthPicked(month: Month? = null, year: Int? = null) {
        val current = state.value ?: return
        val newMonth = month ?: current.month
        val newYear = year ?: current.year
        draft.value = draft.value.copy(month = newMonth, year = newYear)
        if (newMonth != null && newYear != null) {
            viewModelScope.launch {
                updateSettings.setBirth(YearMonth(newYear, newMonth))
                draft.value = draft.value.copy(month = null, year = null)
                saveYoungAgeDraft()
            }
        }
    }

    fun onClearBirth() {
        draft.value = draft.value.copy(month = null, year = null)
        viewModelScope.launch {
            updateSettings.clearBirth()
            saveYoungAgeDraft()
        }
    }

    /** While the slider moves: shown, not saved yet. */
    fun onYoungAgeChange(age: Int) {
        draft.value = draft.value.copy(youngAge = age)
    }

    /** When the slider is released: saved if allowed; otherwise it stays shown with the message. */
    fun onYoungAgeChangeFinished() {
        viewModelScope.launch { saveYoungAgeDraft() }
    }

    /** Saves a young age left unsaved, once the birth allows it (also after the birth changes). */
    private suspend fun saveYoungAgeDraft() {
        val age = draft.value.youngAge ?: return
        if (updateSettings.setYoungAge(age)) draft.value = draft.value.copy(youngAge = null)
    }
}
