package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.EventRepository
import kotlin.concurrent.Volatile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/** Outcome of loading the event files, for logs and the debug panel. */
data class LoadReport(val events: List<Event>, val rejections: List<Rejection>)

/**
 * [EventRepository] backed by the JSON files shipped with the app, one per [Family]
 * (docs/ARCHITECTURE.md, "Base d'événements"). Files are read once, on first use, and kept in memory.
 *
 * @param readFile reads a file of the app resources by its path, or returns null when it does not exist.
 *   Supplied by the app, which keeps this layer independent of Compose.
 * @param onRejection called once per rejected entry, for the logs.
 * @param computation where the files are parsed, the indexes built and the titles searched: never the thread of
 *   the interface, which the first screen would otherwise freeze while the base loads (docs/ARCHITECTURE.md).
 */
class JsonEventRepository(
    private val readFile: suspend (path: String) -> ByteArray?,
    private val clock: Clock,
    private val basePath: String = "files/events/",
    private val onRejection: (Rejection) -> Unit = {},
    private val computation: CoroutineDispatcher = Dispatchers.Default,
) : EventRepository {

    private val mutex = Mutex()

    @Volatile
    private var loaded: LoadReport? = null

    /** Built indexes, replaced as a whole under [mutex] so it can be read without it. */
    @Volatile
    private var indexes: Map<Region, EventIndex> = emptyMap()

    /** Loads the files if needed and returns what was kept and left out. */
    suspend fun load(): LoadReport = loaded ?: withContext(computation) { mutex.withLock { loadLocked() } }

    override suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event> =
        index(region).onMonthDay(monthDay)

    override suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region): List<Event> =
        index(region).ofType(type, from, to)

    override suspend fun search(query: String, limit: Int): List<Event> {
        val needle = fold(query.trim())
        if (needle.isEmpty() || limit <= 0) return emptyList()
        val events = load().events
        return withContext(computation) {
            events.asSequence()
                .filter { event -> event.titles.values.any { fold(it).contains(needle) } }
                .take(limit)
                .toList()
        }
    }

    override suspend fun count(): Int = load().events.size

    /**
     * The index of [region]. Once built, it is returned at once on the caller's thread: a query costs microseconds,
     * a switch of thread would cost more than the query (benchmarks of 16/11/2026).
     */
    private suspend fun index(region: Region): EventIndex = indexes[region] ?: withContext(computation) {
        mutex.withLock {
            indexes[region] ?: EventIndex(loadLocked().events, region).also { indexes = indexes + (region to it) }
        }
    }

    private suspend fun loadLocked(): LoadReport {
        loaded?.let { return it }
        val parser = EventParser(clock.today())
        val events = mutableListOf<Event>()
        val rejections = mutableListOf<Rejection>()
        val seenIds = HashSet<String>()
        for (family in Family.entries) {
            // A family without a file yet is simply empty: files arrive theme by theme.
            val bytes = readFile(basePath + family.fileName) ?: continue
            val parsed = parser.parse(family, bytes)
            rejections += parsed.rejections
            for (event in parsed.events) {
                if (seenIds.add(event.id)) {
                    events += event
                } else {
                    rejections += Rejection(family, -1, event.id, listOf("id « ${event.id} » déjà utilisé"))
                }
            }
        }
        rejections.forEach(onRejection)
        return LoadReport(events, rejections).also { loaded = it }
    }
}
