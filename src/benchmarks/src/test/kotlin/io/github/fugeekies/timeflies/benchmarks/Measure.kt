package io.github.fugeekies.timeflies.benchmarks

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

/** Timing of one measured operation: [iterations] runs after [warmup] unmeasured ones. */
data class Measurement(val name: String, val iterations: Int, val median: Duration, val p95: Duration, val min: Duration) {
    fun row(): String = "| $name | ${median.ms()} | ${p95.ms()} | ${min.ms()} |"

    private fun Duration.ms(): String = "%.3f".format(java.util.Locale.ROOT, toDouble(DurationUnit.MILLISECONDS))
}

/** Runs [block] [warmup] times without measuring (JIT), then [iterations] times, and keeps median, p95 and minimum. */
inline fun measure(name: String, warmup: Int = 5, iterations: Int = 30, block: () -> Unit): Measurement {
    repeat(warmup) { block() }
    val samples = LongArray(iterations)
    for (i in 0 until iterations) {
        val start = System.nanoTime()
        block()
        samples[i] = System.nanoTime() - start
    }
    samples.sort()
    val p95Index = ((iterations - 1) * 0.95).toInt()
    return Measurement(name, iterations, samples[iterations / 2].nanoseconds, samples[p95Index].nanoseconds, samples[0].nanoseconds)
}
