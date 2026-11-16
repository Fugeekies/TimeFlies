package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.AppRelease
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.ReleaseSource
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.rules.Versions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReleaseUpdatesTest {

    private class InMemorySettings : SettingsRepository {
        val state = MutableStateFlow(Settings())
        override val settings: Flow<Settings> = state
        override suspend fun update(transform: (Settings) -> Settings) {
            state.value = transform(state.value)
        }
    }

    private val settings = InMemorySettings()
    private var day = LocalDate(2026, 11, 16)
    private val clock = Clock { day }
    private var calls = 0
    private var published: AppRelease? = AppRelease("0.2.0", "https://github.com/Fugeekies/TimeFlies/releases/tag/v0.2.0")
    private val source = ReleaseSource { calls++; published }

    private fun updates(current: String? = "0.1.0") = ReleaseUpdates(source, settings, clock, current)

    @Test
    fun comparesVersionsNumberByNumber() {
        assertTrue(Versions.isNewer("0.2.0", "0.1.9"))
        assertTrue(Versions.isNewer("v1.0.0", "0.9.9"))
        assertTrue(Versions.isNewer("0.1.10", "0.1.9"), "10 after 9, not as text")
        assertFalse(Versions.isNewer("0.1.0", "0.1.0"))
        assertFalse(Versions.isNewer("0.1.0", "0.2.0"))
        assertFalse(Versions.isNewer("bêta", "0.1.0"))
    }

    @Test
    fun asksGitHubOnceADayAndShowsTheNewerVersion() = runTest {
        val updates = updates()
        updates.checkIfDue()
        updates.checkIfDue()
        assertEquals(1, calls, "once a day")
        assertEquals("0.2.0", updates.banner().first()?.version)
        day = LocalDate(2026, 11, 17)
        updates.checkIfDue()
        assertEquals(2, calls, "again the next day")
    }

    @Test
    fun showsNothingForTheRunningOrAnOlderVersion() = runTest {
        val updates = updates(current = "0.2.0")
        updates.checkIfDue()
        assertNull(updates.banner().first())
    }

    @Test
    fun plusTardHidesThisVersionOnly() = runTest {
        val updates = updates()
        updates.checkIfDue()
        updates.dismiss("0.2.0")
        assertNull(updates.banner().first())
        published = AppRelease("0.3.0", "https://github.com/Fugeekies/TimeFlies/releases/tag/v0.3.0")
        day = LocalDate(2026, 11, 17)
        updates.checkIfDue()
        assertEquals("0.3.0", updates.banner().first()?.version, "the next version shows a banner again")
    }

    @Test
    fun neverAsksWhenTurnedOffOrInALocalBuild() = runTest {
        updates(current = null).checkIfDue()
        assertEquals(0, calls, "a local build has no version to compare")
        val updates = updates()
        UpdateSettings(settings, clock).setReleaseCheck(false)
        updates.checkIfDue()
        assertEquals(0, calls)
        assertNull(updates.banner().first())
    }

    @Test
    fun keepsTheLastAnswerWhenOffline() = runTest {
        val updates = updates()
        updates.checkIfDue()
        published = null
        day = LocalDate(2026, 11, 17)
        updates.checkIfDue()
        assertEquals("0.2.0", updates.banner().first()?.version)
        assertEquals(day, settings.state.value.releaseCheck.lastCheck, "the day is marked even offline")
    }
}
