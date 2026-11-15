package io.github.fugeekies.timeflies.data.time

import io.github.fugeekies.timeflies.domain.port.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

/**
 * The system clock, whose date can be replaced by a fake one (debug builds only, docs/DEVELOPMENT.md).
 * A fake date stays the same after midnight: the debug panel moves it by hand.
 */
class AdjustableClock(private val system: Clock) : Clock {
    private val override = MutableStateFlow<LocalDate?>(null)

    /** The fake date, or null when the system date is used. */
    val fakeDate: StateFlow<LocalDate?> = override.asStateFlow()

    fun set(date: LocalDate?) {
        override.value = date
    }

    override fun today(): LocalDate = override.value ?: system.today()

    override fun manualChanges(): Flow<Unit> = override.drop(1).map { }
}
