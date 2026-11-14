package io.github.fugeekies.timeflies.android

import android.app.Application

/** Holds the single [AppGraph] of the process, shared by the activity and, later, the widget. */
class TimeFliesApplication : Application() {
    val graph: AppGraph by lazy { AppGraph() }
}
