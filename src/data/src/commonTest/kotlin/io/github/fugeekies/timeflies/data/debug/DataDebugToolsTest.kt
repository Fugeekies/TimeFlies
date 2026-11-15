package io.github.fugeekies.timeflies.data.debug

import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.data.time.AdjustableClock
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Test-only data set: made-up titles.
class DataDebugToolsTest {

    private val cinema = """
        [
          {"id":"film-a","date":"2011-11-02","title":{"fr":"Film A"},
           "kind":"film-release","type":"blockbuster","importance":3,"source":"https://example.org/a"},
          {"id":"film-b","date":"1994-11-09","title":{"fr":"Film B"},
           "kind":"film-release","type":"blockbuster","importance":3,"source":"https://example.org/b"},
          {"id":"bad","date":"not-a-date","title":{"fr":"Mauvaise date"},
           "kind":"film-release","type":"blockbuster","importance":3,"source":"https://example.org/c"}
        ]
    """.trimIndent()

    private class InMemorySettings : SettingsRepository {
        val state = MutableStateFlow(Settings())
        override val settings: Flow<Settings> = state
        override suspend fun update(transform: (Settings) -> Settings) {
            state.value = transform(state.value)
        }
    }

    private val system = Clock { LocalDate(2026, 11, 15) }
    private val clock = AdjustableClock(system)
    private val settings = InMemorySettings()
    private val events = JsonEventRepository(
        readFile = { path -> if (path == "files/events/cinema.json") cinema.encodeToByteArray() else null },
        clock = system,
    )
    private val tools = DataDebugTools(clock, settings, events)

    @Test
    fun replacesTheDateOfTheDayAndGoesBack() {
        assertNull(tools.fakeDate.value)
        tools.setFakeDate(LocalDate(2028, 2, 29))
        assertEquals(LocalDate(2028, 2, 29), clock.today())
        tools.setFakeDate(null)
        assertEquals(LocalDate(2026, 11, 15), clock.today())
    }

    @Test
    fun tellsScreensWhenTheDateIsChangedByHand() = runTest(UnconfinedTestDispatcher()) {
        val change = async { clock.manualChanges().first() }
        yield()
        tools.setFakeDate(LocalDate(2027, 2, 28))
        change.await()
        assertTrue(change.isCompleted)
    }

    @Test
    fun forcesSettingsWithoutChecks() = runTest {
        tools.forceSettings { it.copy(onboardingDone = true) }
        assertTrue(settings.state.value.onboardingDone)
    }

    @Test
    fun givesTheStatisticsOfTheBase() = runTest {
        val stats = tools.baseStats()
        assertEquals(2, stats.eventCount)
        assertEquals(mapOf(Family.CINEMA to 2), stats.eventsByFamily)
        assertEquals(366 - 2, stats.emptyDays.size)
        assertEquals(2, stats.typesBelowMinimum[EventType.BLOCKBUSTER])
        assertEquals(1, stats.rejections.size)
        assertTrue("bad" in stats.rejections.single(), stats.rejections.single())
    }
}
