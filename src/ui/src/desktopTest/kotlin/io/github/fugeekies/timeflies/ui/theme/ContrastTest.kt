package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import io.github.fugeekies.timeflies.ui.polaroid.Era
import io.github.fugeekies.timeflies.ui.polaroid.FrameInk
import io.github.fugeekies.timeflies.ui.polaroid.FrameInkSoft
import io.github.fugeekies.timeflies.ui.polaroid.PhotoInk
import io.github.fugeekies.timeflies.ui.polaroid.PhotoLabel
import io.github.fugeekies.timeflies.ui.vhs.VhsLabelDate
import io.github.fugeekies.timeflies.ui.vhs.VhsLabelSoft
import io.github.fugeekies.timeflies.ui.vhs.VhsRed
import io.github.fugeekies.timeflies.ui.vhs.VhsYellow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * WCAG AA contrast of the Polaroïd and Éphéméride tokens, light and dark, and of the VHS tokens
 * (docs/UI_DESIGN_SPEC.md, "Accessibilité").
 */
class ContrastTest {

    private val text = 4.5f
    private val large = 3f

    private fun assertContrast(min: Float, foreground: Color, background: Color, what: String) {
        val ratio = contrastRatio(foreground, background)
        assertTrue(ratio >= min, "$what: ${"%.2f".format(ratio)} < $min")
    }

    /** @param chipInk ink of the "Chiffre rond" chip on [ColorScheme.secondaryContainer]: the primary by default. */
    private fun checkScheme(name: String, scheme: ColorScheme, extras: StyleExtras, chipInk: Color = scheme.primary) {
        for ((bgName, bg) in listOf("background" to scheme.background, "surface" to scheme.surface, "surfaceContainerHigh" to scheme.surfaceContainerHigh)) {
            assertContrast(text, scheme.onSurface, bg, "$name onSurface on $bgName")
            assertContrast(text, scheme.onSurfaceVariant, bg, "$name onSurfaceVariant on $bgName")
            assertContrast(text, scheme.primary, bg, "$name primary on $bgName")
        }
        assertContrast(text, scheme.onBackground, scheme.background, "$name onBackground")
        assertContrast(text, scheme.onPrimary, scheme.primary, "$name onPrimary")
        assertContrast(text, scheme.onSecondaryContainer, scheme.secondaryContainer, "$name onSecondaryContainer")
        assertContrast(text, chipInk, scheme.secondaryContainer, "$name round chip on secondaryContainer")
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
    fun vhsTokensAreAA() = checkScheme("vhs", VhsDark, VhsExtras)

    // On the Éphéméride page, the round chip is written in onSecondaryContainer, the deeper red of the mock-up.
    @Test
    fun ephemerisLightTokensAreAA() = checkScheme("ephemeris light", EphemerisLight, EphemerisLightExtras, EphemerisLight.onSecondaryContainer)

    @Test
    fun ephemerisDarkTokensAreAA() = checkScheme("ephemeris dark", EphemerisDark, EphemerisDarkExtras, EphemerisDark.onSecondaryContainer)

    @Test
    fun theWidgetNumberReadsOnBothEphemerisPages() {
        // Drawn once into an image in the light red, on the white or the dark page following the system.
        assertContrast(large, EphemerisLight.primary, EphemerisLight.surface, "widget number on the light page")
        assertContrast(large, EphemerisLight.primary, EphemerisDark.surface, "widget number on the dark page")
    }

    @Test
    fun theWritingOnTheVhsTapeIsReadable() {
        assertContrast(text, VhsLabelInk, VhsLabel, "ink on the label")
        assertContrast(text, VhsLabelDate, VhsLabel, "date line on the label")
        assertContrast(text, VhsLabelSoft, VhsLabel, "legend on the label")
        assertContrast(large, VhsNumber, VhsLabel, "big number on the label")
        assertContrast(text, VhsYellow, VhsLabelInk, "round chip on the label")
        assertContrast(text, VhsRed, VhsDark.surface, "REC on the tape")
        assertContrast(text, VhsCyan, VhsDark.surface, "tape counter on the tape")
        assertContrast(text, VhsCyan, VhsDark.background, "section titles")
        assertContrast(text, VhsYellow, VhsDark.background, "PLAY in the header")
    }

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
