package io.github.fugeekies.timeflies.domain.rules

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MidnightTest {

    private val paris = TimeZone.of("Europe/Paris")

    @Test
    fun waitsUntilJustAfterTheNextMidnight() {
        val now = LocalDateTime(2026, 11, 16, 22, 0).toInstant(paris)
        assertEquals(2.hours + 1.seconds, Midnight.delayUntilNext(now, paris))
    }

    @Test
    fun takesTheMarginAsked() {
        val now = LocalDateTime(2026, 11, 16, 23, 59).toInstant(paris)
        assertEquals(2.minutes, Midnight.delayUntilNext(now, paris, margin = 1.minutes))
    }

    @Test
    fun followsAShorterDayAtTheSpringClockChange() {
        // 29 March 2026 has 23 hours in Paris (2:00 becomes 3:00): from 0:30, midnight comes in 22 h 30.
        val now = LocalDateTime(2026, 3, 29, 0, 30).toInstant(paris)
        assertEquals(22.hours + 30.minutes + 1.seconds, Midnight.delayUntilNext(now, paris))
    }
}
