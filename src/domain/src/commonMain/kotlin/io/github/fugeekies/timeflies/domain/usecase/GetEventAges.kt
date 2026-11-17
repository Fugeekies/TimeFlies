package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventAges
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.rules.Ages
import kotlinx.datetime.LocalDate

/** The people's ages on the date of an event, for the "Quel âge ?" screen (docs/UISpec/EventAges.template.md). */
class GetEventAges {
    /** Ages of the people born on [births] (the user first) on the date of [event] in [region]; negative before a birth. */
    operator fun invoke(event: Event, births: List<LocalDate>, region: Region = Region.FRANCE): EventAges {
        val date = event.dateFor(region)
        return EventAges(event, date, births.map { Ages.age(it, date) })
    }
}
