package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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
    fun savesTheThemesOfTheEquivalences() = runTest {
        update.setEquivalenceFamilies(setOf(Family.MUSIC, Family.SPORT))
        assertEquals(setOf(Family.MUSIC, Family.SPORT), repository.state.value.equivalenceFamilies)
    }

    @Test
    fun startsWithTheWindowsDefaults() = runTest {
        val windows = repository.state.value.windows
        assertTrue(windows.launchAtStartup)
        assertTrue(windows.morningNotification)
        assertEquals(LocalTime(9, 0), windows.notificationTime)
        assertTrue(windows.keepInTray)
        assertFalse(windows.trayHintShown)
        assertNull(windows.lastNotification)
    }

    @Test
    fun savesTheWindowsPanel() = runTest {
        update.setLaunchAtStartup(false)
        update.setMorningNotification(false)
        update.setNotificationTime(LocalTime(7, 45))
        update.setKeepInTray(false)
        update.markTrayHintShown()
        val windows = repository.state.value.windows
        assertFalse(windows.launchAtStartup)
        assertFalse(windows.morningNotification)
        assertEquals(LocalTime(7, 45), windows.notificationTime)
        assertFalse(windows.keepInTray)
        assertTrue(windows.trayHintShown)
    }

    @Test
    fun rejectsANotificationTimeOffTheQuarters() = runTest {
        assertFailsWith<IllegalArgumentException> { update.setNotificationTime(LocalTime(9, 10)) }
        assertEquals(LocalTime(9, 0), repository.state.value.windows.notificationTime)
    }

    @Test
    fun savesTheDisplayStyle() = runTest {
        assertEquals(DisplayStyle.POLAROID, repository.state.value.displayStyle, "Polaroïd by default")
        update.setDisplayStyle(DisplayStyle.VHS)
        assertEquals(DisplayStyle.VHS, repository.state.value.displayStyle)
        update.setDisplayStyle(DisplayStyle.EPHEMERIS)
        assertEquals(DisplayStyle.EPHEMERIS, repository.state.value.displayStyle)
    }

    @Test
    fun savesTheLanguage() = runTest {
        assertEquals(null, repository.state.value.language, "the system language by default")
        update.setLanguage(AppLanguage.GERMAN)
        assertEquals(AppLanguage.GERMAN, repository.state.value.language)
    }
}
