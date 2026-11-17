package io.github.fugeekies.timeflies.android.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.ui.readResourceFile
import java.io.File
import kotlin.math.ceil

/**
 * The number of years on the widget's sheet. A widget cannot use the app's fonts in its texts, so the number is
 * drawn into an image with the font of the chosen style, from the same font file as the app: Caveat written by hand
 * on the Polaroïd, Bungee on the VHS label.
 */
internal object Handwriting {
    /** Loaded fonts, by resource file name. */
    private val typefaces = java.util.concurrent.ConcurrentHashMap<String, Typeface>()

    /** [text] in the font of [style] (Caveat bold or Bungee) at [sizeSp], in [color], on a transparent background. */
    suspend fun bitmap(context: Context, text: String, sizeSp: Float, color: Color, style: DisplayStyle = DisplayStyle.POLAROID): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = when (style) {
                DisplayStyle.POLAROID -> font(context, "caveat.ttf", Typeface.BOLD)
                DisplayStyle.VHS -> font(context, "bungee.ttf", Typeface.NORMAL)
            }
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

    /** A font of the Compose resources, copied once to the cache folder (Typeface needs a file). */
    private suspend fun font(context: Context, name: String, weight: Int): Typeface {
        typefaces[name]?.let { return it }
        val bytes = readResourceFile("font/$name") ?: return Typeface.DEFAULT_BOLD
        val file = File(context.cacheDir, name)
        if (!file.exists() || file.length() != bytes.size.toLong()) file.writeBytes(bytes)
        // Caveat is a variable font: bold as on the app's Polaroïd.
        val loaded = Typeface.create(Typeface.createFromFile(file), weight)
        typefaces[name] = loaded
        return loaded
    }
}
