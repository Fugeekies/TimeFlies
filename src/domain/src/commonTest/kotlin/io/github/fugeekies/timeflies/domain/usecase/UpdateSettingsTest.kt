package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UpdateSettingsTest {

    private class InMemorySettings : SettingsRepository {
        val state = MutableStateFlow(Settings())
        override val settings: Flow<Settings> = state
        override suspend fun update(transform: (Settings) -> Settings) {
            state.value = transform(state.value)
        }
    }

    private val repository = InMemorySettings()
    private val update = UpdateSettings(repository, Clock { LocalDate(2026, 11, 15) })

    @Test
    fun startsWithTheFirstLaunchDefaults() = runTest {
        val settings = ObserveSettings(repository)().first()
        assertNull(settings.birth)
        assertEquals(15, settings.otherAge)
        assertFalse(settings.equivalencesReversed)
        assertEquals(Family.entries.toSet(), settings.equivalenceFamilies)
        assertEquals("FR", settings.region.code)
    }

    @Test
    fun savesAndClearsTheBirth() = runTest {
        update.setBirth(YearMonth(1991, Month.MAY))
        assertEquals(YearMonth(1991, Month.MAY), repository.state.value.birth)
        update.clearBirth()
        assertNull(repository.state.value.birth)
    }

    @Test
    fun rejectsABirthYearOutOfTheList() = runTest {
        assertFailsWith<IllegalArgumentException> { update.setBirth(YearMonth(1919, Month.DECEMBER)) }
        assertFailsWith<IllegalArgumentException> { update.setBirth(YearMonth(2022, Month.JANUARY)) }
        assertNull(repository.state.value.birth)
    }

    @Test
    fun savesAnOtherAgeYoungerOrOlderThanTheUser() = runTest {
        update.setBirth(YearMonth(1991, Month.MAY))
        assertTrue(update.setOtherAge(65))
        assertEquals(65, repository.state.value.otherAge)
        // A small gap is allowed, the screen warns (decision of 15/11/2026).
        assertTrue(update.setOtherAge(34))
        assertEquals(34, repository.state.value.otherAge)
    }

    @Test
    fun rejectsAnOtherAgeOutOfRange() = runTest {
        assertFalse(update.setOtherAge(0))
        assertFalse(update.setOtherAge(101))
        assertEquals(15, repository.state.value.otherAge)
    }

    @Test
    fun savesTheDirectionAndThemesOfTheEquivalences() = runTest {
        update.setEquivalencesReversed(true)
        update.setEquivalenceFamilies(setOf(Family.MUSIC, Family.SPORT))
        assertTrue(repository.state.value.equivalencesReversed)
        assertEquals(setOf(Family.MUSIC, Family.SPORT), repository.state.value.equivalenceFamilies)
    }
}
