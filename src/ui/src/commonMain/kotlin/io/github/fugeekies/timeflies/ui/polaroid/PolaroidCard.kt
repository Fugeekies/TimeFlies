package io.github.fugeekies.timeflies.ui.polaroid

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.decade_label
import io.github.fugeekies.timeflies.ui.resources.family_and_year
import io.github.fugeekies.timeflies.ui.resources.round_chip
import io.github.fugeekies.timeflies.ui.resources.years_unit
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.photoColor
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val PhotoInk = Color(0xFFFFFDF6)
private val PhotoLabel = Color(0xFF3A2A12)
private val FrameInk = Color(0xFF2B2420)
private val FrameInkSoft = Color(0xFF55493F)

/**
 * A Polaroïd of an anniversary (docs/UI_DESIGN_SPEC.md, § 1): the frame takes the colour of the event's
 * decade with the decade written in the corner of the bottom band; the photo keeps the family colour, with the
 * type drawing in the background and the handwritten number of years on top.
 *
 * @param legend handwritten line under the title.
 * @param description what screen readers say for the whole card.
 */
@Composable
fun PolaroidCard(anniversary: Anniversary, legend: String, description: String, modifier: Modifier = Modifier) {
    val style = LocalStyle.current
    val event = anniversary.event
    val era = Era.of(anniversary.eventDate.year)
    val photo = style.photoColor(event.family)
    Box(modifier.padding(top = 14.dp, bottom = 6.dp).clearAndSetSemantics { contentDescription = description }) {
        Column(
            Modifier
                .widthIn(max = 340.dp)
                .rotate(-2.5f)
                .shadow(10.dp, RoundedCornerShape(2.dp))
                .background(era.frame)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        ) {
            Box(Modifier.fillMaxWidth().height(236.dp).background(photo)) {
                Canvas(Modifier.matchParentSize()) { drawTypeDrawing(event.type, PhotoInk.copy(alpha = 0.42f)) }
                Column(Modifier.matchParentSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        stringResource(Res.string.family_and_year, familyLabel(event.family).uppercase(), anniversary.eventDate.year),
                        style = MaterialTheme.typography.labelSmall,
                        color = PhotoLabel,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(anniversary.years.toString(), fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 120.sp, lineHeight = 110.sp, color = PhotoInk)
                        Text(
                            pluralStringResource(Res.plurals.years_unit, anniversary.years),
                            fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 44.sp, color = PhotoInk,
                            modifier = Modifier.padding(start = 8.dp, bottom = 18.dp),
                        )
                    }
                }
            }
            Box(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(top = 12.dp, bottom = 18.dp, start = 4.dp, end = 72.dp)) {
                    Text(event.title("fr"), fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 38.sp, color = FrameInk)
                    Text(legend, fontFamily = style.handwriting, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = FrameInkSoft)
                }
                DecadeStamp(anniversary.eventDate.year, era, Modifier.align(Alignment.BottomEnd).padding(bottom = 10.dp))
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

/** The decade written by hand in the corner of the frame, like a stamp: "années" above "70". */
@Composable
private fun DecadeStamp(year: Int, era: Era, modifier: Modifier = Modifier) {
    val handwriting = LocalStyle.current.handwriting
    Column(modifier.rotate(-8f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(Res.string.decade_label), fontFamily = handwriting, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 15.sp, color = era.stamp)
        Text(Era.decadeNumber(year), fontFamily = handwriting, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 36.sp, color = era.stamp)
    }
}
