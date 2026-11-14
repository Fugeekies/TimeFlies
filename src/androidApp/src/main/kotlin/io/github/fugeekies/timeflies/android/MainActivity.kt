package io.github.fugeekies.timeflies.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.fugeekies.timeflies.ui.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val graph = (application as TimeFliesApplication).graph
        setContent { App(getDailyShock = graph.getDailyShock, clock = graph.clock) }
    }
}
