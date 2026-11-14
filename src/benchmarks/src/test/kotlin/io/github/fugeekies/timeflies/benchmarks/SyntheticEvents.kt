package io.github.fugeekies.timeflies.benchmarks

import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import kotlinx.datetime.LocalDate
import kotlin.random.Random

/**
 * Made-up events used only to measure how loading scales towards 5 000 events and beyond
 * (docs/ARCHITECTURE.md, "Base d'événements"). Never shipped.
 */
object SyntheticEvents {
    private val firstDay = LocalDate(1950, 1, 1).toEpochDays()
    private val lastDay = LocalDate(2025, 12, 31).toEpochDays()

    /** One JSON file per family with [count] events in all, spread over every type and every day; same seed, same files. */
    fun files(count: Int, seed: Int = 42): Map<Family, ByteArray> {
        val random = Random(seed)
        val types = EventType.entries
        val byFamily = Family.entries.associateWith { StringBuilder("[") }
        for (i in 0 until count) {
            val type = types[i % types.size]
            val date = LocalDate.fromEpochDays(random.nextLong(firstDay, lastDay + 1))
            val builder = byFamily.getValue(type.family)
            if (builder.length > 1) builder.append(',')
            builder.append(
                """{"id":"bench-$i","date":"$date","title":{"fr":"Événement de test numéro $i"},""" +
                    """"kind":"${EventKind.HISTORY.id}","type":"${type.id}","importance":${1 + random.nextInt(3)},""" +
                    """"source":"https://example.org/bench/$i"}""",
            )
        }
        return byFamily.mapValues { (_, builder) -> builder.append(']').toString().encodeToByteArray() }
    }
}
