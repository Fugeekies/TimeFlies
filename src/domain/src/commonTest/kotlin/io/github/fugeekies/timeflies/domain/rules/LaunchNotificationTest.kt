package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LaunchNotificationTest {

    private val today = LocalDate(2026, 11, 2)
    private val defaults = WindowsSettings()

    @Test
    fun isDueAtTheFirstStartOfTheDay() {
        assertTrue(LaunchNotification.isDue(defaults, today))
    }

    @Test
    fun isSentOnlyOnceADay() {
        val sent = defaults.copy(lastNotification = today)
        assertFalse(LaunchNotification.isDue(sent, today), "a second start the same day")
        assertTrue(LaunchNotification.isDue(sent, LocalDate(2026, 11, 3)), "the next day it is due again")
    }

    @Test
    fun isNeverDueWhenTurnedOff() {
        assertFalse(LaunchNotification.isDue(defaults.copy(launchNotification = false), today))
    }
}
