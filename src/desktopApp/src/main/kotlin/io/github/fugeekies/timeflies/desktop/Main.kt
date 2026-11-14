package io.github.fugeekies.timeflies.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.fugeekies.timeflies.ui.App
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TimeFlies",
        state = rememberWindowState(size = DpSize(1000.dp, 700.dp)),
    ) {
        window.minimumSize = Dimension(420, 640)
        App()
    }
}
