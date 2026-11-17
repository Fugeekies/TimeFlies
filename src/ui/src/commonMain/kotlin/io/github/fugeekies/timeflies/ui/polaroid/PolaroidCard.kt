package io.github.fugeekies.timeflies.ui.polaroid

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.family_and_year
import io.github.fugeekies.timeflies.ui.resources.round_chip
import io.github.fugeekies.timeflies.ui.resources.years_unit
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.PhotoPaper
import io.github.fugeekies.timeflies.ui.theme.bestInkOn
import io.github.fugeekies.timeflies.ui.theme.photoColor
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

internal val PhotoInk = Color(0xFFFFFDF6)
internal val PhotoLabel = Color(0xFF1E1408)
internal val FrameInk = Color(0xFF2B2420)
internal val FrameInkSoft = Color(0xFF55493F)

/** Length of the photo developing, and the parts of it: the colour first, then the writing. */
private const val DEVELOP_MILLIS = 1600
private const val COLOR_PART = 0.7f
private const val INK_START = 0.6f

/**
 * A Polaroïd of an anniversary (docs/UI_DESIGN_SPEC.md, § 1), as on the mock-up: an off-white frame for every event
 * (decision of 17/11/2026, the decade colours are gone); the photo keeps the family colour, with the
 * type drawing in the background and the handwritten number of years on top.
 *
 * @param legend handwritten line under the title.
 * @param userAgeLine handwritten line under the legend ("Tu avais 20 ans."), or null when the birth is unknown.
 * @param description what screen readers say for the whole card.
 */
@Composable
fun PolaroidCard(
    anniversary: Anniversary,
    legend: String,
    userAgeLine: String?,
    description: String,
    modifier: Modifier = Modifier,
    develop: Boolean = false,
    onDeveloped: () -> Unit = {},
) {
    val style = LocalStyle.current
    val event = anniversary.event
    val photo = style.photoColor(event.family)
    // White or ink, whichever reads best on the family colour: 3:1 at least for the big number (docs/UI_DESIGN_SPEC.md).
    val photoInk = bestInkOn(photo, PhotoInk, FrameInk)
    val photoLabel = bestInkOn(photo, PhotoLabel, PhotoInk)

    // The photo develops (docs/UI_DESIGN_SPEC.md, "Animation"): the colour rises on the white paper, then the writing.
    val progress = remember { Animatable(if (develop) 0f else 1f) }
    LaunchedEffect(develop) {
        if (develop) {
            progress.animateTo(1f, tween(DEVELOP_MILLIS, easing = LinearEasing))
            onDeveloped()
        }
    }
    val colorStep = FastOutSlowInEasing.transform((progress.value / COLOR_PART).coerceIn(0f, 1f))
    val inkAlpha = ((progress.value - INK_START) / (1f - INK_START)).coerceIn(0f, 1f)
    val shownPhoto = lerp(style.photoFade, photo, colorStep)

    // The big number is part of the photo: it keeps its size when the system text is enlarged, the photo would not hold it.
    val density = LocalDensity.current
    val bigNumber = with(density) { 120.dp.toSp() }
    val bigNumberLine = with(density) { 110.dp.toSp() }
    val unitSize = with(density) { 44.dp.toSp() }

    Box(modifier.padding(top = 14.dp, bottom = 6.dp).clearAndSetSemantics { contentDescription = description }) {
        Column(
            Modifier
                .widthIn(max = 340.dp)
                .rotate(-2.5f)
                .shadow(10.dp, RoundedCornerShape(2.dp))
                .background(PhotoPaper)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        ) {
            Box(Modifier.fillMaxWidth().height(236.dp).background(shownPhoto)) {
                Canvas(Modifier.matchParentSize().alpha(inkAlpha)) { drawTypeDrawing(event.type, photoInk.copy(alpha = 0.42f)) }
                Column(Modifier.matchParentSize().padding(14.dp).alpha(inkAlpha), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        stringResource(Res.string.family_and_year, familyLabel(event.family).uppercase(), anniversary.eventDate.year),
                        style = MaterialTheme.typography.labelSmall,
                        color = photoLabel,
                        maxLines = 1,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(anniversary.years.toString(), fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = bigNumber, lineHeight = bigNumberLine, color = photoInk)
                        Text(
                            pluralStringResource(Res.plurals.years_unit, anniversary.years),
                            fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = unitSize, color = photoInk,
                            modifier = Modifier.padding(start = 8.dp, bottom = 18.dp),
                        )
                    }
                }
            }
            Box(Modifier.fillMaxWidth().alpha(inkAlpha)) {
                Column(Modifier.padding(top = 12.dp, bottom = 18.dp, start = 4.dp, end = 4.dp)) {
                    Text(event.title("fr"), fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 38.sp, color = FrameInk)
                    Text(legend, fontFamily = style.handwriting, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = FrameInkSoft)
                    if (userAgeLine != null) {
                        Text(userAgeLine, fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = FrameInk)
                    }
                }
            }
        }
        // Tape holding the photo.
        Box(Modifier.align(Alignment.TopCenter).offset(y = (-12).dp).rotate(3f).size(width = 86.dp, height = 26.dp).background(Color(0xD9F0E2B4)))
        if (anniversary.isRound) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(50),
                // Sits on the bottom edge of the photo, clear of the handwritten legend.
                modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 22.dp).rotate(4f),
            ) {
                Text(
                    stringResource(Res.string.round_chip),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
            }
        }
    }
}

