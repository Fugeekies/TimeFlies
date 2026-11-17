package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CompleteOnboardingTest {

    private class InMemorySettings : SettingsRepository {
        val state = MutableStateFlow(Settings())
        override val settings: Flow<Settings> = state
        override suspend fun update(transform: (Settings) -> Settings) {
            state.value = transform(state.value)
        }
    }

    private val repository = InMemorySettings()
    private val onboarding = CompleteOnboarding(repository, Clock { LocalDate(2026, 11, 15) })

    @Test
    fun startsNotDone() {
        assertFalse(repository.state.value.onboardingDone)
    }

    @Test
    fun savesTheBirthOnStart() = runTest {
        assertTrue(onboarding.start(YearMonth(1991, Month.MAY)))
        val settings = repository.state.value
        assertEquals(YearMonth(1991, Month.MAY), settings.birth)
        assertTrue(settings.onboardingDone)
    }

    @Test
    fun savesNothingForABirthYearOutOfTheList() = runTest {
        assertFalse(onboarding.start(YearMonth(2022, Month.JANUARY)))
        assertEquals(Settings(), repository.state.value)
    }

    @Test
    fun laterEndsTheFirstLaunchWithoutBirth() = runTest {
        onboarding.later()
        assertNull(repository.state.value.birth)
        assertTrue(repository.state.value.onboardingDone)
    }
}
