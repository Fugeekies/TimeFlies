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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Line drawing shown faintly inside the Polaroïd photo, one per type (decisions of 14/11/2026 and 17/11/2026,
 * docs/UI_DESIGN_SPEC.md, § 1). Drawn on a 100 × 100 grid: simple shapes, never a logo nor a real work.
 *
 * No default case: a new type does not compile until it has its drawing (AGENTS.md).
 */
fun DrawScope.drawTypeDrawing(type: EventType, color: Color) {
    val side = min(size.width, size.height) * 0.78f
    // Top-right of the photo, away from the handwritten number at the bottom left.
    translate(left = size.width - side - size.width * 0.04f, top = size.height * 0.06f) {
        val unit = side / 100f
        val pen = Stroke(width = 3.2f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val grid = Grid(unit, color, pen)
        when (type) {
            // Series & TV
            EventType.SITCOM_HIT -> grid.sofa(this)
            EventType.DRAMA_SERIES_HIT -> grid.television(this)
            EventType.FRENCH_SERIES_HIT -> grid.eiffelTower(this)
            EventType.REALITY_TV -> grid.camcorder(this)
            EventType.KIDS_TV_SHOW -> grid.teddyBear(this)
            EventType.CARTOON_ON_TV -> grid.speechBubble(this)
            // Cinema
            EventType.BLOCKBUSTER -> grid.popcorn(this)
            EventType.ANIMATED_FEATURE -> grid.pencilAndSparkle(this)
            EventType.FRENCH_COMEDY_HIT -> grid.theatreMasks(this)
            EventType.SAGA_EPISODE -> grid.planetAndStars(this)
            // Music
            EventType.MUSIC_ICON_DEATH -> grid.candle(this)
            EventType.DIVA_HIT -> grid.microphone(this)
            EventType.BOY_BAND_HIT -> grid.threeFaces(this)
            EventType.RAP_ALBUM_HIT -> grid.boombox(this)
            EventType.SUMMER_HIT -> grid.sunOverWaves(this)
            EventType.POP_ALBUM_HIT -> grid.vinyl(this)
            // Sport
            EventType.FOOTBALL_STAR_PEAK -> grid.ball(this)
            EventType.WORLD_CUP_WIN_FRANCE -> grid.trophy(this)
            EventType.OLYMPICS_IN_FRANCE -> grid.torch(this)
            EventType.SPORTS_RECORD -> grid.stopwatch(this)
            EventType.WORLD_CUP_WIN -> grid.laurelStar(this)
            EventType.TENNIS_GRAND_SLAM -> grid.racketAndBall(this)
            EventType.TOUR_DE_FRANCE_WIN -> grid.bicycle(this)
            // Video games & tech
            EventType.NINTENDO_CONSOLE -> grid.controller(this)
            EventType.PLAYSTATION_CONSOLE -> grid.disc(this)
            EventType.HANDHELD_CONSOLE -> grid.handheld(this)
            EventType.VIDEO_GAME_PHENOMENON -> grid.arcadeStick(this)
            EventType.PHONE_LAUNCH -> grid.smartphone(this)
            EventType.SOCIAL_NETWORK_LAUNCH -> grid.heartBubble(this)
            EventType.MUSIC_FORMAT -> grid.cassette(this)
            // Politics & history
            EventType.FRENCH_PRESIDENT_ELECTED -> grid.ballotBox(this)
            EventType.US_PRESIDENT_ELECTED -> grid.starBadge(this)
            EventType.ACTOR_TURNED_POLITICIAN -> grid.starAndLectern(this)
            EventType.WORLD_EVENT -> grid.globe(this)
            EventType.ROYAL_EVENT -> grid.crown(this)
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

    fun DrawScope.box(x: Float, y: Float, w: Float, h: Float, r: Float = 0f) =
        drawRoundRect(color, p(x, y), s(w, h), androidx.compose.ui.geometry.CornerRadius(r * unit), style = pen)

    /** A five-pointed star centred on (x, y). */
    fun DrawScope.star(x: Float, y: Float, outer: Float, inner: Float) = stroke(path {
        for (i in 0 until 10) {
            val a = -PI / 2 + PI * i / 5
            val r = if (i % 2 == 0) outer else inner
            val px = x + r * cos(a).toFloat()
            val py = y + r * sin(a).toFloat()
            if (i == 0) m(px, py) else l(px, py)
        }
        close()
    })

    // Sitcom: the sofa of the living room.
    fun sofa(scope: DrawScope) = with(scope) {
        box(20f, 30f, 60f, 30f, 8f)
        box(8f, 48f, 16f, 30f, 7f); box(76f, 48f, 16f, 30f, 7f)
        box(24f, 56f, 52f, 22f, 4f)
        line(50f, 56f, 50f, 78f)
        line(18f, 78f, 18f, 88f); line(82f, 78f, 82f, 88f)
    }

    // French series: the Eiffel Tower.
    fun eiffelTower(scope: DrawScope) = with(scope) {
        stroke(path { m(50f, 8f); q(44f, 50f, 26f, 92f) })
        stroke(path { m(50f, 8f); q(56f, 50f, 74f, 92f) })
        line(41f, 40f, 59f, 40f); line(34f, 64f, 66f, 64f)
        stroke(path { m(38f, 92f); q(50f, 72f, 62f, 92f) })
        line(50f, 4f, 50f, 8f)
    }

    // Reality TV: a camcorder on its tripod, recording.
    fun camcorder(scope: DrawScope) = with(scope) {
        box(12f, 30f, 54f, 36f, 6f)
        stroke(path { m(66f, 40f); l(88f, 30f); l(88f, 66f); l(66f, 56f) })
        dot(22f, 39f, 3.4f)
        line(39f, 66f, 26f, 92f); line(39f, 66f, 52f, 92f); line(39f, 66f, 39f, 92f)
    }

    // Children's show: a teddy bear.
    fun teddyBear(scope: DrawScope) = with(scope) {
        circle(32f, 22f, 7f); circle(68f, 22f, 7f)
        circle(50f, 38f, 18f)
        dot(43f, 35f, 2.4f); dot(57f, 35f, 2.4f); dot(50f, 44f, 3f)
        circle(50f, 76f, 18f)
        circle(28f, 70f, 6f); circle(72f, 70f, 6f)
    }

    // Cartoon: a comic speech bubble with an exclamation mark.
    fun speechBubble(scope: DrawScope) = with(scope) {
        box(12f, 14f, 76f, 54f, 22f)
        stroke(path { m(32f, 67f); l(24f, 88f); l(48f, 68f) })
        line(50f, 26f, 50f, 46f); dot(50f, 56f, 3.4f)
    }

    // Death of a music icon: a lit candle.
    fun candle(scope: DrawScope) = with(scope) {
        box(38f, 46f, 24f, 44f, 3f)
        stroke(path { m(50f, 14f); q(64f, 30f, 50f, 40f); q(36f, 30f, 50f, 14f); close() })
        line(50f, 40f, 50f, 46f)
        line(32f, 90f, 68f, 90f)
    }

    // Diva: a stage microphone.
    fun microphone(scope: DrawScope) = with(scope) {
        box(36f, 8f, 28f, 42f, 14f)
        line(36f, 22f, 64f, 22f); line(36f, 34f, 64f, 34f)
        stroke(path { m(26f, 36f); q(26f, 62f, 50f, 62f); q(74f, 62f, 74f, 36f) })
        line(50f, 62f, 50f, 84f); line(34f, 88f, 66f, 88f)
    }

    // Boy band: three faces side by side.
    fun threeFaces(scope: DrawScope) = with(scope) {
        circle(24f, 44f, 9f); circle(76f, 44f, 9f); circle(50f, 34f, 10f)
        stroke(path { m(8f, 80f); q(24f, 56f, 40f, 80f) })
        stroke(path { m(60f, 80f); q(76f, 56f, 92f, 80f) })
        stroke(path { m(32f, 76f); q(50f, 46f, 68f, 76f) })
    }

    // Rap album: a boombox.
    fun boombox(scope: DrawScope) = with(scope) {
        box(10f, 36f, 80f, 46f, 6f)
        stroke(path { m(28f, 36f); l(30f, 22f); l(70f, 22f); l(72f, 36f) })
        circle(29f, 60f, 12f); circle(71f, 60f, 12f)
        dot(29f, 60f, 3f); dot(71f, 60f, 3f)
        box(44f, 50f, 12f, 18f, 2f)
    }

    // Summer hit: the sun over the waves.
    fun sunOverWaves(scope: DrawScope) = with(scope) {
        circle(56f, 38f, 13f)
        for (i in 0 until 8) {
            val a = 2 * PI * i / 8
            line(56f + 19f * cos(a).toFloat(), 38f + 19f * sin(a).toFloat(), 56f + 26f * cos(a).toFloat(), 38f + 26f * sin(a).toFloat())
        }
        for (y in listOf(76f, 88f)) {
            stroke(path { m(8f, y); q(18f, y - 8f, 28f, y); q(38f, y + 8f, 48f, y); q(58f, y - 8f, 68f, y); q(78f, y + 8f, 88f, y) })
        }
    }

    // World Cup: a trophy cup.
    fun trophy(scope: DrawScope) = with(scope) {
        stroke(path { m(30f, 14f); l(70f, 14f); q(70f, 54f, 50f, 58f); q(30f, 54f, 30f, 14f); close() })
        stroke(path { m(30f, 22f); q(12f, 22f, 16f, 36f); q(20f, 46f, 33f, 44f) })
        stroke(path { m(70f, 22f); q(88f, 22f, 84f, 36f); q(80f, 46f, 67f, 44f) })
        line(50f, 58f, 50f, 72f)
        box(34f, 72f, 32f, 10f, 2f)
        line(28f, 88f, 72f, 88f)
    }

    // Olympic Games: the torch and its flame.
    fun torch(scope: DrawScope) = with(scope) {
        stroke(path { m(36f, 48f); l(64f, 48f); l(54f, 92f); l(46f, 92f); close() })
        line(32f, 48f, 68f, 48f)
        stroke(path { m(50f, 8f); q(70f, 26f, 60f, 42f); l(40f, 42f); q(30f, 26f, 50f, 8f); close() })
        stroke(path { m(50f, 24f); q(58f, 34f, 50f, 42f); q(42f, 34f, 50f, 24f) })
    }


    // World Cup win (other nations): the champion's star inside a laurel wreath.
    fun laurelStar(scope: DrawScope) = with(scope) {
        star(50f, 44f, 16f, 7f)
        stroke(path { m(46f, 88f); q(14f, 76f, 18f, 30f) })
        stroke(path { m(54f, 88f); q(86f, 76f, 82f, 30f) })
        listOf(18f to 40f, 20f to 54f, 26f to 67f, 36f to 79f).forEach { (x, y) -> drawOval(color, p(x - 9f, y - 4f), s(9f, 6f), style = pen) }
        listOf(82f to 40f, 80f to 54f, 74f to 67f, 64f to 79f).forEach { (x, y) -> drawOval(color, p(x, y - 4f), s(9f, 6f), style = pen) }
    }
    // Grand Slam tennis: a racket with its strings, and a ball.
    fun racketAndBall(scope: DrawScope) = with(scope) {
        drawOval(color, p(18f, 10f), s(44f, 54f), style = pen)
        line(30f, 16f, 30f, 58f); line(40f, 11f, 40f, 63f); line(50f, 16f, 50f, 58f)
        line(22f, 26f, 58f, 26f); line(19f, 37f, 61f, 37f); line(22f, 48f, 58f, 48f)
        line(40f, 64f, 40f, 72f)
        box(35f, 72f, 10f, 22f, 3f)
        circle(76f, 76f, 10f)
        stroke(path { m(68f, 70f); q(76f, 76f, 70f, 84f) })
    }

    // Tour de France win: a racing bicycle.
    fun bicycle(scope: DrawScope) = with(scope) {
        circle(24f, 68f, 18f); circle(76f, 68f, 18f)
        stroke(path { m(24f, 68f); l(42f, 40f); l(68f, 40f); l(50f, 68f); close() })
        line(50f, 68f, 38f, 30f); line(32f, 30f, 44f, 30f)
        line(68f, 40f, 76f, 68f); line(68f, 40f, 66f, 30f); line(62f, 30f, 72f, 28f)
        dot(50f, 68f, 3f)
    }

    // Sports record: a stopwatch.
    fun stopwatch(scope: DrawScope) = with(scope) {
        circle(50f, 58f, 30f)
        box(44f, 14f, 12f, 8f, 2f); line(50f, 22f, 50f, 28f)
        line(72f, 34f, 78f, 28f)
        line(50f, 58f, 50f, 40f); line(50f, 58f, 62f, 64f)
        dot(50f, 58f, 3f)
    }

    // PlayStation console: an optical disc (the console's medium, never its logo).
    fun disc(scope: DrawScope) = with(scope) {
        circle(50f, 52f, 36f); circle(50f, 52f, 10f); dot(50f, 52f, 3f)
        stroke(path { m(28f, 44f); q(32f, 30f, 46f, 26f) })
        stroke(path { m(72f, 60f); q(68f, 74f, 54f, 78f) })
    }

    // Handheld console: a screen above a cross and two buttons.
    fun handheld(scope: DrawScope) = with(scope) {
        box(28f, 8f, 44f, 84f, 8f)
        box(34f, 16f, 32f, 30f, 3f)
        line(36f, 64f, 48f, 64f); line(42f, 58f, 42f, 70f)
        dot(58f, 68f, 3.4f); dot(65f, 61f, 3.4f)
        line(54f, 82f, 62f, 82f)
    }

    // Video game phenomenon: an arcade joystick.
    fun arcadeStick(scope: DrawScope) = with(scope) {
        stroke(path { m(16f, 68f); l(84f, 68f); l(90f, 90f); l(10f, 90f); close() })
        line(50f, 68f, 50f, 34f)
        circle(50f, 24f, 11f)
        dot(28f, 80f, 3.4f); dot(72f, 80f, 3.4f)
    }

    // Phone launch: a smartphone.
    fun smartphone(scope: DrawScope) = with(scope) {
        box(32f, 8f, 36f, 84f, 8f)
        box(37f, 18f, 26f, 58f, 2f)
        line(45f, 13f, 55f, 13f)
        dot(50f, 84f, 3f)
    }

    // Social network: a heart in a message bubble.
    fun heartBubble(scope: DrawScope) = with(scope) {
        box(12f, 14f, 76f, 56f, 14f)
        stroke(path { m(30f, 69f); l(24f, 88f); l(46f, 70f) })
        stroke(path { m(50f, 58f); q(28f, 44f, 35f, 33f); q(43f, 24f, 50f, 34f); q(57f, 24f, 65f, 33f); q(72f, 44f, 50f, 58f); close() })
    }

    // Music format: an audio cassette.
    fun cassette(scope: DrawScope) = with(scope) {
        box(10f, 24f, 80f, 56f, 6f)
        box(22f, 34f, 56f, 24f, 4f)
        circle(37f, 46f, 6f); circle(63f, 46f, 6f)
        stroke(path { m(26f, 80f); l(32f, 66f); l(68f, 66f); l(74f, 80f) })
    }

    // French presidential election: a ballot going into the ballot box.
    fun ballotBox(scope: DrawScope) = with(scope) {
        stroke(path { m(42f, 52f); l(46f, 14f); l(68f, 18f); l(63f, 52f) })
        line(50f, 28f, 60f, 36f); line(60f, 28f, 50f, 36f)
        box(16f, 50f, 68f, 40f, 3f)
        line(34f, 50f, 70f, 50f)
        line(16f, 62f, 84f, 62f)
    }

    // US presidential election: a campaign badge with a star and ribbons.
    fun starBadge(scope: DrawScope) = with(scope) {
        circle(50f, 40f, 26f)
        star(50f, 40f, 15f, 6.5f)
        stroke(path { m(38f, 63f); l(30f, 92f); l(40f, 86f); l(46f, 94f); l(48f, 66f) })
        stroke(path { m(62f, 63f); l(70f, 92f); l(60f, 86f); l(54f, 94f); l(52f, 66f) })
    }

    // Actor turned politician: a film star above a speaker's lectern.
    fun starAndLectern(scope: DrawScope) = with(scope) {
        star(50f, 24f, 16f, 7f)
        stroke(path { m(24f, 52f); l(76f, 52f); l(72f, 46f); l(28f, 46f); close() })
        stroke(path { m(32f, 52f); l(68f, 52f); l(62f, 92f); l(38f, 92f); close() })
        line(30f, 92f, 70f, 92f)
    }

    // Major world event: a globe.
    fun globe(scope: DrawScope) = with(scope) {
        circle(50f, 50f, 36f)
        drawOval(color, p(36f, 14f), s(28f, 72f), style = pen)
        line(14f, 50f, 86f, 50f)
        stroke(path { m(20f, 32f); q(50f, 40f, 80f, 32f) })
        stroke(path { m(20f, 68f); q(50f, 60f, 80f, 68f) })
    }

    // Royal event: a crown.
    fun crown(scope: DrawScope) = with(scope) {
        stroke(path { m(14f, 72f); l(18f, 32f); l(36f, 54f); l(50f, 24f); l(64f, 54f); l(82f, 32f); l(86f, 72f); close() })
        box(14f, 72f, 72f, 14f, 3f)
        dot(18f, 27f, 3.4f); dot(50f, 19f, 3.4f); dot(82f, 27f, 3.4f)
        dot(50f, 79f, 3f)
    }

    // Drama series: an old television.
    fun television(scope: DrawScope) = with(scope) {
        drawRoundRect(color, p(14f, 36f), s(72f, 50f), androidx.compose.ui.geometry.CornerRadius(8f * unit), style = pen)
        drawRoundRect(color, p(22f, 44f), s(46f, 34f), androidx.compose.ui.geometry.CornerRadius(6f * unit), style = pen)
        dot(77f, 52f, 3f); dot(77f, 66f, 3f)
        line(50f, 36f, 36f, 14f); line(50f, 36f, 66f, 16f)
    }

    // Pop album: a vinyl record.
    fun vinyl(scope: DrawScope) = with(scope) {
        circle(50f, 52f, 36f); circle(50f, 52f, 26f); circle(50f, 52f, 12f); dot(50f, 52f, 3f)
    }

    // Football star: a ball.
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

    // Nintendo console: a game controller.
    fun controller(scope: DrawScope) = with(scope) {
        stroke(path { m(28f, 36f); l(72f, 36f); q(92f, 36f, 90f, 66f); q(88f, 82f, 74f, 72f); l(66f, 62f); l(34f, 62f); l(26f, 72f); q(12f, 82f, 10f, 66f); q(8f, 36f, 28f, 36f); close() })
        line(24f, 50f, 36f, 50f); line(30f, 44f, 30f, 56f)
        dot(66f, 46f, 3.4f); dot(74f, 54f, 3.4f)
    }
}
