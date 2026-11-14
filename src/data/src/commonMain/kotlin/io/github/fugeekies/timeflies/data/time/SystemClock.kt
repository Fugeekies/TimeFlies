package io.github.fugeekies.timeflies.data.time

import io.github.fugeekies.timeflies.domain.port.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock as KotlinClock

/** Today's date from the system clock, in the device time zone (read on every call, so a change applies at once). */
class SystemClock : Clock {
    override fun today(): LocalDate = KotlinClock.System.todayIn(TimeZone.currentSystemDefault())
}
