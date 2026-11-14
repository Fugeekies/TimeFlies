package io.github.fugeekies.timeflies.domain.model

import kotlinx.datetime.LocalDate

/**
 * A dated event of the base (docs/EVENTS_DATA.md, "Format d'un événement").
 *
 * @property date reference date: first worldwide release for a work or a product, the date of the event otherwise.
 * @property regionalDates dates in the countries where they differ from [date].
 * @property titles title by language code ("fr" is always present).
 * @property importance 1 (anecdotal) to 3 (known by everyone); breaks ties between anniversaries of the same day.
 * @property source URL that confirms the date.
 */
data class Event(
    val id: String,
    val date: LocalDate,
    val regionalDates: Map<Region, LocalDate>,
    val titles: Map<String, String>,
    val kind: EventKind,
    val type: EventType,
    val importance: Int,
    val source: String,
) {
    val family: Family get() = type.family

    /** The date of the event in [region], or the reference date when the region has none. */
    fun dateFor(region: Region): LocalDate = regionalDates[region] ?: date

    /** The title in [language], falling back to French. */
    fun title(language: String): String = titles[language] ?: titles.getValue(FRENCH)

    companion object {
        const val FRENCH = "fr"
    }
}
