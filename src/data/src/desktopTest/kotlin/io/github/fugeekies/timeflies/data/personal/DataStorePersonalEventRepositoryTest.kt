package io.github.fugeekies.timeflies.data.personal

import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DataStorePersonalEventRepositoryTest {

    private val folder: Path = Files.createTempDirectory("timeflies-personal").toOkioPath()
    private val file = folder / "TimeFlies" / "personal-events.json"
    private val scopes = mutableListOf<CoroutineScope>()

    /** A new repository on [file], as after a restart; the previous one is fully stopped first (a DataStore rule). */
    private suspend fun open(): DataStorePersonalEventRepository {
        scopes.forEach { it.coroutineContext.job.cancelAndJoin() }
        scopes.clear()
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob()).also { scopes += it }
        return DataStorePersonalEventRepository(FileSystem.SYSTEM, file, scope)
    }

    @AfterTest
    fun cleanUp() {
        runBlocking { scopes.forEach { it.coroutineContext.job.cancelAndJoin() } }
        FileSystem.SYSTEM.deleteRecursively(folder)
    }

    @Test
    fun isEmptyWithoutAFile() = runBlocking {
        assertTrue(open().events.first().isEmpty())
    }

    @Test
    fun keepsTheEventsSortedByDateAcrossARestart() = runBlocking {
        val repository = open()
        repository.add("Naissance de Léa", LocalDate(2018, 3, 2))
        repository.add("Notre mariage", LocalDate(2015, 6, 20))
        val again = open().events.first()
        assertEquals(listOf("Notre mariage", "Naissance de Léa"), again.map { it.title })
        assertEquals(2, again.map { it.id }.toSet().size, "each event has its own id")
        assertTrue(again.all { it.id.startsWith(DataStorePersonalEventRepository.ID_PREFIX) })
    }

    @Test
    fun updatesAndRemovesByIdOnly() = runBlocking {
        val repository = open()
        repository.addAll(listOf("A" to LocalDate(2000, 1, 1), "B" to LocalDate(2001, 1, 1)))
        val (a, b) = repository.events.first()
        repository.update(a.id, "A bis", LocalDate(2000, 1, 2))
        repository.remove(b.id)
        assertEquals(listOf(PersonalEvent(a.id, "A bis", LocalDate(2000, 1, 2))), repository.events.first())
    }

    @Test
    fun theExportHasNoIdAndReadsBack() = runBlocking {
        val repository = open()
        repository.add("Notre mariage", LocalDate(2015, 6, 20))
        val text = repository.encode()
        assertTrue("\"format\": \"timeflies-personal-events\"" in text, text)
        assertFalse("\"id\"" in text, "no id in the export: $text")
        val decoded = assertNotNull(repository.decode(text))
        assertEquals(listOf("Notre mariage" to LocalDate(2015, 6, 20)), decoded.events)
        assertEquals(0, decoded.unreadable)
    }

    @Test
    fun decodeRefusesOtherFilesAndCountsBadDates() {
        assertNull(decodeExport("not json"))
        assertNull(decodeExport("""{"format":"something-else","version":1,"events":[]}"""))
        assertNull(decodeExport("""{"format":"timeflies-personal-events","version":99,"events":[]}"""), "a newer version is refused")
        val decoded = assertNotNull(
            decodeExport("""{"format":"timeflies-personal-events","version":1,"events":[{"title":"A","date":"2018-02-31"},{"title":"B","date":"2018-02-28"}]}"""),
        )
        assertEquals(listOf("B" to LocalDate(2018, 2, 28)), decoded.events)
        assertEquals(1, decoded.unreadable)
    }

    @Test
    fun anUnreadableFileGivesAnEmptyList() = runBlocking {
        FileSystem.SYSTEM.createDirectories(file.parent!!)
        FileSystem.SYSTEM.write(file) { writeUtf8("{ broken") }
        assertTrue(open().events.first().isEmpty())
    }
}
