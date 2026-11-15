package io.github.fugeekies.timeflies.domain.port

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.LocalDate

/** Gives today's date in the device time zone. Injected everywhere so rules can be tested on fixed dates. */
fun interface Clock {
    fun today(): LocalDate

    /**
     * Emits when today's date is changed by hand (debug panel's fake date), so screens recompute at once.
     * The passing of midnight is not included; the system clock never emits.
     */
    fun manualChanges(): Flow<Unit> = emptyFlow()
}
