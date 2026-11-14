package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import io.github.fugeekies.timeflies.domain.testEvent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDailyShockTest {

    private class FakeRepository(private val events: List<Event>) : EventRepository {
        val askedDays = mutableListOf<MonthDay>()

        override suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event> {
            askedDays += monthDay
            return events.filter { MonthDay.of(it.dateFor(region)) == monthDay }
        }

        override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region) = emptyList<Event>()
        override suspend fun search(query: String, limit: Int) = emptyList<Event>()
        override suspend fun count() = events.size
    }

    @Test
    fun asksTheRepositoryForTodayAndTheNextSevenDays() = runTest {
        val repository = FakeRepository(listOf(testEvent("film", "2011-11-02"), testEvent("soon", "2001-11-06")))
        val shock = GetDailyShock(repository, Clock { LocalDate(2026, 11, 2) })()
        assertEquals((2..9).map { MonthDay(11, it) }, repository.askedDays)
        assertEquals("film", shock.main?.event?.id)
        assertEquals(listOf("soon"), shock.upcoming.flatMap { day -> day.anniversaries.map { it.event.id } })
    }

    @Test
    fun alsoAsksForFebruary29OnFebruary28OfACommonYear() = runTest {
        val repository = FakeRepository(listOf(testEvent("leap", "2000-02-29")))
        val shock = GetDailyShock(repository, Clock { LocalDate(2026, 2, 28) })()
        assertEquals(listOf(MonthDay(2, 28), MonthDay(2, 29)), repository.askedDays.take(2))
        assertEquals("leap", shock.main?.event?.id)
        assertEquals(0, shock.mainInDays)
    }

    @Test
    fun passesTheBirthToComputeTheUserAge() = runTest {
        val repository = FakeRepository(listOf(testEvent("film", "2011-11-02")))
        val shock = GetDailyShock(repository, Clock { LocalDate(2026, 11, 2) })(birth = LocalDate(1991, 5, 1))
        assertEquals(20, shock.main?.userAge)
    }
}
