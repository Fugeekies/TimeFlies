package io.github.fugeekies.timeflies.domain.port

import kotlinx.datetime.LocalDate

/** Gives today's date in the device time zone. Injected everywhere so rules can be tested on fixed dates. */
fun interface Clock {
    fun today(): LocalDate
}
