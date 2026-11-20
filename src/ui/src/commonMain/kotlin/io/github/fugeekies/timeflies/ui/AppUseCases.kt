package io.github.fugeekies.timeflies.ui

import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.GetEventAges
import io.github.fugeekies.timeflies.domain.usecase.GetEventGroups
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.PersonalEvents
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings

/**
 * Every use case the shared screens call, and the clock, built once by the app's composition root (`AppGraph`,
 * docs/ARCHITECTURE.md): the screens never see the data layer.
 */
class AppUseCases(
    val getDailyShock: GetDailyShock,
    val getEquivalences: GetEquivalences,
    val getEventAges: GetEventAges,
    val searchEvents: SearchEvents,
    val observeSettings: ObserveSettings,
    val updateSettings: UpdateSettings,
    val completeOnboarding: CompleteOnboarding,
    val countEvents: CountEvents,
    val getEventGroups: GetEventGroups,
    val personalEvents: PersonalEvents,
    val clock: Clock,
)
