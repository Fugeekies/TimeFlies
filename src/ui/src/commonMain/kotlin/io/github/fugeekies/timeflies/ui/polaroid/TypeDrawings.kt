package io.github.fugeekies.timeflies.ui.polaroid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Line drawing shown faintly inside the Polaroïd photo: one per type, or one per family while the
 * type has none yet (decision of 14/11/2026, docs/UI_DESIGN_SPEC.md, § 1). Drawn on a 100 × 100 grid.
 */
fun DrawScope.drawTypeDrawing(type: EventType, color: Color) {
    val side = min(size.width, size.height) * 0.78f
    // Top-right of the photo, away from the handwritten number at the bottom left.
    translate(left = size.width - side - size.width * 0.04f, top = size.height * 0.06f) {
        val unit = side / 100f
        val pen = Stroke(width = 3.2f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val grid = Grid(unit, color, pen)
        when (type) {
            EventType.BLOCKBUSTER -> grid.popcorn(this)
            EventType.ANIMATED_FEATURE -> grid.pencilAndSparkle(this)
            EventType.FRENCH_COMEDY_HIT -> grid.theatreMasks(this)
            EventType.SAGA_EPISODE -> grid.planetAndStars(this)
            else -> when (type.family) {
                Family.CINEMA -> grid.clapperboard(this)
                Family.SERIES_TV -> grid.television(this)
                Family.MUSIC -> grid.vinyl(this)
                Family.SPORT -> grid.ball(this)
                Family.GAMES_TECH -> grid.controller(this)
                Family.HISTORY -> grid.monument(this)
            }
        }
    }
}

/** Helpers drawing on the 100 × 100 grid. */
private class Grid(val unit: Float, val color: Color, val pen: Stroke) {
    fun p(x: Float, y: Float) = Offset(x * unit, y * unit)
    fun s(w: Float, h: Float) = Size(w * unit, h * unit)
    fun path(block: Path.() -> Unit) = Path().apply(block)
    fun Path.m(x: Float, y: Float) = moveTo(x * unit, y * unit)
    fun Path.l(x: Float, y: Float) = lineTo(x * unit, y * unit)
    fun Path.q(cx: Float, cy: Float, x: Float, y: Float) = quadraticTo(cx * unit, cy * unit, x * unit, y * unit)

    fun DrawScope.line(x1: Float, y1: Float, x2: Float, y2: Float) = drawLine(color, p(x1, y1), p(x2, y2), pen.width, StrokeCap.Round)
    fun DrawScope.circle(x: Float, y: Float, r: Float) = drawCircle(color, r * unit, p(x, y), style = pen)
    fun DrawScope.dot(x: Float, y: Float, r: Float) = drawCircle(color, r * unit, p(x, y))
    fun DrawScope.stroke(path: Path) = drawPath(path, color, style = pen)

    // Blockbuster: a striped popcorn bucket.
    fun popcorn(scope: DrawScope) = with(scope) {
        stroke(path { m(24f, 42f); l(76f, 42f); l(68f, 94f); l(32f, 94f); close() })
        line(41f, 42f, 44f, 94f); line(59f, 42f, 56f, 94f)
        listOf(30f to 36f, 42f to 30f, 54f to 32f, 66f to 36f, 37f to 22f, 50f to 18f, 62f to 24f).forEach { (x, y) -> circle(x, y, 8f) }
    }

    // Animated feature: a pencil drawing a sparkle.
    fun pencilAndSparkle(scope: DrawScope) = with(scope) {
        stroke(path { m(20f, 88f); l(26f, 70f); l(70f, 26f); l(82f, 38f); l(38f, 82f); close() })
        line(26f, 70f, 38f, 82f); line(64f, 32f, 76f, 44f)
        val star = path {
            m(74f, 58f); q(76f, 70f, 88f, 72f); q(76f, 74f, 74f, 86f); q(72f, 74f, 60f, 72f); q(72f, 70f, 74f, 58f); close()
        }
        stroke(star)
        dot(30f, 22f, 3f); dot(18f, 40f, 2.4f)
    }

    // French comedy: comedy and tragedy masks.
    fun theatreMasks(scope: DrawScope) = with(scope) {
        stroke(path { m(14f, 22f); q(40f, 14f, 58f, 22f); q(60f, 62f, 36f, 70f); q(12f, 62f, 14f, 22f); close() })
        line(24f, 34f, 32f, 34f); line(40f, 34f, 48f, 34f)
        stroke(path { m(24f, 48f); q(36f, 60f, 48f, 48f) })
        stroke(path { m(46f, 40f); q(70f, 30f, 88f, 40f); q(90f, 80f, 66f, 88f); q(48f, 80f, 46f, 60f) })
        line(58f, 52f, 64f, 50f); line(72f, 50f, 78f, 52f)
        stroke(path { m(58f, 72f); q(68f, 62f, 78f, 72f) })
    }

    // Saga: a ringed planet among stars.
    fun planetAndStars(scope: DrawScope) = with(scope) {
        circle(50f, 54f, 20f)
        stroke(path { m(18f, 64f); q(50f, 34f, 84f, 44f) })
        stroke(path { m(84f, 44f); q(92f, 50f, 70f, 62f) })
        stroke(path { m(18f, 64f); q(10f, 72f, 32f, 68f) })
        listOf(20f to 20f, 80f to 18f, 86f to 84f, 14f to 88f).forEach { (x, y) ->
            line(x - 4f, y, x + 4f, y); line(x, y - 4f, x, y + 4f)
        }
    }

    // Cinema (other types): a clapperboard.
    fun clapperboard(scope: DrawScope) = with(scope) {
        drawRect(color, p(18f, 44f), s(64f, 44f), style = pen)
        stroke(path { m(18f, 44f); l(16f, 28f); l(80f, 18f); l(82f, 34f) })
        line(32f, 26f, 38f, 42f); line(48f, 23f, 54f, 39f); line(64f, 21f, 70f, 37f)
    }

    // Series & TV: an old television.
    fun television(scope: DrawScope) = with(scope) {
        drawRoundRect(color, p(14f, 36f), s(72f, 50f), androidx.compose.ui.geometry.CornerRadius(8f * unit), style = pen)
        drawRoundRect(color, p(22f, 44f), s(46f, 34f), androidx.compose.ui.geometry.CornerRadius(6f * unit), style = pen)
        dot(77f, 52f, 3f); dot(77f, 66f, 3f)
        line(50f, 36f, 36f, 14f); line(50f, 36f, 66f, 16f)
    }

    // Music: a vinyl record.
    fun vinyl(scope: DrawScope) = with(scope) {
        circle(50f, 52f, 36f); circle(50f, 52f, 26f); circle(50f, 52f, 12f); dot(50f, 52f, 3f)
    }

    // Sport: a ball.
    fun ball(scope: DrawScope) = with(scope) {
        circle(50f, 52f, 34f)
        val pentagon = path {
            for (i in 0 until 5) {
                val a = -PI / 2 + 2 * PI * i / 5
                val x = 50f + 12f * cos(a).toFloat()
                val y = 52f + 12f * sin(a).toFloat()
                if (i == 0) m(x, y) else l(x, y)
            }
            close()
        }
        stroke(pentagon)
        for (i in 0 until 5) {
            val a = -PI / 2 + 2 * PI * i / 5
            line(50f + 12f * cos(a).toFloat(), 52f + 12f * sin(a).toFloat(), 50f + 34f * cos(a).toFloat(), 52f + 34f * sin(a).toFloat())
        }
    }

    // Video games & tech: a game controller.
    fun controller(scope: DrawScope) = with(scope) {
        stroke(path { m(28f, 36f); l(72f, 36f); q(92f, 36f, 90f, 66f); q(88f, 82f, 74f, 72f); l(66f, 62f); l(34f, 62f); l(26f, 72f); q(12f, 82f, 10f, 66f); q(8f, 36f, 28f, 36f); close() })
        line(24f, 50f, 36f, 50f); line(30f, 44f, 30f, 56f)
        dot(66f, 46f, 3.4f); dot(74f, 54f, 3.4f)
    }

    // Politics & history: a monument with columns.
    fun monument(scope: DrawScope) = with(scope) {
        stroke(path { m(14f, 36f); l(50f, 14f); l(86f, 36f); close() })
        line(14f, 86f, 86f, 86f); line(20f, 78f, 80f, 78f)
        listOf(26f, 42f, 58f, 74f).forEach { line(it, 42f, it, 74f) }
    }
}
