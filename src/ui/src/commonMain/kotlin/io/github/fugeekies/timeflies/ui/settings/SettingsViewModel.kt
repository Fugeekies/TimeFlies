package io.github.fugeekies.timeflies.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.model.WindowsSettings
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
import io.github.fugeekies.timeflies.ui.dayChanges
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth

/**
 * What the Settings screen shows.
 *
 * @property month month picked while the birth is not saved yet (both lists are needed to save it).
 * @property year year picked while the birth is not saved yet.
 * @property eventCount number of events in the base; null until counted or when the base cannot be read.
 */
data class SettingsUiState(
    val today: LocalDate,
    val birth: YearMonth?,
    val month: Month?,
    val year: Int?,
    val region: Region,
    val eventCount: Int?,
    val windows: WindowsSettings,
) {
    val birthYears: IntRange get() = Generations.birthYears(today)
    val userAge: Int? get() = birth?.let { Generations.userAge(it, today) }
}

/**
 * State of the Settings screen (docs/UISpec/Settings.template.md). Every change is saved at once.
 * The young person's age is not set here but on the Equivalences screen (decision of 15/11/2026).
 */
class SettingsViewModel(
    observeSettings: ObserveSettings,
    private val updateSettings: UpdateSettings,
    countEvents: CountEvents,
    private val clock: Clock,
) : ViewModel() {

    private data class Draft(val month: Month? = null, val year: Int? = null)

    private val draft = MutableStateFlow(Draft())
    private val eventCount = MutableStateFlow<Int?>(null)

    val state: StateFlow<SettingsUiState?> = combine(
        observeSettings(),
        draft,
        eventCount,
        // The age shown next to the birth changes with the day.
        clock.dayChanges(),
    ) { settings, draft, count, _ ->
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
        region = settings.region,
        eventCount = count,
        windows = settings.windows,
    )

    fun onMonthPicked(month: Month) = onBirthPicked(month = month)

    fun onYearPicked(year: Int) = onBirthPicked(year = year)

    /** Saves the birth as soon as both the month and the year are known. */
    private fun onBirthPicked(month: Month? = null, year: Int? = null) {
        val current = state.value ?: return
        val newMonth = month ?: current.month
        val newYear = year ?: current.year
        draft.value = Draft(newMonth, newYear)
        if (newMonth != null && newYear != null) {
            viewModelScope.launch {
                updateSettings.setBirth(YearMonth(newYear, newMonth))
                draft.value = Draft()
            }
        }
    }

    fun onClearBirth() {
        draft.value = Draft()
        viewModelScope.launch { updateSettings.clearBirth() }
    }

    // Windows panel: the app writes or removes its startup entry when the setting changes.
    fun onLaunchAtStartup(enabled: Boolean) {
        viewModelScope.launch { updateSettings.setLaunchAtStartup(enabled) }
    }

    fun onMorningNotification(enabled: Boolean) {
        viewModelScope.launch { updateSettings.setMorningNotification(enabled) }
    }

    fun onNotificationTime(time: LocalTime) {
        viewModelScope.launch { updateSettings.setNotificationTime(time) }
    }

    fun onKeepInTray(enabled: Boolean) {
        viewModelScope.launch { updateSettings.setKeepInTray(enabled) }
    }
}
