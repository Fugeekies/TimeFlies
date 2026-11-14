package io.github.fugeekies.timeflies.data.events

import kotlinx.serialization.Serializable

/**
 * One event as written in a JSON file (docs/EVENTS_DATA.md). Every field is optional here so that a
 * faulty entry is reported with all its problems instead of making the whole file unreadable.
 */
@Serializable
internal data class EventDto(
    val id: String? = null,
    val date: String? = null,
    val regionalDates: Map<String, String> = emptyMap(),
    val title: Map<String, String> = emptyMap(),
    val kind: String? = null,
    val type: String? = null,
    val importance: Int? = null,
    val source: String? = null,
    val note: String? = null,
)
