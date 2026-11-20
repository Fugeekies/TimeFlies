package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.atkinson_bold
import io.github.fugeekies.timeflies.ui.resources.atkinson_regular
import io.github.fugeekies.timeflies.ui.resources.big_shoulders_display
import io.github.fugeekies.timeflies.ui.resources.bungee
import io.github.fugeekies.timeflies.ui.resources.caveat
import io.github.fugeekies.timeflies.ui.resources.permanent_marker
import io.github.fugeekies.timeflies.ui.resources.vt323
import org.jetbrains.compose.resources.Font

/**
 * Tokens of a display style (docs/UI_DESIGN_SPEC.md, § 1 and 2): Polaroïd, VHS or Éphéméride, the layout of the
 * screens being the same in all of them.
 *
 * @property handwriting the hand-written font of the sheet of the day: Caveat on the Polaroïd, Permanent Marker on
 *   the label of the VHS tape; the readable font on the Éphéméride, which has no handwriting.
 * @property accent font of the numbers of years, the gaps and the screen titles (Caveat, Bungee or Big Shoulders
 *   Display); see [accent].
 * @property accentScale size of [accent] for the same role: Bungee is much wider than Caveat.
 * @property accentWeight Caveat is written bold, Bungee has a single weight, Big Shoulders Display is black.
 * @property section font of the section titles ("Bientôt…"): Caveat, VT323 like the tape counter, or the readable
 *   font in small capitals.
 * @property sectionColor colour of the section titles.
 */
@Immutable
data class StyleTokens(
    val displayStyle: DisplayStyle,
    val handwriting: FontFamily,
    val accent: FontFamily,
    val accentScale: Float,
    val accentWeight: FontWeight,
    val section: FontFamily,
    val sectionColor: Color,
    val familyColors: Map<Family, Color>,
    val photoFade: Color,
    /**
     * Pictogram colours of the people of "Quel âge ?" and Equivalences, instead of names (decision of 15/11/2026):
     * the user first, then the three ages that can be added (decision of 17/11/2026).
     */
    val personColors: List<Color>,
)

val LocalStyle = staticCompositionLocalOf<StyleTokens> { error("TimeFliesTheme is missing") }

/**
 * The accent text of the style, for a role that is [size] sp in the Polaroïd (numbers of years, gaps, screen titles):
 * Caveat bold, or Bungee about a quarter smaller so it takes the same room.
 */
fun StyleTokens.accent(size: Float, lineHeight: Float? = null): TextStyle = TextStyle(
    fontFamily = accent,
    fontWeight = accentWeight,
    fontSize = (size * accentScale).sp,
    lineHeight = lineHeight?.let { (it * accentScale).sp } ?: TextUnit.Unspecified,
)

/** Family colours (docs/UI_DESIGN_SPEC.md, § 3), shared by every style. */
internal val FamilyColors = mapOf(
    Family.SERIES_TV to Color(0xFF7C5CFF),
    Family.CINEMA to Color(0xFFE09F1F),
    Family.MUSIC to Color(0xFFE5487C),
    Family.SPORT to Color(0xFF1FA463),
    Family.GAMES_TECH to Color(0xFF2E8BFF),
    Family.HISTORY to Color(0xFF8A7F72),
    // The user's own events (decision of 19/11/2026): a teal no theme uses.
    Family.PERSONAL to Color(0xFF13A39B),
)

/**
 * Colours of a style that are not Material roles (docs/UI_DESIGN_SPEC.md, § 2), one set per theme.
 *
 * @property personColors pictogram colours on the "Quel âge ?" and Equivalences screens: the user, then the three
 *   other ages (decisions of 15/11/2026 and 17/11/2026).
 */
internal data class StyleExtras(val personColors: List<Color>)

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

internal val PolaroidLightExtras = StyleExtras(listOf(Color(0xFFB4501E), Color(0xFF0E7C7B), Color(0xFF7A4A9E), Color(0xFF4F6B12)))

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

internal val PolaroidDarkExtras = StyleExtras(listOf(Color(0xFFF08A55), Color(0xFF4FC3C0), Color(0xFFC3A3F0), Color(0xFFB5D168)))

// VHS, dark only: the colours of the mock-up (docs/maquettes/android/TodayVhs.html, decision of 17/11/2026).
internal val VhsDark: ColorScheme = darkColorScheme(
    background = Color(0xFF0E0B16),
    onBackground = Color(0xFFF2ECFF),
    surface = Color(0xFF1B1824),
    onSurface = Color(0xFFF2ECFF),
    surfaceVariant = Color(0xFF1B1824),
    onSurfaceVariant = Color(0xFFB9B0CC),
    surfaceContainerLowest = Color(0xFF0E0B16),
    surfaceContainerLow = Color(0xFF151220),
    surfaceContainer = Color(0xFF151220),
    surfaceContainerHigh = Color(0xFF1B1824),
    surfaceContainerHighest = Color(0xFF241F33),
    primary = Color(0xFFFF3EA5),
    onPrimary = Color(0xFF0E0B16),
    // The magenta at 20 % on the background, like the active tab of the mock-up.
    secondaryContainer = Color(0xFF3E1533),
    onSecondaryContainer = Color(0xFFF2ECFF),
    outline = Color(0xFF2E2940),
    outlineVariant = Color(0xFF2E2940),
)

