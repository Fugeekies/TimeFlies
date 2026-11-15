package io.github.fugeekies.timeflies.desktop

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.platformLogWriter
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Logs of the Windows app (docs/DEVELOPMENT.md, "Logs et plantages"): console, plus one file per day in
 * `%APPDATA%\TimeFlies\logs\`, the last [KEPT_DAYS] days kept. Never any personal data (neither the birth month
 * nor the year). An uncaught exception is written to the logs before the app stops.
 */
object Logging {
    private const val KEPT_DAYS = 7

    fun setUp(debug: Boolean) {
        val folder = File(AppFolder.path().toFile(), "logs")
        Logger.setLogWriters(platformLogWriter(), DailyFileLogWriter(folder, KEPT_DAYS))
        Logger.setMinSeverity(if (debug) Severity.Debug else Severity.Info)
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            Logger.withTag("Crash").a(error) { "Uncaught exception in ${thread.name}" }
            previous?.uncaughtException(thread, error)
        }
    }
}

/** Appends each line to `timeflies-AAAA-MM-JJ.log` in [folder], and removes the files older than [keptDays]. */
private class DailyFileLogWriter(private val folder: File, private val keptDays: Int) : LogWriter() {
    private val time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private var cleanedOn: LocalDate? = null

    @Synchronized
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        try {
            val today = LocalDate.now()
            folder.mkdirs()
            if (cleanedOn != today) {
                removeOldFiles(today)
                cleanedOn = today
            }
            val line = buildString {
                append(LocalDateTime.now().format(time)).append(' ').append(severity.name.first()).append('/').append(tag)
                append(": ").append(message).append('\n')
                if (throwable != null) append(StringWriter().also { throwable.printStackTrace(PrintWriter(it)) })
            }
            File(folder, "timeflies-$today.log").appendText(line)
        } catch (e: Exception) {
            // Logging must never break the app; the console writer still has the message.
        }
    }

    private fun removeOldFiles(today: LocalDate) {
        val oldest = today.minusDays(keptDays.toLong() - 1)
        folder.listFiles { file -> file.name.matches(Regex("""timeflies-\d{4}-\d{2}-\d{2}\.log""")) }?.forEach { file ->
            val day = runCatching { LocalDate.parse(file.name.removePrefix("timeflies-").removeSuffix(".log")) }.getOrNull()
            if (day != null && day < oldest) file.delete()
        }
    }
}
