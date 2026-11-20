package io.github.fugeekies.timeflies.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.big_shoulders_display
import io.github.fugeekies.timeflies.ui.resources.bungee
import io.github.fugeekies.timeflies.ui.resources.caveat
import io.github.fugeekies.timeflies.ui.resources.style_ephemeris
import io.github.fugeekies.timeflies.ui.resources.style_polaroid
import io.github.fugeekies.timeflies.ui.resources.style_vhs
import io.github.fugeekies.timeflies.ui.resources.vhs_rec
import io.github.fugeekies.timeflies.ui.resources.vt323
import io.github.fugeekies.timeflies.ui.theme.EphemerisLight
import io.github.fugeekies.timeflies.ui.theme.EphemerisRingLight
import io.github.fugeekies.timeflies.ui.theme.FamilyColors
import io.github.fugeekies.timeflies.ui.theme.PhotoPaper
import io.github.fugeekies.timeflies.ui.theme.VhsDark
import io.github.fugeekies.timeflies.ui.theme.VhsLabel
import io.github.fugeekies.timeflies.ui.theme.VhsNumber
import io.github.fugeekies.timeflies.ui.vhs.VhsRed
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val DisplayStyle.label: StringResource
    get() = when (this) {
        DisplayStyle.POLAROID -> Res.string.style_polaroid
        DisplayStyle.VHS -> Res.string.style_vhs
        DisplayStyle.EPHEMERIS -> Res.string.style_ephemeris
    }

/**
 * Settings › Affichage › Style (docs/UISpec/Settings.template.md): one choice per style, with a miniature of its
 * sheet of the day in its own colours and fonts, whatever the style shown around it. Side by side when the three
 * fit, one under the other on a narrow phone.
 */
@Composable
internal fun StylePicker(selected: DisplayStyle, onSelect: (DisplayStyle) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth >= MIN_SIDE_BY_SIDE * DisplayStyle.entries.size) {
            Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DisplayStyle.entries.forEach { style ->
                    val isSelected = style == selected
                    Column(
                        Modifier
                            .weight(1f)
                            .widthIn(max = 160.dp)
                            .selectable(isSelected, role = Role.RadioButton) { onSelect(style) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Miniature(style)
                        StyleLabel(style, isSelected)
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxWidth().selectableGroup()) {
                DisplayStyle.entries.forEach { style ->
                    val isSelected = style == selected
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(isSelected, role = Role.RadioButton) { onSelect(style) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StyleLabel(style, isSelected, Modifier.weight(1f))
                        Miniature(style)
                    }
                }
            }
        }
    }
}

/** Width of one choice when they are side by side: the miniature and its label. */
private val MIN_SIDE_BY_SIDE = 140.dp

@Composable
private fun Miniature(style: DisplayStyle) {
    Box(Modifier.size(width = 100.dp, height = 84.dp).clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
        when (style) {
            DisplayStyle.POLAROID -> PolaroidMiniature()
            DisplayStyle.VHS -> VhsMiniature()
            DisplayStyle.EPHEMERIS -> EphemerisMiniature()
        }
    }
}

@Composable
private fun StyleLabel(style: DisplayStyle, isSelected: Boolean, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = isSelected, onClick = null, modifier = Modifier.padding(12.dp))
        Text(stringResource(style.label), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** A tiny Polaroïd: white frame, a cinema-amber photo with a handwritten "15". */
@Composable
private fun PolaroidMiniature() {
    val caveat = FontFamily(Font(Res.font.caveat, FontWeight.Bold))
    Column(
        Modifier.rotate(-3f).shadow(3.dp, RoundedCornerShape(1.dp)).background(PhotoPaper).padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 14.dp),
    ) {
        Box(Modifier.size(width = 58.dp, height = 48.dp).background(FamilyColors.getValue(Family.CINEMA)), contentAlignment = Alignment.Center) {
            Text("15", fontFamily = caveat, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = PhotoPaper)
        }
    }
}

/** A tiny VHS tape: "● REC" on the dark shell, the white label askew with a magenta "15". */
@Composable
private fun VhsMiniature() {
    val bungee = FontFamily(Font(Res.font.bungee))
    val vt323 = FontFamily(Font(Res.font.vt323))
    val shape = RoundedCornerShape(6.dp)
    Column(
        Modifier.size(width = 92.dp, height = 76.dp).background(VhsDark.surface, shape).border(1.dp, VhsDark.outline, shape).padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(stringResource(Res.string.vhs_rec), fontFamily = vt323, fontSize = 13.sp, lineHeight = 13.sp, color = VhsRed)
        Box(Modifier.fillMaxWidth().rotate(-2f).background(VhsLabel, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text("15", fontFamily = bungee, fontSize = 24.sp, lineHeight = 26.sp, color = VhsNumber)
        }
    }
}

/** A tiny calendar page: two rings, the perforated line and a red "15" on white, in the light colours. */
@Composable
private fun EphemerisMiniature() {
    val shoulders = FontFamily(
        Font(Res.font.big_shoulders_display, FontWeight.Black, variationSettings = FontVariation.Settings(FontVariation.weight(900))),
    )
    val shape = RoundedCornerShape(8.dp)
    Column(
        Modifier.size(width = 72.dp, height = 78.dp).shadow(3.dp, shape).background(EphemerisLight.surface, shape),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, top = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(2) { Box(Modifier.size(7.dp).background(EphemerisLight.background, CircleShape).border(1.dp, EphemerisRingLight, CircleShape)) }
        }
        Box(Modifier.fillMaxWidth().padding(top = 5.dp).height(1.dp).background(EphemerisLight.outline))
        Text("15", fontFamily = shoulders, fontWeight = FontWeight.Black, fontSize = 42.sp, lineHeight = 46.sp, color = EphemerisLight.primary)
    }
}
