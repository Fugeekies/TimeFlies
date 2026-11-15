package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DataStoreSettingsRepositoryTest {

    private val folder: Path = Files.createTempDirectory("timeflies-settings").toOkioPath()
    private val file = folder / "TimeFlies" / "settings.json"
    private val scopes = mutableListOf<CoroutineScope>()

    /** A new repository on [file], as after a restart of the app (the previous one is closed first). */
    private fun open(): DataStoreSettingsRepository {
        scopes.forEach { it.cancel() }
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob()).also { scopes += it }
        return DataStoreSettingsRepository(FileSystem.SYSTEM, file, scope)
    }

    @AfterTest
    fun cleanUp() {
        scopes.forEach { it.cancel() }
        FileSystem.SYSTEM.deleteRecursively(folder)
    }

    @Test
    fun givesTheDefaultsWithoutAFile() = runBlocking {
        assertEquals(Settings(), open().settings.first())
    }

    @Test
    fun keepsTheSettingsAfterARestart() = runBlocking {
        open().update { it.copy(birth = YearMonth(1991, Month.MAY), youngAge = 20) }
        assertTrue(FileSystem.SYSTEM.exists(file), "the parent folder is created")
        val reread = open().settings.first()
        assertEquals(YearMonth(1991, Month.MAY), reread.birth)
        assertEquals(20, reread.youngAge)
    }

    @Test
    fun writesReadableJson() = runBlocking {
        open().update { it.copy(birth = YearMonth(1991, Month.MAY)) }
        val text = FileSystem.SYSTEM.read(file) { readUtf8() }
        assertTrue("\"birthYear\": 1991" in text, text)
        assertTrue("\"birthMonth\": 5" in text, text)
        assertTrue("\"version\": 1" in text, text)
    }

    @Test
    fun replacesAnUnreadableFileWithTheDefaults() = runBlocking {
        FileSystem.SYSTEM.createDirectories(file.parent!!)
        FileSystem.SYSTEM.write(file) { writeUtf8("{ not json") }
        assertEquals(Settings(), open().settings.first())
    }

    @Test
    fun readsAnOlderOrHandEditedFile() = runBlocking {
        FileSystem.SYSTEM.createDirectories(file.parent!!)
        // Missing fields take their default, unknown ones are ignored, out-of-range values are made safe.
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"birthYear": 1980, "birthMonth": 13, "youngAge": 99, "region": "france", "future": true}""") }
        val settings = open().settings.first()
        assertNull(settings.birth)
        assertEquals(60, settings.youngAge)
        assertEquals("FR", settings.region.code)
    }
}
