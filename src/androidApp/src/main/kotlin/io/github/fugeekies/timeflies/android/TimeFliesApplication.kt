package io.github.fugeekies.timeflies.android

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.work.ExistingWorkPolicy
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import io.github.fugeekies.timeflies.android.widget.WidgetUpdates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Holds the single [AppGraph] of the process, shared by the activity and the widget. Sets up the logs
 * (Kermit to Logcat, docs/DEVELOPMENT.md): never any personal data; an uncaught exception is logged before the
 * app stops.
 */
class TimeFliesApplication : Application() {
    private val debug: Boolean get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    val graph: AppGraph by lazy { AppGraph(filesDir, debug) }

    /** Work that lives as long as the process: keeping the widget up to date. */
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Logger.setMinSeverity(if (debug) Severity.Debug else Severity.Info)
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            Logger.withTag("Crash").a(error) { "Uncaught exception in ${thread.name}" }
            previous?.uncaughtException(thread, error)
        }
        Logger.withTag("App").i { "TimeFlies démarre${if (debug) " (débogage)" else ""}" }
        keepWidgetUpToDate()
    }

    /**
     * The widget follows the settings and the fake date of the debug panel; the first value also updates it at each
     * start of the process, which catches up a midnight update delayed by Android (docs/UISpec/AndroidWidget.template.md).
     */
    private fun keepWidgetUpToDate() {
        WidgetUpdates.scheduleMidnight(this, ExistingWorkPolicy.KEEP)
        scope.launch {
            // Only what the widget shows (the event filter, the style and the language included): ticking a theme of
            // Equivalences does not.
            val shown = graph.observeSettings().map { listOf(it.birth, it.region, it.onboardingDone, it.excludedEventGroups, it.displayStyle, it.language) }.distinctUntilChanged()
            combine(shown, graph.clock.manualChanges().onStart { emit(Unit) }) { _, _ -> }
                .collect {
                    try {
                        WidgetUpdates.refresh(this@TimeFliesApplication)
                    } catch (e: Exception) {
                        if (e is kotlinx.coroutines.CancellationException) throw e
                        Logger.withTag("Widget").e(e) { "Widget could not be updated" }
                    }
                }
        }
    }
}
