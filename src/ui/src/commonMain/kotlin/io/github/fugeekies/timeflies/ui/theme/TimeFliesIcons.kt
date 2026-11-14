package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Stroke icons of the navigation, drawn as in the mock-ups (docs/maquettes/). Tinted by `Icon`. */
object TimeFliesIcons {
    val Calendar: ImageVector = strokeIcon("Calendar") {
        moveTo(5f, 5f); lineTo(19f, 5f); quadTo(21f, 5f, 21f, 7f); lineTo(21f, 19f); quadTo(21f, 21f, 19f, 21f)
        lineTo(5f, 21f); quadTo(3f, 21f, 3f, 19f); lineTo(3f, 7f); quadTo(3f, 5f, 5f, 5f); close()
        moveTo(3f, 10f); lineTo(21f, 10f)
        moveTo(8f, 3f); lineTo(8f, 7f)
        moveTo(16f, 3f); lineTo(16f, 7f)
    }

    val Compare: ImageVector = strokeIcon("Compare") {
        moveTo(4f, 8f); lineTo(18f, 8f)
        moveTo(14f, 4f); lineTo(18f, 8f); lineTo(14f, 12f)
        moveTo(20f, 16f); lineTo(6f, 16f)
        moveTo(10f, 12f); lineTo(6f, 16f); lineTo(10f, 20f)
    }

    val Search: ImageVector = strokeIcon("Search") {
        moveTo(18f, 11f); arcTo(7f, 7f, 0f, true, true, 4f, 11f); arcTo(7f, 7f, 0f, true, true, 18f, 11f); close()
        moveTo(20f, 20f); lineTo(16f, 16f)
    }

    val Tune: ImageVector = strokeIcon("Tune") {
        moveTo(4f, 6f); lineTo(14f, 6f); moveTo(18f, 6f); lineTo(20f, 6f)
        moveTo(4f, 12f); lineTo(8f, 12f); moveTo(12f, 12f); lineTo(20f, 12f)
        moveTo(4f, 18f); lineTo(16f, 18f)
        moveTo(18f, 6f); arcTo(2f, 2f, 0f, true, true, 14f, 6f); arcTo(2f, 2f, 0f, true, true, 18f, 6f)
        moveTo(12f, 12f); arcTo(2f, 2f, 0f, true, true, 8f, 12f); arcTo(2f, 2f, 0f, true, true, 12f, 12f)
        moveTo(20f, 18f); arcTo(2f, 2f, 0f, true, true, 16f, 18f); arcTo(2f, 2f, 0f, true, true, 20f, 18f)
    }

    private fun strokeIcon(name: String, block: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
            .path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathBuilder = block,
            )
            .build()
}
