package io.github.fugeekies.timeflies.domain.usecase

import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import io.github.fugeekies.timeflies.domain.model.PersonalEventError
import io.github.fugeekies.timeflies.domain.model.PersonalImportResult
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.PersonalEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PersonalEventsTest {

    /** Keeps the events in memory; the file format is the data layer's, so [decode] reads "title|date" lines here. */
    private class MemoryRepository : PersonalEventRepository {
        private var next = 0
        override val events = MutableStateFlow(emptyList<PersonalEvent>())

        override suspend fun add(title: String, date: LocalDate) = addAll(listOf(title to date))

        override suspend fun addAll(events: List<Pair<String, LocalDate>>) =
            this.events.update { current -> (current + events.map { (title, date) -> PersonalEvent("p${next++}", title, date) }).sortedBy { it.date } }

        override suspend fun update(id: String, title: String, date: LocalDate) =
            events.update { current -> current.map { if (it.id == id) it.copy(title = title, date = date) else it } }

        override suspend fun remove(id: String) = events.update { current -> current.filterNot { it.id == id } }

        override suspend fun encode(): String = events.value.joinToString("\n") { "${it.title}|${it.date}" }

        override fun decode(text: String): PersonalEventRepository.Decoded? {
            if (!text.startsWith("export")) return null
            var unreadable = 0
            val events = text.lines().drop(1).filter { it.isNotBlank() }.mapNotNull { line ->
                val (title, date) = line.split('|')
                runCatching { title to LocalDate.parse(date) }.getOrNull() ?: null.also { unreadable++ }
            }
            return PersonalEventRepository.Decoded(events, unreadable)
        }
    }

    private val today = LocalDate(2026, 11, 19)
    private val repository = MemoryRepository()
    private val personal = PersonalEvents(repository, Clock { today })

    @Test
    fun addsAnEventWithATrimmedTitle() = runTest {
        assertNull(personal.add("  Notre mariage ", LocalDate(2015, 6, 20)))
        val saved = personal.observe().first().single()
        assertEquals("Notre mariage", saved.title)
        assertEquals(LocalDate(2015, 6, 20), saved.date)
    }

    @Test
    fun refusesAMissingTitleATooLongOneAndAFutureDate() = runTest {
        assertEquals(PersonalEventError.TITLE_MISSING, personal.add("   ", LocalDate(2015, 6, 20)))
        assertEquals(PersonalEventError.TITLE_TOO_LONG, personal.add("x".repeat(PersonalEvent.MAX_TITLE_LENGTH + 1), LocalDate(2015, 6, 20)))
        assertEquals(PersonalEventError.DATE_IN_FUTURE, personal.add("Demain", LocalDate(2026, 11, 20)))
        assertNull(personal.check("Aujourd'hui", today), "today is allowed")
        assertTrue(personal.observe().first().isEmpty(), "nothing saved")
    }

    @Test
    fun updatesAndRemoves() = runTest {
        personal.add("Mariage", LocalDate(2015, 6, 20))
        val id = personal.observe().first().single().id
        assertNull(personal.update(id, "Notre mariage", LocalDate(2015, 6, 21)))
        assertEquals(PersonalEvent(id, "Notre mariage", LocalDate(2015, 6, 21)), personal.observe().first().single())
        assertEquals(PersonalEventError.TITLE_MISSING, personal.update(id, "", LocalDate(2015, 6, 21)))
        personal.remove(id)
        assertTrue(personal.observe().first().isEmpty())
    }

    @Test
    fun importAddsTheNewEventsOnlyAndCountsTheOthers() = runTest {
        personal.add("Notre mariage", LocalDate(2015, 6, 20))
        val file = listOf(
            "export",
            "notre mariage |2015-06-20", // already there: same title ignoring case and spaces, same date
            "Naissance de Léa|2018-03-02",
            "Naissance de Léa|2018-03-02", // twice in the file
            "|2010-01-01", // no title
            "Plus tard|2030-01-01", // future
            "Date cassée|2018-02-31", // unreadable
        ).joinToString("\n")
        val result = personal.import(file)
        assertEquals(PersonalImportResult.Imported(added = 1, duplicates = 2, invalid = 3), result)
        assertEquals(listOf("Notre mariage", "Naissance de Léa"), personal.observe().first().map { it.title })
    }

    @Test
    fun importRefusesAFileThatIsNotAnExport() = runTest {
        assertIs<PersonalImportResult.NotAnExport>(personal.import("{\"hello\":1}"))
        assertTrue(personal.observe().first().isEmpty())
    }

    @Test
    fun aPersonalEventIsShownAsAnEventOfItsOwnTypeAndFamily() {
        val event = PersonalEvent("p1", "Notre mariage", LocalDate(2015, 6, 20)).toEvent()
        assertEquals(EventType.PERSONAL, event.type)
        assertEquals(Family.PERSONAL, event.family)
        assertEquals(EventKind.PERSONAL, event.kind)
        assertEquals("Notre mariage", event.title("en"), "a single title, whatever the language")
        assertEquals(PersonalEvent.IMPORTANCE, event.importance)
    }

    @Test
    fun theBaseCannotHoldPersonalEvents() {
        assertIs<EventGroup.Result.Invalid>(EventGroup.parse("personal/personal/fr"))
        assertTrue(Family.PERSONAL !in Family.THEMES)
    }
}
