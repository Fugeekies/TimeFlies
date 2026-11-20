package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.model.UserAge
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.testEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SendLaunchNotificationTest {

    private class InMemorySettings(initial: Settings) : SettingsRepository {
        val state = MutableStateFlow(initial)
        override val settings: Flow<Settings> = state
        override suspend fun update(transform: (Settings) -> Settings) {
            state.value = transform(state.value)
        }
    }

    private class FakeEvents(private val events: List<Event>) : EventRepository {
        override suspend fun onMonthDay(monthDay: MonthDay, region: Region) = events.filter { MonthDay.of(it.dateFor(region)) == monthDay }
        override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region) = emptyList<Event>()
        override suspend fun search(query: String, limit: Int) = emptyList<Event>()
        override suspend fun count() = events.size
    }

    private val today = LocalDate(2026, 11, 2)
    private val clock = Clock { today }
    private val settings = InMemorySettings(Settings(birth = YearMonth(1991, Month.MAY), onboardingDone = true))
    private val send = SendLaunchNotification(GetDailyShock(FakeEvents(listOf(testEvent("film", "2011-11-02"))), clock), settings, clock)

    @Test
    fun sendsTheDailyShockOnceWhenDue() = runTest {
        val shock = assertNotNull(send.ifDue())
        assertEquals("film", shock.main?.event?.id)
        assertEquals(UserAge.Years(20), shock.main?.userAge, "with the user's age, as on Today")
        assertEquals(today, settings.state.value.windows.lastNotification)
        assertNull(send.ifDue(), "a second start the same day sends nothing")
    }

    @Test
    fun sendsAtOnceWithoutMarkingTheDay() = runTest {
        assertEquals("film", send.now().main?.event?.id)
        assertNull(settings.state.value.windows.lastNotification)
        assertNotNull(send.ifDue(), "the notification of the start still goes out")
    }
}
