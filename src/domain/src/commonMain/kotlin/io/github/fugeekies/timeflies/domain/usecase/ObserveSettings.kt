package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import kotlinx.coroutines.flow.Flow

/** The current settings, then every change. Screens recompute when they change. */
class ObserveSettings(private val settings: SettingsRepository) {
    operator fun invoke(): Flow<Settings> = settings.settings
}
