package io.github.fugeekies.timeflies.data.events

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

// Test-only data set: made-up titles covering a regional date, a 29 February and two families.
class JsonEventRepositoryTest {

    private val cinema = """
        [
          {"id":"film-a","date":"2011-09-23","regionalDates":{"FR":"2011-11-02"},"title":{"fr":"Film A"},
           "kind":"film-release","type":"french-comedy-hit","importance":3,"source":"https://example.org/a"},
          {"id":"film-b","date":"1994-11-09","title":{"fr":"Le Roi Écureuil"},
           "kind":"film-release","type":"animated-feature","importance":3,"source":"https://example.org/b"},
          {"id":"film-c","date":"2013-12-04","title":{"fr":"La Reine des glaçons"},
           "kind":"film-release","type":"animated-feature","importance":2,"source":"https://example.org/c"},
          {"id":"film-d","date":"2000-02-29","title":{"fr":"Film du 29 février"},
           "kind":"film-release","type":"blockbuster","importance":1,"source":"https://example.org/d"},
          {"id":"film-e","date":"1997-11-02","title":{"fr":"Film E"},
           "kind":"film-release","type":"blockbuster","importance":2,"source":"https://example.org/e"}
        ]
    """.trimIndent()

    private val music = """
        [
          {"id":"film-a","date":"1977-08-16","title":{"fr":"Doublon"},
           "kind":"death","type":"music-icon-death","importance":3,"source":"https://example.org/x"},
          {"id":"music-a","date":"2017-12-05","title":{"fr":"Chanteur A"},
           "kind":"death","type":"music-icon-death","importance":3,"source":"https://example.org/m"}
        ]
    """.trimIndent()

    private val files = mapOf(
        "files/events/cinema.json" to cinema,
        "files/events/music.json" to music,
    )

    private val readPaths = mutableListOf<String>()

    private fun repository() = JsonEventRepository(
        readFile = { path -> readPaths += path; files[path]?.encodeToByteArray() },
        clock = Clock { LocalDate(2026, 11, 14) },
    )

    private val us = Region.of("US")!!

    @Test
    fun loadsEveryFamilyFileAndSkipsMissingOnes() = runTest {
        val report = repository().load()
        assertEquals(listOf("film-a", "film-b", "film-c", "film-d", "film-e", "music-a"), report.events.map { it.id })
        assertEquals(Family.entries.size, readPaths.size)
    }

    @Test
    fun rejectsADuplicateIdAcrossFiles() = runTest {
        val rejection = repository().load().rejections.single()
        assertEquals(Family.MUSIC, rejection.family)
        assertEquals("film-a", rejection.id)
    }

    @Test
    fun readsTheFilesOnlyOnce() = runTest {
        val repository = repository()
        repository.count()
        repository.onMonthDay(MonthDay(11, 2), Region.FRANCE)
        repository.search("film", 10)
        assertEquals(Family.entries.size, readPaths.size)
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
