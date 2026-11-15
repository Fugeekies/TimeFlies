package io.github.fugeekies.timeflies.android

import android.app.Application
import android.content.pm.ApplicationInfo
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

/**
 * Holds the single [AppGraph] of the process, shared by the activity and, later, the widget. Sets up the logs
 * (Kermit to Logcat, docs/DEVELOPMENT.md): never any personal data; an uncaught exception is logged before the
 * app stops.
 */
class TimeFliesApplication : Application() {
    private val debug: Boolean get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    val graph: AppGraph by lazy { AppGraph(filesDir, debug) }

    override fun onCreate() {
        super.onCreate()
        Logger.setMinSeverity(if (debug) Severity.Debug else Severity.Info)
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            Logger.withTag("Crash").a(error) { "Uncaught exception in ${thread.name}" }
            previous?.uncaughtException(thread, error)
        }
        Logger.withTag("App").i { "TimeFlies démarre${if (debug) " (débogage)" else ""}" }
    }
}
