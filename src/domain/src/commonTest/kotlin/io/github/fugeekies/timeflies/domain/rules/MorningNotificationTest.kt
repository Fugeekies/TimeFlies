package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MorningNotificationTest {

    private val today = LocalDate(2026, 11, 2)
    private val defaults = WindowsSettings()

    @Test
    fun isDueFromTheChosenTime() {
        assertFalse(MorningNotification.isDue(defaults, today, LocalTime(8, 59)))
        assertTrue(MorningNotification.isDue(defaults, today, LocalTime(9, 0)))
    }

    @Test
    fun isStillDueLaterInTheDayAfterASleep() {
        assertTrue(MorningNotification.isDue(defaults, today, LocalTime(14, 30)))
    }

    @Test
    fun isSentOnlyOnceADay() {
        val sent = defaults.copy(lastNotification = today)
        assertFalse(MorningNotification.isDue(sent, today, LocalTime(9, 0)))
        assertTrue(MorningNotification.isDue(sent, LocalDate(2026, 11, 3), LocalTime(9, 0)), "the next day it is due again")
    }

    @Test
    fun isNeverDueWhenTurnedOff() {
        assertFalse(MorningNotification.isDue(defaults.copy(morningNotification = false), today, LocalTime(12, 0)))
    }

    @Test
    fun offersEveryQuarterOfAnHour() {
        assertEquals(96, MorningNotification.TIMES.size)
        assertEquals(LocalTime(0, 0), MorningNotification.TIMES.first())
        assertEquals(LocalTime(23, 45), MorningNotification.TIMES.last())
        assertTrue(LocalTime(9, 0) in MorningNotification.TIMES)
        assertTrue(MorningNotification.TIMES.all(MorningNotification::isQuarterHour))
        assertFalse(MorningNotification.isQuarterHour(LocalTime(9, 10)))
    }
}