internal val VhsExtras = StyleExtras(listOf(Color(0xFFFFE14D), Color(0xFF3EE6FF), Color(0xFF7CFF8A), Color(0xFFFF9F43)))

/** The cyan of the tape counter and of the section titles of the VHS. */
internal val VhsCyan = Color(0xFF3EE6FF)

// Éphéméride, light (docs/UI_DESIGN_SPEC.md, § 2). The red is a shade darker than the #D7263D of the mock-up, so
// that it reads at 4.5:1 on the paper-grey background too (4.42:1 before).
internal val EphemerisLight: ColorScheme = lightColorScheme(
    background = Color(0xFFF3F2EE),
    onBackground = Color(0xFF1F1B24),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F1B24),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF5E5866),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFF3F2EE),
    surfaceContainerHighest = Color(0xFFEAE8E3),
    primary = Color(0xFFD3243B),
    onPrimary = Color(0xFFFFFFFF),
    // The red at 14 % on white, like the active tab of the mock-up; its ink is the deeper red of the round chip.
    secondaryContainer = Color(0xFFF9E1E4),
    onSecondaryContainer = Color(0xFFB01F33),
    outline = Color(0xFFD9D5DE),
    outlineVariant = Color(0xFFD9D5DE),
)

internal val EphemerisLightExtras = StyleExtras(listOf(Color(0xFFD3243B), Color(0xFF1F6FB2), Color(0xFF2E7D32), Color(0xFF7B4FA0)))

// Éphéméride, dark (docs/UI_DESIGN_SPEC.md, § 2).
internal val EphemerisDark: ColorScheme = darkColorScheme(
    background = Color(0xFF16141A),
    onBackground = Color(0xFFEDEAF2),
    surface = Color(0xFF221F27),
    onSurface = Color(0xFFEDEAF2),
    surfaceVariant = Color(0xFF221F27),
    onSurfaceVariant = Color(0xFFA8A1B2),
    surfaceContainerLowest = Color(0xFF16141A),
    surfaceContainerLow = Color(0xFF1C1A21),
    surfaceContainer = Color(0xFF221F27),
    surfaceContainerHigh = Color(0xFF2A2630),
    surfaceContainerHighest = Color(0xFF322D39),
    primary = Color(0xFFFF5C6C),
    onPrimary = Color(0xFF16141A),
    // The red at 20 % on the background.
    secondaryContainer = Color(0xFF45222A),
    onSecondaryContainer = Color(0xFFEDEAF2),
    outline = Color(0xFF3A3542),
    outlineVariant = Color(0xFF3A3542),
)

internal val EphemerisDarkExtras = StyleExtras(listOf(Color(0xFFFF5C6C), Color(0xFF6CB4FF), Color(0xFF6FD47A), Color(0xFFC9A2FF)))

/** The binding rings at the top of the sheet of the day: a grey rim on the colour of the background. */
internal val EphemerisRingLight = Color(0xFFC9C4CF)
internal val EphemerisRingDark = Color(0xFF4A4452)

/**
 * The photo paper. A Polaroïd stays a light print in both themes, so the handwriting on its frame keeps a dark ink
 * (docs/UI_DESIGN_SPEC.md, § 1).
 */
internal val PhotoPaper = Color(0xFFFBFAF7)

/** The white label stuck on the VHS tape, and its ink (docs/UI_DESIGN_SPEC.md, § 1). */
internal val VhsLabel = Color(0xFFF4F1E8)
internal val VhsLabelInk = Color(0xFF16121F)

/** The VHS has no light version (docs/UI_DESIGN_SPEC.md, § 1): it is always dark. The others follow the system. */
fun DisplayStyle.isDark(systemDark: Boolean): Boolean = when (this) {
    DisplayStyle.POLAROID, DisplayStyle.EPHEMERIS -> systemDark
    DisplayStyle.VHS -> true
}

/**
 * Colours of the Android widget, taken from the tokens of the chosen style so it matches the app: Glance draws with
 * its own views and cannot use the Compose theme (docs/UISpec/AndroidWidget.template.md).
 *
 * @property paper the sheet of the day: the Polaroïd photo paper, or the label of the VHS tape.
 * @property ink the writing on [paper].
 * @property number colour of the big number on [paper].
 */
