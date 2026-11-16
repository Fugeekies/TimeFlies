package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
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
    /** Pictogram colours of the two people of the Equivalences screen, instead of names (decision of 15/11/2026). */
    val userColor: Color,
    val otherColor: Color,
)

val LocalStyle = staticCompositionLocalOf<StyleTokens> { error("TimeFliesTheme is missing") }

/** Family colours (docs/UI_DESIGN_SPEC.md, § 3), shared by every style. */
internal val FamilyColors = mapOf(
    Family.SERIES_TV to Color(0xFF7C5CFF),
    Family.CINEMA to Color(0xFFE09F1F),
    Family.MUSIC to Color(0xFFE5487C),
    Family.SPORT to Color(0xFF1FA463),
    Family.GAMES_TECH to Color(0xFF2E8BFF),
    Family.HISTORY to Color(0xFF8A7F72),
)

/**
 * Colours of the Polaroïd style that are not Material roles (docs/UI_DESIGN_SPEC.md, § 2), one set per theme.
 *
 * @property userColor pictogram colour of the user on the Equivalences and "Quel âge ?" screens (decision of 15/11/2026).
 * @property otherColor pictogram colour of the other person.
 */
internal data class PolaroidExtras(val userColor: Color, val otherColor: Color)

// Polaroïd, light (docs/UI_DESIGN_SPEC.md, § 2).
internal val PolaroidLight: ColorScheme = lightColorScheme(
    background = Color(0xFFE8DFD2),
    onBackground = Color(0xFF2B2420),
    surface = Color(0xFFFBFAF7),
    onSurface = Color(0xFF2B2420),
    surfaceVariant = Color(0xFFFBFAF7),
    onSurfaceVariant = Color(0xFF6A5D52),
    surfaceContainerLowest = Color(0xFFFBFAF7),
    surfaceContainerLow = Color(0xFFFBFAF7),
    surfaceContainer = Color(0xFFFBFAF7),
    surfaceContainerHigh = Color(0xFFFBFAF7),
    surfaceContainerHighest = Color(0xFFF4EEE6),
    primary = Color(0xFF9A4E1C),
    onPrimary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF1E1D3),
    onSecondaryContainer = Color(0xFF2B2420),
    outline = Color(0xFFD6CBBC),
    outlineVariant = Color(0xFFD6CBBC),
)

internal val PolaroidLightExtras = PolaroidExtras(userColor = Color(0xFFB4501E), otherColor = Color(0xFF0E7C7B))

// Polaroïd, dark: "chambre noire", a warm darkroom brown with a lighter copper (decision of 15/11/2026).
internal val PolaroidDark: ColorScheme = darkColorScheme(
    background = Color(0xFF1C1714),
    onBackground = Color(0xFFF1E8DD),
    surface = Color(0xFF2A231F),
    onSurface = Color(0xFFF1E8DD),
    surfaceVariant = Color(0xFF2A231F),
    onSurfaceVariant = Color(0xFFBFAF9F),
    surfaceContainerLowest = Color(0xFF1C1714),
    surfaceContainerLow = Color(0xFF241E1A),
    surfaceContainer = Color(0xFF2A231F),
    surfaceContainerHigh = Color(0xFF332A25),
    surfaceContainerHighest = Color(0xFF3A302A),
    primary = Color(0xFFE39A62),
    onPrimary = Color(0xFF1C1714),
    secondaryContainer = Color(0xFF4A3426),
    onSecondaryContainer = Color(0xFFF1E8DD),
    outline = Color(0xFF4A3F37),
    outlineVariant = Color(0xFF4A3F37),
)

internal val PolaroidDarkExtras = PolaroidExtras(userColor = Color(0xFFF08A55), otherColor = Color(0xFF4FC3C0))

/**
 * The photo paper. A Polaroïd stays a light print in both themes, so the handwriting on its frame keeps a dark ink
 * (docs/UI_DESIGN_SPEC.md, § 1).
 */
internal val PhotoPaper = Color(0xFFFBFAF7)

/**
 * Colours of the Android widget, taken from the Polaroïd tokens so it matches the app: Glance draws with its own
 * views and cannot use the Compose theme (docs/UISpec/AndroidWidget.template.md).
 */
data class WidgetPalette(
    val background: Color,
    val onBackground: Color,
    val secondary: Color,
    val accent: Color,
    val paper: Color,
    val ink: Color,
) {
    companion object {
        fun of(dark: Boolean): WidgetPalette {
            val scheme = if (dark) PolaroidDark else PolaroidLight
            return WidgetPalette(
                background = scheme.surface,
                onBackground = scheme.onSurface,
                secondary = scheme.onSurfaceVariant,
                accent = scheme.primary,
                paper = PhotoPaper,
                ink = PolaroidLight.onSurface,
            )
        }
    }
}

/** The family colour slightly faded, like an old print. */
fun StyleTokens.photoColor(family: Family): Color = lerp(familyColors.getValue(family), photoFade, 0.12f)

/** Contrast ratio between two colours, from 1 to 21 (WCAG 2): 4.5 for text, 3 for large text. */
fun contrastRatio(a: Color, b: Color): Float {
    val (light, dark) = if (a.luminance() >= b.luminance()) a to b else b to a
    return (light.luminance() + 0.05f) / (dark.luminance() + 0.05f)
}

/** Of [first] and [second], the one that reads best on [background]. */
fun bestInkOn(background: Color, first: Color, second: Color): Color =
    if (contrastRatio(first, background) >= contrastRatio(second, background)) first else second

/**
 * The theme of the app: the Polaroïd style, light or dark following the system (docs/UI_DESIGN_SPEC.md, § 2).
 *
 * @param darkTheme dark colours; the system setting by default.
 */
@Composable
fun TimeFliesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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
    val extras = if (darkTheme) PolaroidDarkExtras else PolaroidLightExtras
    val tokens = StyleTokens(
        handwriting = caveat,
        familyColors = FamilyColors,
        photoFade = PhotoPaper,
        userColor = extras.userColor,
        otherColor = extras.otherColor,
    )
    CompositionLocalProvider(LocalStyle provides tokens) {
        MaterialTheme(colorScheme = if (darkTheme) PolaroidDark else PolaroidLight, typography = typography, content = content)
    }
}

/**
 * Light or dark: the system's choice, or forced from the debug panel to try both themes (docs/DEVELOPMENT.md,
 * "Panneau de débogage"). Never saved.
 */
enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    @Composable
    fun isDark(): Boolean = when (this) {
        SYSTEM -> isSystemInDarkTheme()
        LIGHT -> false
        DARK -> true
    }
}
