package io.github.fugeekies.timeflies.benchmarks

import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.EventType
import kotlinx.datetime.LocalDate
import kotlin.random.Random

/**
 * Made-up events used only to measure how loading scales towards 5 000 events and beyond
 * (docs/ARCHITECTURE.md, "Base d'événements"). Never shipped.
 */
object SyntheticEvents {
    private val firstDay = LocalDate(1950, 1, 1).toEpochDays()
    private val lastDay = LocalDate(2025, 12, 31).toEpochDays()
    private val origins = listOf("fr", "us", "gb")

    /**
     * [count] events in all, spread over every type, three origins and every day, laid out as the real base: one file
     * per group and `index.txt`, keyed by their resource path. Same seed, same files.
     */
    fun files(count: Int, seed: Int = 42): Map<String, ByteArray> {
        val random = Random(seed)
        val types = EventType.entries
        val byGroup = sortedMapOf<String, StringBuilder>()
        for (i in 0 until count) {
            val type = types[i % types.size]
            val group = EventGroup(type, origins[(i / types.size) % origins.size]).path
            val date = LocalDate.fromEpochDays(random.nextLong(firstDay, lastDay + 1))
            val builder = byGroup.getOrPut(group) { StringBuilder("[") }
            if (builder.length > 1) builder.append(',')
            builder.append(
                """{"id":"bench-$i","date":"$date","title":{"fr":"Événement de test numéro $i"},""" +
                    """"kind":"${EventKind.HISTORY.id}","importance":${1 + random.nextInt(3)},""" +
                    """"source":"https://example.org/bench/$i"}""",
            )
        }
        val files = byGroup.map { (group, builder) -> "files/events/$group.json" to builder.append(']').toString().encodeToByteArray() }.toMap()
        return files + ("files/events/index.txt" to byGroup.keys.joinToString("\n").encodeToByteArray())
    }
}
