package io.github.fugeekies.timeflies.domain.port

import io.github.fugeekies.timeflies.domain.model.Settings
import kotlinx.coroutines.flow.Flow

/** The user's settings, stored on the device. Implemented by the data layer (DataStore). */
interface SettingsRepository {
    /** The current settings, then every change. */
    val settings: Flow<Settings>

    /** Applies [transform] to the stored settings atomically and saves the result. */
    suspend fun update(transform: (Settings) -> Settings)
}
