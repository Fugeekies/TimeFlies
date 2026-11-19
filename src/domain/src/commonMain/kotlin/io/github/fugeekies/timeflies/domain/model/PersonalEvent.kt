package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate

/**
 * An event the user added in Settings › Mes événements (decision of 19/11/2026): a title and a date to the day, kept on
 * the device, exported and imported as a file to change phones. Shown on Today (widget and notification included) and
 * on "Quel âge ?", never on Equivalences (its type has no match).
 *
 * @property id made on the device when the event is added; not kept in the exported file.
 * @property title as the user typed it, in a single language.
 */
data class PersonalEvent(val id: String, val title: String, val date: LocalDate) {

    /** The event as the screens show it: type, kind and family "personnel", the same date in every region. */
    fun toEvent(): Event = Event(
        id = id,
        date = date,
        regionalDates = emptyMap(),
        titles = mapOf(Event.FRENCH to title),
        kind = EventKind.PERSONAL,
        group = GROUP,
        importance = IMPORTANCE,
        source = "",
    )

    /** Same title (ignoring case and spaces around) and same date: an import does not add it twice. */
    fun sameAs(other: PersonalEvent): Boolean = date == other.date && title.trim().equals(other.title.trim(), ignoreCase = true)

    companion object {
        const val MAX_TITLE_LENGTH = 80

        /** The highest, as the events everybody knows: on a given day, the user's own events come first. */
        const val IMPORTANCE = 3

        val GROUP = EventGroup(EventType.PERSONAL, EventGroup.INTERNATIONAL)
    }
}

/** Why a personal event cannot be saved. */
enum class PersonalEventError {
    /** No title, or only spaces. */
    TITLE_MISSING,

    /** More than [PersonalEvent.MAX_TITLE_LENGTH] characters. */
    TITLE_TOO_LONG,

    /** After today: the app counts the years since an event. */
    DATE_IN_FUTURE,
}

/**
 * Outcome of an import.
 *
 * @property added events added to those of the device.
 * @property duplicates events already there (same title and date), not added again.
 * @property invalid entries of the file left out: no title, a title too long, a missing or future date.
 */
sealed interface PersonalImportResult {
    data class Imported(val added: Int, val duplicates: Int, val invalid: Int) : PersonalImportResult

    /** Not a file exported by TimeFlies, or a newer format than this version reads. */
    data object NotAnExport : PersonalImportResult
}
