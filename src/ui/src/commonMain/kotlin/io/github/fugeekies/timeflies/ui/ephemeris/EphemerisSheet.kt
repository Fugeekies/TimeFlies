package io.github.fugeekies.timeflies.ui.ephemeris

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.round_chip
import io.github.fugeekies.timeflies.ui.resources.years_unit
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.theme.EphemerisRingDark
import io.github.fugeekies.timeflies.ui.theme.EphemerisRingLight
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/** The yesterday's sheet waits a moment, then is torn off upwards, once a day like the Polaroïd developing. */
private const val TEAR_DELAY_MILLIS = 300
private const val TEAR_MILLIS = 1300

/**
 * A page of a tear-off calendar for an anniversary (docs/UI_DESIGN_SPEC.md, § 1), as on the mock-up
 * (docs/maquettes/android/Today.html): two binding rings and a perforated line on top, then the day, the number of
 * years in big red figures instead of the day of the month, the title, the legend, the user's age, the "Chiffre rond"
 * chip and the family.
 *
 * @param dateLine the day of the anniversary, in capitals ("LUNDI 2 NOVEMBRE 2026").
 * @param previousDateLine the day before, written on the sheet torn off by the animation.
 * @param legend line under the title.
 * @param userAgeLine "Tu avais 20 ans.", or null when the birth is unknown.
 * @param description what screen readers say for the whole sheet.
 * @param tear yesterday's sheet is torn off to show this one (once a day, never when the system asks for fewer
 *   animations); [onTorn] is called at the end.
 */
@Composable
fun EphemerisSheet(
    anniversary: Anniversary,
    dateLine: String,
    previousDateLine: String,
    legend: String,
    userAgeLine: String?,
    description: String,
    modifier: Modifier = Modifier,
    tear: Boolean = false,
    onTorn: () -> Unit = {},
) {
    val style = LocalStyle.current
    val colors = MaterialTheme.colorScheme
    val event = anniversary.event
    val progress = remember { Animatable(if (tear) 0f else 1f) }
    LaunchedEffect(tear) {
        if (tear) {
            progress.animateTo(1f, tween(TEAR_MILLIS, delayMillis = TEAR_DELAY_MILLIS, easing = FastOutLinearInEasing))
            onTorn()
        }
    }
    // The big number is the page itself: it keeps its size when the system text is enlarged, like on the Polaroïd.
    val density = LocalDensity.current
    val bigNumber = with(density) { 120.dp.toSp() }
    val bigNumberLine = with(density) { 116.dp.toSp() }
    val unitSize = with(density) { 44.dp.toSp() }
    val shape = RoundedCornerShape(18.dp)
    val ring = if (colors.background.luminance() < 0.5f) EphemerisRingDark else EphemerisRingLight

    Column(
        modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description }
            .shadow(6.dp, shape, clip = false)
            .background(colors.surface, shape),
    ) {
        Row(Modifier.fillMaxWidth().padding(start = 44.dp, end = 44.dp, top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(2) { Box(Modifier.size(14.dp).background(colors.background, CircleShape).border(2.dp, ring, CircleShape)) }
        }
        Perforation(colors.outline, Modifier.padding(top = 10.dp))
        Box {
            Column(Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Eyebrow(dateLine)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        anniversary.years.toString(),
                        fontFamily = style.accent, fontWeight = FontWeight.Black, fontSize = bigNumber, lineHeight = bigNumberLine,
                        color = colors.primary,
                    )
                    Text(
                        pluralStringResource(Res.plurals.years_unit, anniversary.years),
                        fontFamily = style.accent, fontWeight = FontWeight.Bold, fontSize = unitSize,
                        color = colors.primary, modifier = Modifier.padding(bottom = 14.dp),
                    )
                }
                Text(event.title("fr"), style = MaterialTheme.typography.titleLarge, fontSize = 28.sp, lineHeight = 34.sp, color = colors.onSurface)
                Text(legend, style = MaterialTheme.typography.bodyLarge, color = colors.onSurfaceVariant)
                if (userAgeLine != null || anniversary.isRound) {
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(userAgeLine.orEmpty(), style = MaterialTheme.typography.bodyLarge, fontSize = 17.sp, color = colors.onSurface, modifier = Modifier.weight(1f))
                        if (anniversary.isRound) {
                            Text(
                                stringResource(Res.string.round_chip),
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.onSecondaryContainer,
                                modifier = Modifier.background(colors.secondaryContainer, CircleShape).padding(horizontal = 10.dp, vertical = 5.dp),
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(8.dp).background(style.familyColors.getValue(event.family), CircleShape))
                    Text(familyLabel(event.family), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                }
            }
            if (progress.value < 1f) TornSheet(previousDateLine, progress.value, Modifier.matchParentSize())
        }
    }
}

/** The date line at the top of a page: small capitals, spaced out. */
@Composable
private fun Eyebrow(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** The dashed line where the page is torn off. */
@Composable
private fun Perforation(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(2.dp)) {
        val dash = 6.dp.toPx()
        drawLine(
            color, Offset(0f, size.height / 2), Offset(size.width, size.height / 2),
            strokeWidth = size.height, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash * 0.7f)),
        )
    }
}

/**
 * Yesterday's page over today's, torn off from the perforated line (docs/UI_DESIGN_SPEC.md, "Animation"): it goes up,
 * turning a little from its left corner, and fades away at the end.
 */
@Composable
private fun TornSheet(dateLine: String, progress: Float, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0f)
                translationY = -progress * size.height * 1.2f
                rotationZ = -8f * progress
                alpha = 1f - progress * progress
            }
            .shadow(if (progress > 0f) 8.dp else 0.dp, RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp), clip = false)
            .background(colors.surface, RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
            .padding(start = 22.dp, end = 22.dp, top = 16.dp),
    ) {
        Eyebrow(dateLine)
    }
}
