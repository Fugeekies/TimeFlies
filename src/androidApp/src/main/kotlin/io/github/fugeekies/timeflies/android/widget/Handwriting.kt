package io.github.fugeekies.timeflies.android.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.github.fugeekies.timeflies.ui.readResourceFile
import java.io.File
import kotlin.math.ceil

/**
 * The years written by hand on the widget's Polaroïd. A widget cannot use the app's fonts in its texts, so the
 * number is drawn with Caveat (the same font file as the app) into an image.
 */
internal object Handwriting {
    @Volatile
    private var typeface: Typeface? = null

    /** [text] in Caveat bold at [sizeSp], in [color], on a transparent background. */
    suspend fun bitmap(context: Context, text: String, sizeSp: Float, color: Color): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = caveat(context)
            textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizeSp, context.resources.displayMetrics)
            this.color = color.toArgb()
        }
        val metrics = paint.fontMetrics
        val width = ceil(paint.measureText(text)).toInt().coerceAtLeast(1)
        val height = ceil(metrics.descent - metrics.ascent).toInt().coerceAtLeast(1)
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
            Canvas(it).drawText(text, 0f, -metrics.ascent, paint)
        }
    }

    /** Caveat from the Compose resources, copied once to the cache folder (Typeface needs a file). */
    private suspend fun caveat(context: Context): Typeface {
        typeface?.let { return it }
        val bytes = readResourceFile("font/caveat.ttf") ?: return Typeface.DEFAULT_BOLD
        val file = File(context.cacheDir, "caveat.ttf")
        if (!file.exists() || file.length() != bytes.size.toLong()) file.writeBytes(bytes)
        // Caveat is a variable font: bold as on the app's Polaroïd.
        val loaded = Typeface.create(Typeface.createFromFile(file), Typeface.BOLD)
        typeface = loaded
        return loaded
    }
}
