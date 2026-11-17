package io.github.fugeekies.timeflies.ui.vhs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.round_chip
import io.github.fugeekies.timeflies.ui.resources.vhs_label_date
import io.github.fugeekies.timeflies.ui.resources.vhs_rec
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.VhsCyan
import io.github.fugeekies.timeflies.ui.theme.VhsLabel
import io.github.fugeekies.timeflies.ui.theme.VhsLabelInk
import io.github.fugeekies.timeflies.ui.theme.VhsNumber
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/** Colours of the tape ornaments (docs/maquettes/android/TodayVhs.html). */
internal val VhsRed = Color(0xFFFF4D4D)
internal val VhsYellow = Color(0xFFFFE14D)

/** Secondary inks of the white label: the date line and the legend. */
internal val VhsLabelDate = Color(0xFF6B5F7A)
internal val VhsLabelSoft = Color(0xFF4A4158)

/** Length of the tape counter running up to the number of years, once a day like the Polaroïd developing. */
private const val REWIND_MILLIS = 1600

/**
 * A VHS tape of an anniversary (docs/UI_DESIGN_SPEC.md, § 1, decision of 17/11/2026), as on the mock-up: "● REC" and
 * the tape counter on top (the number of years as minutes, "00:15:00"), the two reels, then the white label stuck
 * askew with scan lines, which carries the day, the number of years in Bungee, the title in marker and the legend.
 *
 * @param dateLine the day of the anniversary, in capitals ("LUNDI 2 NOVEMBRE 2026").
 * @param legend line under the title.
 * @param userAgeLine "Tu avais 20 ans.", or null when the birth is unknown.
 * @param description what screen readers say for the whole tape.
 * @param rewind the counter runs up to the number of years (once a day, never when the system asks for fewer
 *   animations); [onRewound] is called at the end.
 */
@Composable
fun VhsTape(
    anniversary: Anniversary,
    dateLine: String,
    legend: String,
    userAgeLine: String?,
    description: String,
    modifier: Modifier = Modifier,
    rewind: Boolean = false,
    onRewound: () -> Unit = {},
) {
    val style = LocalStyle.current
    val colors = MaterialTheme.colorScheme
    val event = anniversary.event
    val counter = remember { Animatable(if (rewind) 0f else anniversary.years.toFloat()) }
    LaunchedEffect(rewind) {
        if (rewind) {
            counter.animateTo(anniversary.years.toFloat(), tween(REWIND_MILLIS, easing = FastOutSlowInEasing))
            onRewound()
        }
    }
    // The big number is part of the label: it keeps its size when the system text is enlarged, like on the Polaroïd.
    val density = LocalDensity.current
    val bigNumber = with(density) { 60.dp.toSp() }
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description }
            .background(colors.surface, shape)
            .border(2.dp, colors.outline, shape)
            .padding(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(Res.string.vhs_rec), fontFamily = style.section, fontSize = 24.sp, color = VhsRed)
            Text(tapeCounter(counter.value.toInt()), fontFamily = style.section, fontSize = 24.sp, color = VhsCyan)
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(70.dp, Alignment.CenterHorizontally)) {
            repeat(2) {
                Box(Modifier.size(54.dp).background(colors.background, CircleShape).border(8.dp, colors.outline, CircleShape))
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .rotate(-1.5f)
                .shadow(10.dp, RoundedCornerShape(4.dp))
                .background(VhsLabel)
                .drawBehind { drawScanLines() }
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(stringResource(Res.string.vhs_label_date, dateLine), fontFamily = style.section, fontSize = 20.sp, color = VhsLabelDate)
            Text(
                pluralStringResource(Res.plurals.years_count, anniversary.years, anniversary.years).uppercase(),
                fontFamily = style.accent, fontSize = bigNumber, lineHeight = bigNumber, color = VhsNumber,
            )
            Text(event.title("fr"), fontFamily = style.handwriting, fontSize = 30.sp, lineHeight = 34.sp, color = VhsLabelInk)
            Text(legend, style = MaterialTheme.typography.bodyMedium, fontSize = 15.sp, color = VhsLabelSoft)
            if (userAgeLine != null || anniversary.isRound) {
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(userAgeLine.orEmpty(), style = MaterialTheme.typography.bodyLarge, color = VhsLabelInk, modifier = Modifier.weight(1f))
                    if (anniversary.isRound) {
                        Text(
                            stringResource(Res.string.round_chip).uppercase(),
                            fontFamily = style.section,
                            fontSize = 20.sp,
                            color = VhsYellow,
                            modifier = Modifier.background(VhsLabelInk, RoundedCornerShape(3.dp)).padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

/** The tape counter of the mock-up: the number of years as minutes, "00:15:00". */
internal fun tapeCounter(years: Int): String = "00:${years.toString().padStart(2, '0')}:00"

/** Light scan lines on the label only (docs/UI_DESIGN_SPEC.md, § 1): a dark line every 3 dp, as on the mock-up. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawScanLines() {
    val step = 3.dp.toPx()
    val line = Color.Black.copy(alpha = 0.05f)
    var y = 0f
    while (y < size.height) {
        drawLine(line, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        y += step
    }
}
