package io.github.fugeekies.timeflies.domain.usecase

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
        assertEquals(15, settings.youngAge)
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
    fun doesNotSaveAYoungAgeTooCloseToTheUser() = runTest {
        update.setBirth(YearMonth(1991, Month.MAY))
        assertTrue(update.setYoungAge(30))
        assertFalse(update.setYoungAge(31))
        assertEquals(30, repository.state.value.youngAge)
    }

    @Test
    fun keepsTheYoungAgeWhenANewBirthMakesItTooClose() = runTest {
        // Decision of 15/11/2026: allowed, the Settings screen shows the message.
        assertTrue(update.setYoungAge(15))
        update.setBirth(YearMonth(2018, Month.MARCH))
        assertEquals(15, repository.state.value.youngAge)
        assertEquals(YearMonth(2018, Month.MARCH), repository.state.value.birth)
    }
}
