package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import io.github.fugeekies.timeflies.ui.polaroid.Era
import io.github.fugeekies.timeflies.ui.polaroid.FrameInk
import io.github.fugeekies.timeflies.ui.polaroid.FrameInkSoft
import io.github.fugeekies.timeflies.ui.polaroid.PhotoInk
import io.github.fugeekies.timeflies.ui.polaroid.PhotoLabel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** WCAG AA contrast of the Polaroïd tokens, light and dark (docs/UI_DESIGN_SPEC.md, "Accessibilité"). */
class ContrastTest {

    private val text = 4.5f
    private val large = 3f

    private fun assertContrast(min: Float, foreground: Color, background: Color, what: String) {
        val ratio = contrastRatio(foreground, background)
        assertTrue(ratio >= min, "$what: ${"%.2f".format(ratio)} < $min")
    }

    private fun checkScheme(name: String, scheme: ColorScheme, extras: PolaroidExtras) {
        for ((bgName, bg) in listOf("background" to scheme.background, "surface" to scheme.surface, "surfaceContainerHigh" to scheme.surfaceContainerHigh)) {
            assertContrast(text, scheme.onSurface, bg, "$name onSurface on $bgName")
            assertContrast(text, scheme.onSurfaceVariant, bg, "$name onSurfaceVariant on $bgName")
            assertContrast(text, scheme.primary, bg, "$name primary on $bgName")
        }
        assertContrast(text, scheme.onBackground, scheme.background, "$name onBackground")
        assertContrast(text, scheme.onPrimary, scheme.primary, "$name onPrimary")
        assertContrast(text, scheme.onSecondaryContainer, scheme.secondaryContainer, "$name onSecondaryContainer")
        assertContrast(text, scheme.primary, scheme.secondaryContainer, "$name primary on secondaryContainer (round chip)")
        // The two people's ages, handwritten (20 sp and more, bold) on cards and on the background.
        for ((who, color) in listOf("user" to extras.userColor, "other" to extras.otherColor)) {
            assertContrast(text, color, scheme.surface, "$name $who on surface")
            assertContrast(large, color, scheme.background, "$name $who on background")
        }
    }

    @Test
    fun lightTokensAreAA() = checkScheme("light", PolaroidLight, PolaroidLightExtras)

    @Test
    fun darkTokensAreAA() = checkScheme("dark", PolaroidDark, PolaroidDarkExtras)

    @Test
    fun theWritingOnEveryFrameIsReadable() {
        for (era in Era.entries) {
            assertContrast(text, FrameInk, era.frame, "ink on $era")
            assertContrast(text, FrameInkSoft, era.frame, "soft ink on $era")
            assertContrast(large, era.stamp, era.frame, "decade stamp on $era")
        }
    }

    @Test
    fun theBigNumberAndTheLabelReadOnEveryPhoto() {
        for ((family, color) in FamilyColors) {
            val photo = lerp(color, PhotoPaper, 0.12f)
            assertContrast(large, bestInkOn(photo, PhotoInk, FrameInk), photo, "big number on $family")
            assertContrast(text, bestInkOn(photo, PhotoLabel, PhotoInk), photo, "label on $family")
        }
    }

    @Test
    fun computesTheWcagRatio() {
        assertEquals(21f, contrastRatio(Color.White, Color.Black), 0.01f)
        assertEquals(1f, contrastRatio(Color.Red, Color.Red), 0.01f)
    }
}
