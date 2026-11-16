package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Clock as SystemClock

/** Validates the event files shipped with the app: any rejected entry fails the build. */
class RealEventFilesTest {

    private val eventsDir = File(
        System.getProperty("timeflies.eventsDir") ?: fail("system property timeflies.eventsDir is not set"),
    )

    @Test
    fun everyShippedEntryIsValid() = runTest {
        assertTrue(eventsDir.isDirectory, "missing folder $eventsDir")
        // The index the build writes: every JSON file of assets/events, by its path.
        val index = eventsDir.walkTopDown().filter { it.isFile && it.extension == "json" }
            .joinToString("\n") { it.relativeTo(eventsDir).invariantSeparatorsPath.removeSuffix(".json") }
        val repository = JsonEventRepository(
            readFile = { path ->
                val relative = path.removePrefix("files/events/")
                if (relative == JsonEventRepository.INDEX) index.encodeToByteArray() else File(eventsDir, relative).takeIf { it.isFile }?.readBytes()
            },
            clock = Clock { SystemClock.System.todayIn(TimeZone.currentSystemDefault()) },
        )
        val report = repository.load()
        println(CoverageReport.of(report.events, Region.FRANCE))
        if (report.rejections.isNotEmpty()) {
            fail("Entrées rejetées :\n" + report.rejections.joinToString("\n"))
        }
        assertTrue(report.events.isNotEmpty(), "no event file found in $eventsDir")
    }
}
