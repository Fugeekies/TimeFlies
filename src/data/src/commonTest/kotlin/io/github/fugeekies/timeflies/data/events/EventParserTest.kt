package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.Region
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Test-only entries: titles are made up on purpose, they are never shipped.
class EventParserTest {

    private val parser = EventParser(today = LocalDate(2026, 11, 14))

    private fun parse(json: String, file: String = "cinema/french-comedy-hit/fr") = parser.parse(file, json.encodeToByteArray())

    private fun reasonsOf(entry: String, file: String = "cinema/french-comedy-hit/fr"): List<String> {
        val result = parse("[$entry]", file)
        assertEquals(0, result.events.size, "the entry should be rejected")
        return result.rejections.single().reasons
    }

    private val valid = """
        {"id":"film-test-a","date":"2011-09-23","regionalDates":{"FR":"2011-11-02"},
         "title":{"fr":"Film test A","en":"Test movie A"},"kind":"film-release","type":"french-comedy-hit",
         "importance":3,"source":"https://example.org/a","note":"ignored field"}
    """.trimIndent()

    @Test
    fun readsAValidEntry() {
        val event = parse("[$valid]").events.single()
        assertEquals("film-test-a", event.id)
        assertEquals(LocalDate(2011, 9, 23), event.date)
        assertEquals(LocalDate(2011, 11, 2), event.dateFor(Region.FRANCE))
        assertEquals(LocalDate(2011, 9, 23), event.dateFor(Region.of("US")!!))
        assertEquals("Test movie A", event.title("en"))
        assertEquals("Film test A", event.title("de"))
        assertEquals(EventKind.FILM_RELEASE, event.kind)
        assertEquals(EventType.FRENCH_COMEDY_HIT, event.type)
        assertEquals(3, event.importance)
    }

    @Test
    fun keepsAFebruary29() {
        val entry = valid.replace("2011-09-23", "2000-02-29").replace(""""regionalDates":{"FR":"2011-11-02"},""", "")
        assertEquals(LocalDate(2000, 2, 29), parse("[$entry]").events.single().date)
    }

    @Test
    fun rejectsABadId() {
        assertTrue(reasonsOf(valid.replace("film-test-a", "Film Test A")).any { it.startsWith("id") })
    }

    @Test
    fun rejectsAMissingOrBadDate() {
        assertTrue(reasonsOf(valid.replace(""""date":"2011-09-23",""", "")).contains("date manquante"))
        assertTrue(reasonsOf(valid.replace("2011-09-23", "23/09/2011")).any { "format AAAA-MM-JJ" in it })
        assertTrue(reasonsOf(valid.replace("2011-09-23", "2011-02-30")).any { "date inexistante" in it })
        assertTrue(reasonsOf(valid.replace("2011-09-23", "2023-02-29")).any { "date inexistante" in it })
    }

    @Test
    fun rejectsADateThatIsNotInThePast() {
        assertTrue(reasonsOf(valid.replace("2011-09-23", "2026-11-14")).any { "pas dans le passé" in it })
        assertTrue(reasonsOf(valid.replace("2011-11-02", "2030-01-01")).any { "regionalDates.FR" in it })
    }

    @Test
    fun rejectsABadRegionCode() {
        assertTrue(reasonsOf(valid.replace(""""FR":""", """"fr":""")).any { "code pays" in it })
    }

    @Test
    fun rejectsATitleInALanguageTheInterfaceDoesNotHave() {
        assertTrue(reasonsOf(valid.replace(""""en":"Test movie A"""", """"en-US":"Test movie A"""")).any { "langue inconnue en-US" in it })
    }

    @Test
    fun rejectsAMissingFrenchTitle() {
        assertTrue(reasonsOf(valid.replace(""""fr":"Film test A",""", "")).contains("title.fr manquant ou vide"))
    }

    @Test
    fun takesTheGroupFromTheFolder() {
        val withoutType = valid.replace(""""type":"french-comedy-hit",""", "")
        val event = parse("[$withoutType]").events.single()
        assertEquals(EventType.FRENCH_COMEDY_HIT, event.type)
        assertEquals("fr", event.group.origin)
        val death = parse("[${withoutType.replace("film-release", "death")}]", "music/music-icon-death/us/rap").events.single()
        assertEquals(EventGroup(EventType.MUSIC_ICON_DEATH, "us", "rap"), death.group)
    }

    @Test
    fun rejectsAnUnknownKindOrATypeThatDisagreesWithItsFolder() {
        assertTrue(reasonsOf(valid.replace("film-release", "movie")).contains("kind « movie » inconnu"))
        assertTrue(reasonsOf(valid.replace("french-comedy-hit", "blockbuster")).any { "rangé dans le dossier « french-comedy-hit »" in it })
    }

    @Test
    fun rejectsAFileOutsideTheTree() {
        val result = parse("[$valid]", "music/french-comedy-hit/fr")
        assertEquals(0, result.events.size)
        assertTrue(result.rejections.single().reasons.any { "rangé dans la famille « music »" in it })
        assertEquals(-1, parse("[$valid]", "cinema/french-comedy-hit").rejections.single().index, "origin missing")
    }

    @Test
    fun rejectsABadImportanceOrSource() {
        assertTrue(reasonsOf(valid.replace(""""importance":3""", """"importance":4""")).any { "importance 4" in it })
        assertTrue(reasonsOf(valid.replace("https://example.org/a", "http://example.org/a")).any { "source" in it })
    }

    @Test
    fun reportsEveryProblemOfAnEntry() {
        val reasons = reasonsOf("""{"id":"X","title":{}}""")
        assertTrue(reasons.size >= 6, "got $reasons")
    }

    @Test
    fun aBadEntryDoesNotStopTheOthers() {
        val result = parse("""[$valid, {"id":12}, ${valid.replace("film-test-a", "film-test-b")}]""")
        assertEquals(listOf("film-test-a", "film-test-b"), result.events.map { it.id })
        assertEquals(1, result.rejections.single().index)
    }

    @Test
    fun rejectsAFileThatIsNotAJsonArray() {
        val result = parse("""{"id":"film-test-a"}""")
        assertEquals(0, result.events.size)
        assertEquals(-1, result.rejections.single().index)
    }
}
