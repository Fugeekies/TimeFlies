package io.github.fugeekies.timeflies.ui

import io.github.fugeekies.timeflies.ui.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.MissingResourceException

/**
 * Reads a file shipped in `composeResources/files`, or returns null when it does not exist.
 * Handed by the apps to the data layer, which stays independent of Compose (docs/ARCHITECTURE.md).
 */
@OptIn(ExperimentalResourceApi::class)
suspend fun readResourceFile(path: String): ByteArray? =
    try {
        Res.readBytes(path)
    } catch (e: MissingResourceException) {
        null
    }
