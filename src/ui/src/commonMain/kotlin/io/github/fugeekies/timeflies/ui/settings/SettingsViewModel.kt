package io.github.fugeekies.timeflies.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetEventGroups
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.dayChanges
import io.github.fugeekies.timeflies.ui.events.FilterNode
import io.github.fugeekies.timeflies.ui.events.filterTree
import io.github.fugeekies.timeflies.ui.recoverFailures
import io.github.fugeekies.timeflies.ui.text.AppLocale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth

/**
 * What the Settings screen shows.
 *
 * @property month month picked while the birth is not saved yet (both lists are needed to save it).
 * @property year year picked while the birth is not saved yet.
 * @property eventCount number of events shown (the event filter applied); null until counted or when unreadable.
 * @property eventGroups the tree of the event filter; empty until read.
 */
data class SettingsUiState(
    val today: LocalDate,
    val birth: YearMonth?,
    val month: Month?,
    val year: Int?,
    val region: Region,
    val eventCount: Int?,
    val windows: WindowsSettings,
    val releaseCheck: Boolean,
    val eventGroups: List<FilterNode>,
    val excludedEventGroups: Set<String>,
    val displayStyle: DisplayStyle,
    /** The language shown: the one chosen, else the system's when available, else French (milestone 17). */
    val language: AppLanguage,
) {
    val birthYears: IntRange get() = Generations.birthYears(today)
    val userAge: Int? get() = birth?.let { Generations.userAge(it, today) }
}

/**
 * State of the Settings screen (docs/UISpec/Settings.template.md). Every change is saved at once.
 * The other ages are not set here but in the banner of "Quel âge ?" and Equivalences (decision of 17/11/2026).
 */
class SettingsViewModel(
    observeSettings: ObserveSettings,
    private val updateSettings: UpdateSettings,
    countEvents: CountEvents,
    getEventGroups: GetEventGroups,
    private val clock: Clock,
) : ViewModel() {

    private data class Draft(val month: Month? = null, val year: Int? = null)

    private val draft = MutableStateFlow(Draft())
    private val eventCount = MutableStateFlow<Int?>(null)
    private val eventGroups = MutableStateFlow<List<FilterNode>>(emptyList())

    val state: StateFlow<SettingsUiState?> = combine(
        observeSettings(),
        draft,
        eventCount,
        eventGroups,
        // The age shown next to the birth changes with the day.
        clock.dayChanges(),
    ) { settings, draft, count, groups, _ ->
        uiState(settings, draft, count, groups)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        // Counted again when the event filter changes: "192 événements" follows what is shown.
        viewModelScope.launch {
            observeSettings().map { it.excludedEventGroups }.distinctUntilChanged().collect {
                eventCount.value = recoverFailures(onFailure = { null }) { countEvents() }
            }
        }
        viewModelScope.launch {
            eventGroups.value = recoverFailures(onFailure = { emptyList() }) { filterTree(getEventGroups()) }
        }
    }

    private fun uiState(settings: Settings, draft: Draft, count: Int?, groups: List<FilterNode>) = SettingsUiState(
        today = clock.today(),
        birth = settings.birth,
        month = settings.birth?.month ?: draft.month,
        year = settings.birth?.year ?: draft.year,
        region = settings.region,
        eventCount = count,
        windows = settings.windows,
        releaseCheck = settings.releaseCheck.enabled,
        eventGroups = groups,
        excludedEventGroups = settings.excludedEventGroups,
        displayStyle = settings.displayStyle,
        language = AppLanguage.resolve(settings.language, AppLocale.systemTag()),
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

    /** The new set of unticked event groups, from the filter tree. */
    fun onExcludedEventGroups(paths: Set<String>) {
        viewModelScope.launch { updateSettings.setExcludedEventGroups(paths) }
    }

    /** Settings › Affichage › Langue: saved, then applied by the app root to every text. */
    fun onLanguage(language: AppLanguage) {
        viewModelScope.launch { updateSettings.setLanguage(language) }
    }

    /** Settings › Affichage: applied at once to the whole app and to the widget. */
    fun onDisplayStyle(style: DisplayStyle) {
        viewModelScope.launch { updateSettings.setDisplayStyle(style) }
    }

    fun onReleaseCheck(enabled: Boolean) {
        viewModelScope.launch { updateSettings.setReleaseCheck(enabled) }
    }

    fun onKeepInTray(enabled: Boolean) {
        viewModelScope.launch { updateSettings.setKeepInTray(enabled) }
    }
}
