package io.github.fugeekies.timeflies.data.settings

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.AppRelease
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.ReleaseCheck
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
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
        open().update { it.copy(birth = YearMonth(1991, Month.MAY), onboardingDone = true, equivalenceFamilies = setOf(Family.MUSIC)) }
        assertTrue(FileSystem.SYSTEM.exists(file), "the parent folder is created")
        val reread = open().settings.first()
        assertEquals(YearMonth(1991, Month.MAY), reread.birth)
        assertEquals(setOf(Family.MUSIC), reread.equivalenceFamilies)
        assertTrue(reread.onboardingDone)
    }

    @Test
    fun writesReadableJson() = runBlocking {
        open().update { it.copy(birth = YearMonth(1991, Month.MAY)) }
        val text = FileSystem.SYSTEM.read(file) { readUtf8() }
        assertTrue("\"birthYear\": 1991" in text, text)
        assertTrue("\"birthMonth\": 5" in text, text)
        assertTrue("\"version\": 9" in text, text)
        // Version 7: the other ages are forgotten when the app closes, nothing of them is written.
        assertTrue("otherAge" !in text && "youngAge" !in text && "equivalencesReversed" !in text, text)
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
        // Missing fields take their default, unknown or dropped ones (otherAge, version 7) are ignored, out-of-range
        // values are made safe.
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 6, "birthYear": 1980, "birthMonth": 13, "otherAge": 99, "equivalencesReversed": true, "region": "france", "future": true}""") }
        val settings = open().settings.first()
        assertNull(settings.birth)
        assertEquals(Family.THEMES.toSet(), settings.equivalenceFamilies)
        assertEquals("FR", settings.region.code)
        assertEquals(WindowsSettings(), settings.windows, "a file without the Windows panel gets its defaults")
    }

    @Test
    fun keepsTheLanguage() = runBlocking {
        assertNull(open().settings.first().language, "the system language without a choice")
        open().update { it.copy(language = AppLanguage.ITALIAN) }
        assertTrue("\"language\": \"it\"" in FileSystem.SYSTEM.read(file) { readUtf8() })
        assertEquals(AppLanguage.ITALIAN, open().settings.first().language)
        // A language the interface does not have (a file edited by hand) falls back to the system language.
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 8, "language": "pt"}""") }
        assertNull(open().settings.first().language)
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
            launchNotification = false,
            keepInTray = false,
            trayHintShown = true,
            lastNotification = LocalDate(2026, 11, 16),
        )
        open().update { it.copy(windows = windows) }
        val text = FileSystem.SYSTEM.read(file) { readUtf8() }
        // The field keeps its former name, so the choice of an older file stays (version 9).
        assertTrue("\"morningNotification\": false" in text, text)
        assertTrue("notificationTime" !in text, text)
        assertTrue("\"lastNotificationDate\": \"2026-11-16\"" in text, text)
        assertEquals(windows, open().settings.first().windows)
    }

    @Test
    fun readsTheWindowsPanelOfAnOlderFile() = runBlocking {
        FileSystem.SYSTEM.createDirectories(file.parent!!)
        // Version 8: the time of the notification is ignored, its choice stays; an unreadable date is dropped.
        FileSystem.SYSTEM.write(file) { writeUtf8("""{"version": 8, "windows": {"morningNotification": false, "notificationTime": "07:45", "lastNotificationDate": "hier"}}""") }
        val windows = open().settings.first().windows
        assertEquals(false, windows.launchNotification)
        assertNull(windows.lastNotification)
    }
}
