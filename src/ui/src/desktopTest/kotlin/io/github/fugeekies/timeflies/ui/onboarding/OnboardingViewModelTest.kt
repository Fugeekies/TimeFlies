package io.github.fugeekies.timeflies.ui.onboarding

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.GetEventGroups
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The first launch screen answers every time it shows, even when it comes back in the same session. */
@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private class InMemorySettings : SettingsRepository {
        override val settings = MutableStateFlow(Settings())
        override suspend fun update(transform: (Settings) -> Settings) {
            settings.value = transform(settings.value)
        }
    }

    private object NoEvents : EventRepository {
        override suspend fun onMonthDay(monthDay: MonthDay, region: Region) = emptyList<Event>()
        override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region) = emptyList<Event>()
        override suspend fun search(query: String, limit: Int) = emptyList<Event>()
        override suspend fun count() = 0
    }

    private val clock = Clock { LocalDate(2026, 11, 17) }
    private val settings = InMemorySettings()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = OnboardingViewModel(CompleteOnboarding(settings, clock), GetEventGroups(NoEvents), clock)

    @Test
    fun answersAgainWhenTheScreenComesBack() {
        val onboarding = viewModel()
        onboarding.onLater()
        assertTrue(settings.settings.value.onboardingDone)
        assertFalse(onboarding.state.value.saving, "the buttons are enabled again once saved")

        // "Revoir le premier lancement" (debug panel): the same view model shows the screen again.
        settings.settings.value = settings.settings.value.copy(onboardingDone = false)
        onboarding.onMonthPicked(Month.MAY)
        onboarding.onYearPicked(1991)
        assertTrue(onboarding.state.value.canStart)
        onboarding.onStart()
        assertTrue(settings.settings.value.onboardingDone)
        assertEquals(1991, settings.settings.value.birth?.year)
        assertFalse(onboarding.state.value.saving)
    }
}
