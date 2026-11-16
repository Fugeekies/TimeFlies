package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Test-only data set: made-up titles covering a regional date, a 29 February, two families and a duplicate id.
class JsonEventRepositoryTest {

    private fun entry(id: String, date: String, title: String, kind: String = "film-release", importance: Int = 2, extra: String = "") =
        """{"id":"$id","date":"$date",$extra"title":{"fr":"$title"},"kind":"$kind","importance":$importance,"source":"https://example.org/$id"}"""

    private val files = mapOf(
        "cinema/french-comedy-hit/fr" to listOf(entry("film-a", "2011-09-23", "Film A", importance = 3, extra = """"regionalDates":{"FR":"2011-11-02"},""")),
        "cinema/animated-feature/us" to listOf(
            entry("film-b", "1994-11-09", "Le Roi Écureuil", importance = 3),
            entry("film-c", "2013-12-04", "La Reine des glaçons"),
        ),
        "cinema/blockbuster/us" to listOf(
            entry("film-d", "2000-02-29", "Film du 29 février", importance = 1),
            entry("film-e", "1997-11-02", "Film E"),
        ),
        "music/music-icon-death/us/rock" to listOf(
            entry("film-a", "1977-08-16", "Doublon", kind = "death", importance = 3),
            entry("music-a", "2017-12-05", "Chanteur A", kind = "death", importance = 3),
        ),
    ).mapKeys { (path, _) -> "files/events/$path.json" }.mapValues { (_, entries) -> entries.joinToString(",", "[", "]") } +
        ("files/events/index.txt" to "cinema/french-comedy-hit/fr\ncinema/animated-feature/us\ncinema/blockbuster/us\nmusic/music-icon-death/us/rock\n")

    private val readPaths = mutableListOf<String>()
    private var excluded = emptySet<String>()

    private fun repository() = JsonEventRepository(
        readFile = { path -> readPaths += path; files[path]?.encodeToByteArray() },
        clock = Clock { LocalDate(2026, 11, 14) },
        excludedGroups = { excluded },
    )

    private val us = Region.of("US")!!

    @Test
    fun loadsEveryFileOfTheIndex() = runTest {
        val report = repository().load()
        assertEquals(listOf("film-a", "film-b", "film-c", "film-d", "film-e", "music-a"), report.events.map { it.id })
        assertEquals(1 + 4, readPaths.size, "the index, then each file once")
        assertEquals(EventGroup(EventType.MUSIC_ICON_DEATH, "us", "rock"), report.events.last().group)
    }

    @Test
    fun rejectsADuplicateIdAcrossFiles() = runTest {
        val rejection = repository().load().rejections.single()
        assertEquals("music/music-icon-death/us/rock", rejection.file)
        assertEquals("film-a", rejection.id)
    }

    @Test
    fun readsTheFilesOnlyOnce() = runTest {
        val repository = repository()
        repository.count()
        repository.onMonthDay(MonthDay(11, 2), Region.FRANCE)
        repository.search("film", 10)
        assertEquals(1 + 4, readPaths.size)
    }

    @Test
    fun leavesTheUntickedGroupsOutOfEveryQuery() = runTest {
        val repository = repository()
        assertEquals(listOf("film-a", "film-e"), repository.onMonthDay(MonthDay(11, 2), Region.FRANCE).map { it.id })
        excluded = setOf("cinema/blockbuster/us")
        assertEquals(listOf("film-a"), repository.onMonthDay(MonthDay(11, 2), Region.FRANCE).map { it.id })
        assertTrue(repository.ofType(EventType.BLOCKBUSTER, LocalDate(1900, 1, 1), LocalDate(2026, 1, 1), Region.FRANCE).isEmpty())
        assertTrue(repository.search("29 février", 10).isEmpty())
        assertEquals(4, repository.count())
        assertEquals(6, repository.groups().sumOf { it.count }, "the filter tree still lists every group")
        excluded = emptySet()
        assertEquals(6, repository.count(), "ticked again, the group comes back")
    }

    @Test
    fun listsTheGroupsWithTheirCount() = runTest {
        val groups = repository().groups().associate { it.group.path to it.count }
        assertEquals(mapOf("cinema/animated-feature/us" to 2, "cinema/blockbuster/us" to 2, "cinema/french-comedy-hit/fr" to 1, "music/music-icon-death/us/rock" to 1), groups)
    }

    @Test
    fun givesAnEmptyBaseWithoutIndex() = runTest {
        val repository = JsonEventRepository(readFile = { null }, clock = Clock { LocalDate(2026, 11, 14) })
        assertEquals(0, repository.count())
    }

    @Test
    fun findsAnniversariesWithTheRegionalDate() = runTest {
        val repository = repository()
        assertEquals(listOf("film-a", "film-e"), repository.onMonthDay(MonthDay(11, 2), Region.FRANCE).map { it.id })
        assertEquals(listOf("film-e"), repository.onMonthDay(MonthDay(11, 2), us).map { it.id })
        assertEquals(listOf("film-a"), repository.onMonthDay(MonthDay(9, 23), us).map { it.id })
    }

    @Test
    fun indexesAFebruary29OnItsOwnDay() = runTest {
        val repository = repository()
        assertEquals(listOf("film-d"), repository.onMonthDay(MonthDay(2, 29), Region.FRANCE).map { it.id })
        assertTrue(repository.onMonthDay(MonthDay(2, 28), Region.FRANCE).isEmpty())
    }

    @Test
    fun findsATypeInADateRangeSortedByDate() = runTest {
        val repository = repository()
        val all = repository.ofType(EventType.ANIMATED_FEATURE, LocalDate(1990, 1, 1), LocalDate(2020, 1, 1), Region.FRANCE)
        assertEquals(listOf("film-b", "film-c"), all.map { it.id })
        val bounds = repository.ofType(EventType.ANIMATED_FEATURE, LocalDate(1994, 11, 9), LocalDate(2013, 12, 3), Region.FRANCE)
        assertEquals(listOf("film-b"), bounds.map { it.id })
        assertTrue(repository.ofType(EventType.SITCOM_HIT, LocalDate(1900, 1, 1), LocalDate(2026, 1, 1), Region.FRANCE).isEmpty())
        assertTrue(repository.ofType(EventType.ANIMATED_FEATURE, LocalDate(2020, 1, 1), LocalDate(1990, 1, 1), Region.FRANCE).isEmpty())
    }

    @Test
    fun searchesTitlesIgnoringCaseAndAccents() = runTest {
        val repository = repository()
        assertEquals(listOf("film-b"), repository.search("ecureuil", 10).map { it.id })
        assertEquals(listOf("film-c"), repository.search("GLAÇONS", 10).map { it.id })
        assertEquals(2, repository.search("film", 2).size)
        assertTrue(repository.search("   ", 10).isEmpty())
    }

    @Test
    fun countsOnlyValidEvents() = runTest {
        assertEquals(6, repository().count())
    }

    @Test
    fun reportsCoverage() = runTest {
        val report = CoverageReport.of(repository().load().events, Region.FRANCE)
        assertEquals(6, report.eventCount)
        assertEquals(5, report.eventsByFamily[Family.CINEMA])
        // 6 events on 5 distinct days (two share 2 November).
        assertEquals(366 - 5, report.daysWithoutAnniversary)
        assertEquals(2, report.typesBelowMinimum[EventType.ANIMATED_FEATURE])
    }
}
