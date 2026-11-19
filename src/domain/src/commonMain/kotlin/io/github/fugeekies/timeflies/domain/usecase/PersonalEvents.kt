package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import io.github.fugeekies.timeflies.domain.model.PersonalEventError
import io.github.fugeekies.timeflies.domain.model.PersonalImportResult
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.PersonalEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

/**
 * Settings › Mes événements (decision of 19/11/2026): the user's own events, added, changed, removed, exported to a file
 * and imported from it on another phone or PC.
 */
class PersonalEvents(
    private val repository: PersonalEventRepository,
    private val clock: Clock,
) {
    /** The events sorted by date, then every change. */
    fun observe(): Flow<List<PersonalEvent>> = repository.events

    /** Why [title] and [date] cannot be saved, or null when they can. */
    fun check(title: String, date: LocalDate): PersonalEventError? {
        val trimmed = title.trim()
        return when {
            trimmed.isEmpty() -> PersonalEventError.TITLE_MISSING
            trimmed.length > PersonalEvent.MAX_TITLE_LENGTH -> PersonalEventError.TITLE_TOO_LONG
            date > clock.today() -> PersonalEventError.DATE_IN_FUTURE
            else -> null
        }
    }

    /** Adds the event, or returns why it cannot be (nothing saved). */
    suspend fun add(title: String, date: LocalDate): PersonalEventError? =
        check(title, date) ?: run { repository.add(title.trim(), date); null }

    /** Changes the event [id], or returns why it cannot be (nothing saved). */
    suspend fun update(id: String, title: String, date: LocalDate): PersonalEventError? =
        check(title, date) ?: run { repository.update(id, title.trim(), date); null }

    suspend fun remove(id: String) = repository.remove(id)

    /** The text of the file to save: every event, without its id. */
    suspend fun export(): String = repository.encode()

    /**
     * Adds the events of an exported file to those of the device: the ones already there (same title and date) and the
     * invalid ones are left out, nothing is removed nor replaced.
     */
    suspend fun import(text: String): PersonalImportResult {
        val decoded = repository.decode(text) ?: return PersonalImportResult.NotAnExport
        val kept = repository.events.first().toMutableList()
        val toAdd = mutableListOf<Pair<String, LocalDate>>()
        var duplicates = 0
        var invalid = decoded.unreadable
        for ((title, date) in decoded.events) {
            if (check(title, date) != null) {
                invalid++
                continue
            }
            val candidate = PersonalEvent("", title.trim(), date)
            if (kept.any { it.sameAs(candidate) }) {
                duplicates++
                continue
            }
            kept += candidate
            toAdd += candidate.title to date
        }
        if (toAdd.isNotEmpty()) repository.addAll(toAdd)
        return PersonalImportResult.Imported(toAdd.size, duplicates, invalid)
    }
}
