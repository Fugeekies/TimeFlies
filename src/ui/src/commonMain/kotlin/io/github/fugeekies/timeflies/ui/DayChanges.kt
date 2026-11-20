package io.github.fugeekies.timeflies.ui

import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Midnight
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.datetime.TimeZone
import kotlin.time.Clock as KotlinClock

/**
 * Emits at once, then one second past each local midnight and whenever the debug panel changes the date by hand:
 * every screen that depends on today's date recomputes, even when the app stays open overnight.
 */
fun Clock.dayChanges(): Flow<Unit> = merge(midnights(), manualChanges())

private fun midnights(): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        // The system midnight, not the one of Clock.today(): a fake date of the debug panel does not move on its own.
        delay(Midnight.delayUntilNext(KotlinClock.System.now(), TimeZone.currentSystemDefault()))
    }
}
