package io.github.fugeekies.timeflies.domain.port

import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * The user's own events, stored on the device (decision of 19/11/2026). Implemented by the data layer (a JSON file
 * through DataStore, next to the settings). The checks are made by the use case before.
 */
interface PersonalEventRepository {
    /** The events, sorted by date, then every change. */
    val events: Flow<List<PersonalEvent>>

    /** Adds an event with a new id. */
    suspend fun add(title: String, date: LocalDate)

    /** Changes the title and date of the event [id]; nothing if it no longer exists. */
    suspend fun update(id: String, title: String, date: LocalDate)

    suspend fun remove(id: String)

    /** Adds several events at once, in a single write. */
    suspend fun addAll(events: List<Pair<String, LocalDate>>)

    /** The text of the export file: every event, in the format [decode] reads. */
    suspend fun encode(): String

    /**
     * The events of an exported file, title and date as written in it (not checked); null when it is not an export of
     * TimeFlies or comes from a newer version. Entries without a readable date are counted in [Decoded.unreadable].
     */
    fun decode(text: String): Decoded?

    data class Decoded(val events: List<Pair<String, LocalDate>>, val unreadable: Int)
}
