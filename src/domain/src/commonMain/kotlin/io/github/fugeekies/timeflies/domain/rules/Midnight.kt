package io.github.fugeekies.timeflies.domain.rules

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * When the day changes on the device clock: screens and the Android widget recompute just after local midnight
 * (docs/ARCHITECTURE.md, "Flux de données"). A fake date of the debug panel does not move on its own.
 */
object Midnight {
    /**
     * Time from [now] until [margin] past the next local midnight in [zone], never less than [margin], so the new
     * day is always seen.
     */
    fun delayUntilNext(now: Instant, zone: TimeZone, margin: Duration = 1.seconds): Duration {
        val nextMidnight = now.toLocalDateTime(zone).date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(zone)
        return maxOf(nextMidnight - now + margin, margin)
    }
}
