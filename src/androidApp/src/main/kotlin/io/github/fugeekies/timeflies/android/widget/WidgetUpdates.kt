package io.github.fugeekies.timeflies.android.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.android.TimeFliesApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import io.github.fugeekies.timeflies.domain.rules.Midnight
import kotlinx.datetime.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.time.Clock as KotlinClock
import kotlin.time.Duration.Companion.minutes

/** Declared in the manifest: the system asks it to draw the widget. */
class TimeFliesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TimeFliesWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdates.scheduleMidnight(context, ExistingWorkPolicy.KEEP)
    }
}

/**
 * When the widget is computed again (docs/UISpec/AndroidWidget.template.md, "Mise à jour"): just after each local
 * midnight, at the phone's start, when the date or the time zone changes, when the settings change in the app, and
 * from the debug panel.
 */
object WidgetUpdates {
    private const val MIDNIGHT_WORK = "widget-midnight"
    private val log = Logger.withTag("Widget")

    /** Every widget on the home screen recomputes its content; a new day brings back the main sheet. */
    suspend fun refresh(context: Context) {
        val manager = GlanceAppWidgetManager(context)
        val ids = manager.getGlanceIds(TimeFliesWidget::class.java)
        if (ids.isEmpty()) return
        val today = (context.applicationContext as TimeFliesApplication).graph.clock.today().toString()
        ids.forEach { id ->
            updateAppWidgetState(context, id) { state ->
                if (state[TimeFliesWidget.DAY] != today) {
                    state[TimeFliesWidget.DAY] = today
                    state[TimeFliesWidget.SHEET] = 0
                }
                state[TimeFliesWidget.REFRESH] = (state[TimeFliesWidget.REFRESH] ?: 0) + 1
            }
            TimeFliesWidget().update(context, id)
        }
        log.d { "Widget mis à jour (${ids.size})" }
    }

    /**
     * The next update one minute past local midnight. [policy]: KEEP leaves an already planned one (app start),
     * REPLACE moves it (date or time zone changed), APPEND_OR_REPLACE chains it after the running one (the worker).
     */
    fun scheduleMidnight(context: Context, policy: ExistingWorkPolicy) {
        val delayMillis = Midnight.delayUntilNext(KotlinClock.System.now(), TimeZone.currentSystemDefault(), margin = 1.minutes).inWholeMilliseconds
        val request = OneTimeWorkRequestBuilder<MidnightWorker>().setInitialDelay(delayMillis, TimeUnit.MILLISECONDS).build()
        WorkManager.getInstance(context).enqueueUniqueWork(MIDNIGHT_WORK, policy, request)
    }

    /** Whether the launcher can pin the widget from the app ("Ajouter le widget" in Settings). */
    fun canPin(context: Context): Boolean = AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported

    /** Asks the launcher to pin the widget; the launcher shows its own confirmation. */
    fun pin(context: Context) {
        AppWidgetManager.getInstance(context).requestPinAppWidget(ComponentName(context, TimeFliesWidgetReceiver::class.java), null, null)
    }
}

/** Runs just after midnight: recomputes the widget and plans the next night. Android may delay it (battery). */
class MidnightWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        WidgetUpdates.refresh(applicationContext)
        // KEEP would see this running work and plan nothing, REPLACE would cancel it: chained after it instead.
        WidgetUpdates.scheduleMidnight(applicationContext, ExistingWorkPolicy.APPEND_OR_REPLACE)
        return Result.success()
    }
}

/** Phone started, date or time zone changed: the widget is recomputed and midnight planned again. */
class WidgetEventsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                WidgetUpdates.scheduleMidnight(context, ExistingWorkPolicy.REPLACE)
                WidgetUpdates.refresh(context)
            } finally {
                pending.finish()
            }
        }
    }
}

/** The "›" button: the next anniversary of the day (the index grows, the widget reads it modulo the sheets). */
class NextSheetAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { state ->
            state[TimeFliesWidget.SHEET] = (state[TimeFliesWidget.SHEET] ?: 0) + 1
        }
        TimeFliesWidget().update(context, glanceId)
    }
}
