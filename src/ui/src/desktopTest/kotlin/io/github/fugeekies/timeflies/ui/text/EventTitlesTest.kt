package io.github.fugeekies.timeflies.ui.text

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.EventType
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Event titles follow the language of the interface, French when a language has none (docs/EVENTS_DATA.md). */
class EventTitlesTest {

    private val event = Event(
        id = "film-test",
        date = LocalDate(2011, 11, 2),
        regionalDates = emptyMap(),
        titles = mapOf("fr" to "Intouchables", "de" to "Ziemlich beste Freunde"),
        kind = EventKind.FILM_RELEASE,
        group = EventGroup(EventType.FRENCH_COMEDY_HIT, "fr"),
        importance = 3,
        source = "https://example.org",
    )

    @AfterTest
    fun backToFrench() = AppLocale.set(AppLanguage.FRENCH)

    @Test
    fun showsTheTitleOfTheChosenLanguageElseTheFrenchOne() {
        AppLocale.set(AppLanguage.GERMAN)
        assertEquals("Ziemlich beste Freunde", event.shownTitle)
        AppLocale.set(AppLanguage.SPANISH)
        assertEquals("Intouchables", event.shownTitle, "no Spanish title: the French one")
    }
}
