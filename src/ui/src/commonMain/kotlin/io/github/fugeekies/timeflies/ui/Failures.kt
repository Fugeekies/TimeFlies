package io.github.fugeekies.timeflies.ui

import kotlinx.coroutines.CancellationException

/**
 * Runs [block] and returns its result; on a failure, returns what [onFailure] gives (an error state, null, an empty
 * list). A cancellation is never caught: the coroutine stops as it should when the screen or the app goes away.
 */
inline fun <T> recoverFailures(onFailure: (Exception) -> T, block: () -> T): T =
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        onFailure(e)
    }
