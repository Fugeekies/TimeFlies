package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.concurrent.Volatile

/** Outcome of loading the event files, for logs and the debug panel. */
data class LoadReport(val events: List<Event>, val rejections: List<Rejection>)

/**
 * [EventRepository] backed by the JSON files shipped with the app, one per group:
 * `<family>/<type>/<origin>[/<subcategory>].json`, listed in `index.txt` (docs/EVENTS_DATA.md, "Fichiers et
 * stockage"). Files are read once, on first use, and kept in memory; the groups the user unticked are left out of
 * every query.
 *
 * @param readFile reads a file of the app resources by its path, or returns null when it does not exist.
 *   Supplied by the app, which keeps this layer independent of Compose.
 * @param excludedGroups the paths of the unticked groups, read at each query (the settings, in the apps).
 * @param onRejection called once per rejected entry, for the logs.
 * @param computation where the files are parsed, the indexes built and the titles searched: never the thread of
 *   the interface, which the first screen would otherwise freeze while the base loads (docs/ARCHITECTURE.md).
 */
class JsonEventRepository(
    private val readFile: suspend (path: String) -> ByteArray?,
    private val clock: Clock,
    private val basePath: String = "files/events/",
    private val excludedGroups: suspend () -> Set<String> = { emptySet() },
    private val onRejection: (Rejection) -> Unit = {},
    private val computation: CoroutineDispatcher = Dispatchers.Default,
) : EventRepository {

    /** An index is built for a region and a set of unticked groups. */
    private data class IndexKey(val region: Region, val excluded: Set<String>)

    private val mutex = Mutex()

    @Volatile
    private var loaded: LoadReport? = null

    /** The folded titles of [searchableTitles], built on the first search; two searches at once may both build it. */
    @Volatile
    private var searchable: List<Pair<Event, String>>? = null

    /** Built indexes, replaced as a whole under [mutex] so it can be read without it. */
    @Volatile
    private var indexes: Map<IndexKey, EventIndex> = emptyMap()

    /** Loads the files if needed and returns everything kept and left out, unticked groups included. */
    suspend fun load(): LoadReport = loaded ?: withContext(computation) { mutex.withLock { loadLocked() } }

    override suspend fun groups(): List<EventGroupCount> =
        load().events.groupingBy { it.group }.eachCount().map { (group, count) -> EventGroupCount(group, count) }.sortedBy { it.group.path }

    override suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event> =
        index(region).onMonthDay(monthDay)

    override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region): List<Event> =
        index(region).ofType(type, from, to)

    override suspend fun search(query: String, limit: Int): List<Event> {
        val needle = fold(query.trim())
        if (needle.isEmpty() || limit <= 0) return emptyList()
        val excluded = excludedGroups()
        val titles = searchableTitles()
        return withContext(computation) {
            titles.asSequence()
                .filter { (event, folded) -> folded.contains(needle) && event.group.path !in excluded }
                .map { it.first }
                .take(limit)
                .toList()
        }
    }

    override suspend fun count(): Int = shown().size

    /**
     * Every event with all its titles folded once ([fold]) and joined by a character no query holds, so a search folds
     * the query only, not the thousands of titles at each key typed.
     */
    private suspend fun searchableTitles(): List<Pair<Event, String>> = searchable ?: run {
        val events = load().events
        withContext(computation) { events.map { it to it.titles.values.joinToString(TITLE_SEPARATOR) { title -> fold(title) } } }
            .also { searchable = it }
    }

    /** The events of the ticked groups. */
    private suspend fun shown(): List<Event> {
        val excluded = excludedGroups()
        val events = load().events
        return if (excluded.isEmpty()) events else events.filter { it.group.path !in excluded }
    }

    /**
     * The index of [region] without the unticked groups. Once built, it is returned at once on the caller's thread: a
     * query costs microseconds, a switch of thread would cost more than the query (benchmarks of 16/11/2026).
     */
    private suspend fun index(region: Region): EventIndex {
        val key = IndexKey(region, excludedGroups())
        return indexes[key] ?: withContext(computation) {
            mutex.withLock {
                indexes[key] ?: run {
                    val events = loadLocked().events.filter { it.group.path !in key.excluded }
                    // Only the current filter is worth keeping: a former one is not coming back soon.
                    EventIndex(events, region).also { index -> indexes = indexes.filterKeys { it.excluded == key.excluded } + (key to index) }
                }
            }
        }
    }

    private suspend fun loadLocked(): LoadReport {
        loaded?.let { return it }
        val parser = EventParser(clock.today())
        val events = mutableListOf<Event>()
        val rejections = mutableListOf<Rejection>()
        val seenIds = HashSet<String>()
        // No index: no base (a build without the event files).
        val files = readFile(basePath + INDEX)?.decodeToString()?.lines()?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        for (file in files) {
            val bytes = readFile("$basePath$file.json")
            if (bytes == null) {
                rejections += Rejection(file, -1, null, listOf("fichier listé dans $INDEX mais absent"))
                continue
            }
            val parsed = parser.parse(file, bytes)
            rejections += parsed.rejections
            for (event in parsed.events) {
                if (seenIds.add(event.id)) {
                    events += event
                } else {
                    rejections += Rejection(file, -1, event.id, listOf("id « ${event.id} » déjà utilisé"))
                }
            }
        }
        rejections.forEach(onRejection)
        return LoadReport(events, rejections).also { loaded = it }
    }

    companion object {
        /** The list of the files of the base, one path per line without ".json", made at build time. */
        const val INDEX = "index.txt"

        /** Between the folded titles of an event: never typed, so a match never spans two titles. */
        private const val TITLE_SEPARATOR = "\u0000"
    }
}
