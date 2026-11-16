package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.MonthDay
import io.github.fugeekies.timeflies.domain.model.Region
import io.github.fugeekies.timeflies.domain.port.Clock
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class JsonEventRepositoryThreadTest {

    @Test
    fun neverReadsTheFilesOnTheCallersThread() = runBlocking {
        // A single thread standing for the interface thread, which asks for the first screen.
        val ui = Executors.newSingleThreadExecutor { Thread(it, "interface") }.asCoroutineDispatcher()
        val readers = mutableListOf<String>()
        val repository = JsonEventRepository(
            readFile = { readers += Thread.currentThread().name; null },
            clock = Clock { LocalDate(2026, 11, 16) },
        )
        try {
            withContext(ui) { repository.onMonthDay(MonthDay(11, 16), Region.FRANCE) }
        } finally {
            ui.close()
        }
        assertTrue(readers.isNotEmpty(), "the files were read")
        readers.forEach { assertNotEquals("interface", it) }
    }
}
