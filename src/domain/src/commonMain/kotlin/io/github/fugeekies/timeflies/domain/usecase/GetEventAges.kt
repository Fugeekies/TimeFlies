package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventAges
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.rules.Ages
import kotlinx.datetime.LocalDate

/** The two people's ages on the date of a chosen event, for the "Quel âge ?" screen (docs/UISpec/EventAges.template.md). */
class GetEventAges {
    /** Ages of the people born on [userBirth] and [otherBirth] on the date of [event] in [region]; negative before a birth. */
    operator fun invoke(event: Event, userBirth: LocalDate, otherBirth: LocalDate, region: Region = Region.FRANCE): EventAges {
        val date = event.dateFor(region)
        return EventAges(event, date, Ages.age(userBirth, date), Ages.age(otherBirth, date))
    }
}
