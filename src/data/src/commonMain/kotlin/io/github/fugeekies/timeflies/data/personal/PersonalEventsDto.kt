package io.github.fugeekies.timeflies.data.personal

import kotlinx.serialization.Serializable

/**
 * The user's own events as stored in `personal-events.json` and as exported to change phones (decision of 19/11/2026,
 * docs/ARCHITECTURE.md, "Événements personnels"). The exported file is the same, without the ids.
 *
 * @property format always [FORMAT]: an import refuses any other JSON file.
 * @property version a file of a newer version is refused rather than half read.
 */
@Serializable
internal data class PersonalEventsDto(
    val format: String = FORMAT,
    val version: Int = CURRENT_VERSION,
    val events: List<PersonalEventDto> = emptyList(),
) {
    companion object {
        const val FORMAT = "timeflies-personal-events"
        const val CURRENT_VERSION = 1
    }
}

/**
 * @property id null in an exported file; given again on import.
 * @property date "2015-06-20", ISO 8601, read with care: an entry with an unreadable date is counted as invalid.
 */
@Serializable
internal data class PersonalEventDto(
    val id: String? = null,
    val title: String = "",
    val date: String = "",
)
