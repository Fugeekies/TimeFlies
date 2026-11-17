package io.github.fugeekies.timeflies.benchmarks

import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Loading of the event base and computations behind each page, on the real files and on made-up sets
 * up to 10 000 events. Writes a Markdown report (path in the `timeflies.benchmarkReport` property).
 * Informative only: no threshold fails the build.
 */
class PerformanceBenchmarks {

    private val today = LocalDate(2026, 11, 14)
    private val clock = Clock { today }
    private val region = Region.FRANCE

    private fun repository(files: Map<String, ByteArray>) =
        JsonEventRepository(readFile = { path -> files[path] }, clock = clock)

    /** The real base of assets/events, with the index the build writes. */
    private fun realFiles(): Map<String, ByteArray> {
        val dir = File(System.getProperty("timeflies.eventsDir") ?: error("timeflies.eventsDir is not set"))
        val paths = dir.walkTopDown().filter { it.isFile && it.extension == "json" }
            .map { it.relativeTo(dir).invariantSeparatorsPath.removeSuffix(".json") }.sorted().toList()
        return paths.associate { "files/events/$it.json" to File(dir, "$it.json").readBytes() } +
            ("files/events/index.txt" to paths.joinToString("\n").encodeToByteArray())
    }

    private fun syntheticFiles(count: Int): Map<String, ByteArray> =
        SyntheticEvents.files(count)

    private class Section(val title: String, val details: String, val rows: List<Measurement>)

    @Test
    fun benchmarks() {
        val sections = mutableListOf<Section>()
        val sets = listOf("Fichiers réels" to realFiles()) +
            listOf(500, 1_000, 5_000, 10_000).map { "Synthétique, $it événements" to syntheticFiles(it) }

        for ((title, files) in sets) {
            val bytes = files.values.sumOf { it.size }
            val count = runBlocking { repository(files).count() }
            sections += Section(title, "$count événements, ${"%.1f".format(java.util.Locale.ROOT, bytes / 1024.0)} Ko de JSON", measureSet(files))
        }

        val report = report(sections)
        println(report)
        System.getProperty("timeflies.benchmarkReport")?.let { path ->
            File(path).apply { parentFile.mkdirs() }.writeText(report)
        }
        assertTrue(sections.isNotEmpty())
    }

    private fun measureSet(files: Map<String, ByteArray>): List<Measurement> = runBlocking {
        val rows = mutableListOf<Measurement>()

        // Cold start: read, validate and index everything, as the app does on first use.
        rows += measure("Chargement complet (lecture, validation, index)", iterations = 20) {
            runBlocking {
                val repository = repository(files)
                repository.load()
                repository.onMonthDay(MonthDay(1, 1), region)
            }
        }

        val loaded = repository(files).also { it.load(); it.onMonthDay(MonthDay(1, 1), region) }
        val allDays = (1..12).flatMap { m -> (1..31).mapNotNull { d -> runCatching { MonthDay(m, d) }.getOrNull() } }

        rows += measure("Requête par jour × 366 (anniversaires)") {
            runBlocking { allDays.forEach { loaded.onMonthDay(it, region) } }
        }
        rows += measure("Requête par type à ±2 ans × 100 (équivalences)") {
            runBlocking {
                repeat(100) { i ->
                    val type = EventType.entries[i % EventType.entries.size]
                    val center = LocalDate(1960 + i % 60, 6, 15)
                    loaded.ofType(type, center.minus(2, DateTimeUnit.YEAR), center.plus(2, DateTimeUnit.YEAR), region)
                }
            }
        }
        rows += measure("Recherche dans les titres × 20 (Quel âge ?)") {
            runBlocking { listOf("roi", "écran", "test 12", "zzz").forEach { repeat(5) { _ -> loaded.search(it, 20) } } }
        }

        // Pages: the computation behind each screen, data already loaded.
        val getDailyShock = GetDailyShock(loaded, clock)
        rows += measure("Page Aujourd'hui (GetDailyShock)", iterations = 100) {
            runBlocking { getDailyShock() }
        }
        rows += measure("Page Aujourd'hui pour chaque jour d'une année × 365", iterations = 10) {
            runBlocking {
                var day = LocalDate(2026, 1, 1)
                repeat(365) {
                    GetDailyShock(loaded, Clock { day })()
                    day = day.plus(1, DateTimeUnit.DAY)
                }
            }
        }
        // Equivalences: the user born in May 1991 and one other age, 15 (younger) or 66 (older), or three side by side
        // (15, 66 and 8, the most since milestone 16), all themes.
        val getEquivalences = GetEquivalences(loaded, clock)
        val user = Generations.userBirth(YearMonth(1991, Month.MAY))
        val child = Generations.birthFromAge(today, 15)
        val parent = Generations.birthFromAge(today, 66)
        val allFamilies = Family.entries.toSet()
        rows += measure("Page Équivalences, un âge plus jeune (GetEquivalences)", iterations = 100) {
            runBlocking { getEquivalences(user, listOf(child), allFamilies, region) }
        }
        rows += measure("Page Équivalences, un âge plus âgé (GetEquivalences)", iterations = 100) {
            runBlocking { getEquivalences(user, listOf(parent), allFamilies, region) }
        }
        val toddler = Generations.birthFromAge(today, 8)
        rows += measure("Page Équivalences, trois âges côte à côte (GetEquivalences)", iterations = 100) {
            runBlocking { getEquivalences(user, listOf(child, parent, toddler), allFamilies, region) }
        }
        // "Quel âge ?": the list by family when the screen opens, and a search.
        val searchEvents = SearchEvents(loaded, clock)
        rows += measure("Page Quel âge ?, liste par famille (SearchEvents)", iterations = 50) {
            runBlocking { searchEvents.byFamily(region) }
        }
        rows += measure("Page Quel âge ?, recherche × 4 (SearchEvents)", iterations = 50) {
            runBlocking { listOf("roi", "écran", "test 12", "zzz").forEach { searchEvents.search(it, region) } }
        }
        rows
    }

    private fun report(sections: List<Section>): String = buildString {
        appendLine("# Rapport de performance TimeFlies")
        appendLine()
        appendLine("- Date : ${ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}")
        appendLine("- JVM : ${System.getProperty("java.vm.name")} ${System.getProperty("java.version")}, ${Runtime.getRuntime().availableProcessors()} processeurs, ${System.getProperty("os.name")}")
        appendLine("- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.")
        appendLine("- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.")
        for (section in sections) {
            appendLine()
            appendLine("## ${section.title}")
            appendLine()
            appendLine(section.details)
            appendLine()
            appendLine("| Mesure | Médiane | P95 | Min |")
            appendLine("|---|---:|---:|---:|")
            section.rows.forEach { appendLine(it.row()) }
        }
    }
}