data class WidgetPalette(
    val background: Color,
    val onBackground: Color,
    val secondary: Color,
    val accent: Color,
    val paper: Color,
    val ink: Color,
    val number: Color,
) {
    companion object {
        fun of(dark: Boolean, style: DisplayStyle = DisplayStyle.POLAROID): WidgetPalette = when (style) {
            DisplayStyle.POLAROID -> {
                val scheme = if (dark) PolaroidDark else PolaroidLight
                WidgetPalette(
                    background = scheme.surface,
                    onBackground = scheme.onSurface,
                    secondary = scheme.onSurfaceVariant,
                    accent = scheme.primary,
                    paper = PhotoPaper,
                    ink = PolaroidLight.onSurface,
                    number = PolaroidLight.onSurface,
                )
            }
            DisplayStyle.VHS -> WidgetPalette(
                background = VhsDark.surface,
                onBackground = VhsDark.onSurface,
                secondary = VhsDark.onSurfaceVariant,
                accent = VhsDark.primary,
                paper = VhsLabel,
                ink = VhsLabelInk,
                number = VhsNumber,
            )
            DisplayStyle.EPHEMERIS -> {
                val scheme = if (dark) EphemerisDark else EphemerisLight
                WidgetPalette(
                    background = scheme.background,
                    onBackground = scheme.onSurface,
                    secondary = scheme.onSurfaceVariant,
                    accent = scheme.primary,
                    paper = scheme.surface,
                    ink = scheme.onSurface,
                    // Drawn once into an image for both themes: the light red, which reads on both sheets.
                    number = EphemerisLight.primary,
                )
            }
        }
    }
}

/** The big number on the VHS label, a deeper magenta than the accent so it reads on the white label. */
internal val VhsNumber = Color(0xFFE0177F)

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
 * The theme of the app: the chosen style, light or dark following the system (docs/UI_DESIGN_SPEC.md, § 2). The VHS
 * is always dark.
 *
 * @param style the display style chosen in Settings › Affichage.
 * @param darkTheme dark colours; the system setting by default. Ignored by the VHS.
 */
@Composable
fun TimeFliesTheme(
    style: DisplayStyle = DisplayStyle.POLAROID,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val atkinson = FontFamily(
        Font(Res.font.atkinson_regular, FontWeight.Normal),
        Font(Res.font.atkinson_bold, FontWeight.Bold),
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
    val dark = style.isDark(darkTheme)
    val (scheme, tokens) = when (style) {
        DisplayStyle.POLAROID -> {
            val caveat = FontFamily(
                Font(Res.font.caveat, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
                Font(Res.font.caveat, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
            )
            val scheme = if (dark) PolaroidDark else PolaroidLight
            val extras = if (dark) PolaroidDarkExtras else PolaroidLightExtras
            scheme to StyleTokens(
                displayStyle = style,
                handwriting = caveat,
                accent = caveat,
                accentScale = 1f,
                accentWeight = FontWeight.Bold,
                section = caveat,
                sectionColor = scheme.onBackground,
                familyColors = FamilyColors,
                photoFade = PhotoPaper,
                personColors = extras.personColors,
            )
        }
        DisplayStyle.VHS -> VhsDark to StyleTokens(
            displayStyle = style,
            handwriting = FontFamily(Font(Res.font.permanent_marker, FontWeight.Normal)),
            accent = FontFamily(Font(Res.font.bungee, FontWeight.Normal)),
            accentScale = 0.72f,
            accentWeight = FontWeight.Normal,
            section = FontFamily(Font(Res.font.vt323, FontWeight.Normal)),
            sectionColor = VhsCyan,
            familyColors = FamilyColors,
            photoFade = VhsLabel,
            personColors = VhsExtras.personColors,
        )
        DisplayStyle.EPHEMERIS -> {
            val shoulders = FontFamily(
                Font(Res.font.big_shoulders_display, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
                Font(Res.font.big_shoulders_display, FontWeight.Black, variationSettings = FontVariation.Settings(FontVariation.weight(900))),
            )
            val scheme = if (dark) EphemerisDark else EphemerisLight
            val extras = if (dark) EphemerisDarkExtras else EphemerisLightExtras
            scheme to StyleTokens(
                displayStyle = style,
                // No handwriting on a calendar page: the readable font.
                handwriting = atkinson,
                accent = shoulders,
                accentScale = 1f,
                accentWeight = FontWeight.Black,
                // Section titles are small capitals in the readable font, as on the mock-up.
                section = atkinson,
                sectionColor = scheme.onSurfaceVariant,
                familyColors = FamilyColors,
                photoFade = scheme.surface,
                personColors = extras.personColors,
            )
        }
    }
    CompositionLocalProvider(LocalStyle provides tokens) {
        MaterialTheme(colorScheme = scheme, typography = typography, content = content)
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
