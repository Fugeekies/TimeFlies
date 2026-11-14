package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.atkinson_bold
import io.github.fugeekies.timeflies.ui.resources.atkinson_regular
import io.github.fugeekies.timeflies.ui.resources.caveat
import org.jetbrains.compose.resources.Font

/**
 * Tokens of a display style (docs/UI_DESIGN_SPEC.md, § 1 and 2). V1 ships the Polaroïd style only;
 * Éphéméride will be another instance of this class, without touching the screens.
 */
@Immutable
data class StyleTokens(
    val handwriting: FontFamily,
    val familyColors: Map<Family, Color>,
    val photoFade: Color,
)

val LocalStyle = staticCompositionLocalOf<StyleTokens> { error("TimeFliesTheme is missing") }

/** Family colours (docs/UI_DESIGN_SPEC.md, § 3), shared by every style. */
private val FamilyColors = mapOf(
    Family.SERIES_TV to Color(0xFF7C5CFF),
    Family.CINEMA to Color(0xFFE09F1F),
    Family.MUSIC to Color(0xFFE5487C),
    Family.SPORT to Color(0xFF1FA463),
    Family.GAMES_TECH to Color(0xFF2E8BFF),
    Family.HISTORY to Color(0xFF8A7F72),
)

// Polaroïd, light (docs/UI_DESIGN_SPEC.md, § 2). The dark version comes with milestone 9.
private val PolaroidLight = lightColorScheme(
    background = Color(0xFFE8DFD2),
    onBackground = Color(0xFF2B2420),
    surface = Color(0xFFFBFAF7),
    onSurface = Color(0xFF2B2420),
    surfaceVariant = Color(0xFFFBFAF7),
    onSurfaceVariant = Color(0xFF6A5D52),
    surfaceContainer = Color(0xFFFBFAF7),
    primary = Color(0xFF9A4E1C),
    onPrimary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF1E1D3),
    onSecondaryContainer = Color(0xFF2B2420),
    outline = Color(0xFFD6CBBC),
    outlineVariant = Color(0xFFD6CBBC),
)

/** The family colour slightly faded, like an old print. */
fun StyleTokens.photoColor(family: Family): Color = lerp(familyColors.getValue(family), photoFade, 0.12f)

@Composable
fun TimeFliesTheme(content: @Composable () -> Unit) {
    val atkinson = FontFamily(
        Font(Res.font.atkinson_regular, FontWeight.Normal),
        Font(Res.font.atkinson_bold, FontWeight.Bold),
    )
    val caveat = FontFamily(
        Font(Res.font.caveat, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
        Font(Res.font.caveat, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
    )
    val base = Typography()
    val typography = Typography(
        displayLarge = base.displayLarge.copy(fontFamily = atkinson),
        headlineMedium = base.headlineMedium.copy(fontFamily = atkinson),
        titleLarge = base.titleLarge.copy(fontFamily = atkinson, fontWeight = FontWeight.Bold),
        titleMedium = base.titleMedium.copy(fontFamily = atkinson, fontWeight = FontWeight.Bold, fontSize = 16.sp),
        bodyLarge = base.bodyLarge.copy(fontFamily = atkinson, fontSize = 16.sp),
        bodyMedium = base.bodyMedium.copy(fontFamily = atkinson, fontSize = 14.sp),
        bodySmall = base.bodySmall.copy(fontFamily = atkinson, fontSize = 13.sp),
        labelLarge = base.labelLarge.copy(fontFamily = atkinson, fontWeight = FontWeight.Bold),
        labelMedium = base.labelMedium.copy(fontFamily = atkinson),
        labelSmall = TextStyle(fontFamily = atkinson, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.8.sp),
    )
    val tokens = StyleTokens(handwriting = caveat, familyColors = FamilyColors, photoFade = Color(0xFFFBFAF7))
    CompositionLocalProvider(LocalStyle provides tokens) {
        MaterialTheme(colorScheme = PolaroidLight, typography = typography, content = content)
    }
}
