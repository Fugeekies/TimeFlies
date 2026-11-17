package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import io.github.fugeekies.timeflies.domain.model.AppRelease
import io.github.fugeekies.timeflies.domain.model.ReleaseCheck
import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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

    /**
     * A new repository on [file], as after a restart of the app. The previous one is closed first and fully
     * stopped: DataStore releases the file only once its scope has completed, and refuses two active stores
     * on the same file (seen on the Linux CI runner).
     */
    private suspend fun open(): DataStoreSettingsRepository {
        closeAll()
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob()).also { scopes += it }
        return DataStoreSettingsRepository(FileSystem.SYSTEM, file, scope)
    }

    private suspend fun closeAll() {
        scopes.forEach { it.coroutineContext.job.cancelAndJoin() }
        scopes.clear()
    }

    @AfterTest
    fun cleanUp() {
        runBlocking { closeAll() }
        FileSystem.SYSTEM.deleteRecursively(folder)
    }

    @Test
    fun givesTheDefaultsWithoutAFile() = runBlocking {
        assertEquals(Settings(), open().settings.first())
    }

    @Test
    fun keepsTheSettingsAfterARestart() = runBlocking {
        open().update { it.copy(birth = YearMonth(1991, Month.MAY), otherAge = 66, onboardingDone = true, equivalencesReversed = true, equivalenceFamilies = setOf(Family.MUSIC)) }
        assertTrue(FileSystem.SYSTEM.exists(file), "the parent folder is created")
        val reread = open().settings.first()
        assertEquals(YearMonth(1991, Month.MAY), reread.birth)
        assertEquals(66, reread.otherAge)
        assertTrue(reread.equivalencesReversed)
        assertEquals(setOf(Family.MUSIC), reread.equivalenceFamilies)
        assertTrue(reread.onboardingDone)
    }

    @Test
    fun writesReadableJson() = runBlocking {
        open().update { it.copy(birth = YearMonth(1991, Month.MAY)) }
        val text = FileSystem.SYSTEM.read(file) { readUtf8() }
        assertTrue("\"birthYear\": 1991" in text, text)
        assertTrue("\"birthMonth\": 5" in text, text)
        assertTrue("\"version\": 6" in text, text)
        assertTrue("youngAge" !in text, "the version 1 field is no longer written")
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
        assertEquals(99, settings.otherAge)
        assertEquals(Family.entries.toSet(), settings.equivalenceFamilies)
        assertEquals("FR", settings.region.code)
        assertEquals(WindowsSettings(), settings.windows, "a file without the Windows panel gets its defaults")
    }

    @Test
    fun keepsTheDisplayStyle() = runBlocking {
        open().update { it.copy(displayStyle = DisplayStyle.VHS) }
        assertTrue("\"style\": \"vhs\"" in FileSystem.SYSTEM.read(file) { readUtf8() })
        assertEquals(DisplayStyle.VHS, open().settings.first().displayStyle)
        open().update { it.copy(displayStyle = DisplayStyle.EPHEMERIS) }
        assertTrue("\"style\": \"ephemeride\"" in FileSystem.SYSTEM.read(file) { readUtf8() })
        assertEquals(DisplayStyle.EPHEMERIS, open().settings.first().displayStyle)
        // A version 5 file (before the choice) and an unknown style both get the Polaroïd.
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 5, "onboardingDone": true}""") }
        assertEquals(DisplayStyle.POLAROID, open().settings.first().displayStyle)
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 6, "style": "betamax"}""") }
        assertEquals(DisplayStyle.POLAROID, open().settings.first().displayStyle)
    }

    @Test
    fun keepsTheEventFilter() = runBlocking {
        val excluded = setOf("music/rap-album-hit/us", "music/music-icon-death/us/rap")
        open().update { it.copy(excludedEventGroups = excluded) }
        assertEquals(excluded, open().settings.first().excludedEventGroups)
        // A version 4 file (before the filter) shows everything.
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 4, "onboardingDone": true}""") }
        assertEquals(emptySet(), open().settings.first().excludedEventGroups)
    }

    @Test
    fun keepsTheCheckForNewVersions() = runBlocking {
        val check = ReleaseCheck(
            enabled = false,
            lastCheck = LocalDate(2026, 11, 16),
            latest = AppRelease("0.2.0", "https://github.com/Fugeekies/TimeFlies/releases/tag/v0.2.0"),
            dismissedVersion = "0.2.0",
        )
        open().update { it.copy(releaseCheck = check) }
        assertEquals(check, open().settings.first().releaseCheck)
        // A version 3 file (before the check) gets it turned on, never asked yet.
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 3, "onboardingDone": true}""") }
        assertEquals(ReleaseCheck(), open().settings.first().releaseCheck)
    }

    @Test
    fun keepsTheWindowsPanel() = runBlocking {
        val windows = WindowsSettings(
            launchAtStartup = false,
            morningNotification = false,
            notificationTime = LocalTime(7, 45),
            keepInTray = false,
            trayHintShown = true,
            lastNotification = LocalDate(2026, 11, 16),
        )
        open().update { it.copy(windows = windows) }
        val text = FileSystem.SYSTEM.read(file) { readUtf8() }
        assertTrue("\"notificationTime\": \"07:45\"" in text, text)
        assertTrue("\"lastNotificationDate\": \"2026-11-16\"" in text, text)
        assertEquals(windows, open().settings.first().windows)
    }

    @Test
    fun givesTheDefaultTimeForAnUnreadableOne() = runBlocking {
        FileSystem.SYSTEM.createDirectories(file.parent!!)
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 3, "windows": {"notificationTime": "9h10", "lastNotificationDate": "hier"}}""") }
        val windows = open().settings.first().windows
        assertEquals(LocalTime(9, 0), windows.notificationTime)
        assertNull(windows.lastNotification)
    }
}
