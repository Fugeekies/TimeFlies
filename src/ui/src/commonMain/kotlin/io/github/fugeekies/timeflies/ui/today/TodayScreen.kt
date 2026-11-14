package io.github.fugeekies.timeflies.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.model.UpcomingDay
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.a11y_anniversary
import io.github.fugeekies.timeflies.ui.resources.also_today
import io.github.fugeekies.timeflies.ui.resources.app_name
import io.github.fugeekies.timeflies.ui.resources.day_and_delay
import io.github.fugeekies.timeflies.ui.resources.family_and_year
import io.github.fugeekies.timeflies.ui.resources.in_days
import io.github.fugeekies.timeflies.ui.resources.kind_and_date
import io.github.fugeekies.timeflies.ui.resources.kind_and_year
import io.github.fugeekies.timeflies.ui.resources.load_error
import io.github.fugeekies.timeflies.ui.resources.loading
import io.github.fugeekies.timeflies.ui.resources.nothing_soon
import io.github.fugeekies.timeflies.ui.resources.round_chip
import io.github.fugeekies.timeflies.ui.resources.upcoming
import io.github.fugeekies.timeflies.ui.resources.will_turn
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.resources.years_unit
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.capitalized
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.photoColor
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/** The Today screen (docs/UISpec/Today.template.md), Polaroïd style. */
@Composable
fun TodayScreen(viewModel: TodayViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val current = state) {
        TodayUiState.Loading -> CenteredText(stringResource(Res.string.loading), modifier)
        TodayUiState.Error -> CenteredText(stringResource(Res.string.load_error), modifier)
        is TodayUiState.Loaded -> TodayContent(current.shock, modifier)
    }
}

@Composable
private fun CenteredText(text: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TodayContent(shock: DailyShock, modifier: Modifier = Modifier) {
    val dates = rememberDateTexts()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val twoColumns = maxWidth >= 840.dp
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Header(dates.withDayName(shock.today).capitalized())
            if (twoColumns) {
                Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                    Box(Modifier.weight(1f)) { MainSheet(shock, dates) }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) { Lists(shock, dates) }
                }
            } else {
                MainSheet(shock, dates)
                Lists(shock, dates)
            }
        }
    }
}

@Composable
private fun Header(today: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            stringResource(Res.string.app_name),
            fontFamily = LocalStyle.current.handwriting,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(today, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MainSheet(shock: DailyShock, dates: DateTexts) {
    val main = shock.main
    if (main == null) {
        Text(
            stringResource(Res.string.nothing_soon),
            fontFamily = LocalStyle.current.handwriting,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 24.dp),
        )
        return
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (shock.mainInDays > 0) {
            Text(
                stringResource(
                    Res.string.day_and_delay,
                    pluralStringResource(Res.plurals.in_days, shock.mainInDays, shock.mainInDays).capitalized(),
                    dates.dayAndMonth(main.day),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Polaroid(main, isToday = shock.mainInDays == 0, dates = dates)
    }
}

@Composable
private fun Polaroid(anniversary: Anniversary, isToday: Boolean, dates: DateTexts) {
    val style = LocalStyle.current
    val colors = MaterialTheme.colorScheme
    val event = anniversary.event
    val legend = if (isToday) {
        stringResource(Res.string.kind_and_date, kindLabel(event.kind), dates.long(anniversary.eventDate))
    } else {
        pluralStringResource(Res.plurals.will_turn, anniversary.years, anniversary.years)
    }
    val description = pluralStringResource(
        Res.plurals.a11y_anniversary, anniversary.years, anniversary.years,
        event.title("fr"), stringResource(Res.string.kind_and_date, kindLabel(event.kind), dates.long(anniversary.eventDate)),
    )
    Box(Modifier.padding(top = 14.dp, bottom = 6.dp).clearAndSetSemantics { contentDescription = description }) {
        Column(
            Modifier
                .widthIn(max = 340.dp)
                .rotate(-2.5f)
                .shadow(10.dp, RoundedCornerShape(2.dp))
                .background(colors.surface)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        ) {
            Column(
                Modifier.fillMaxWidth().height(236.dp).background(style.photoColor(event.family)).padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(Res.string.family_and_year, familyLabel(event.family).uppercase(), anniversary.eventDate.year),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF3A2A12),
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(anniversary.years.toString(), fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 120.sp, lineHeight = 110.sp, color = Color(0xFFFFFDF6))
                    Text(
                        pluralStringResource(Res.plurals.years_unit, anniversary.years),
                        fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 44.sp, color = Color(0xFFFFFDF6),
                        modifier = Modifier.padding(start = 8.dp, bottom = 18.dp),
                    )
                }
            }
            Column(Modifier.padding(top = 12.dp, bottom = 18.dp, start = 4.dp, end = 4.dp)) {
                Text(event.title("fr"), fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 38.sp, color = colors.onSurface)
                Text(legend, fontFamily = style.handwriting, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = colors.onSurfaceVariant)
            }
        }
        // Tape holding the photo.
        Box(Modifier.align(Alignment.TopCenter).offset(y = (-12).dp).rotate(3f).size(width = 86.dp, height = 26.dp).background(Color(0xD9F0E2B4)))
        if (anniversary.isRound) {
            Surface(
                color = colors.secondaryContainer,
                shape = RoundedCornerShape(50),
                // Sits on the bottom edge of the photo, clear of the handwritten legend.
                modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 22.dp).rotate(4f),
            ) {
                Text(
                    stringResource(Res.string.round_chip),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
            }
        }
    }
}

@Composable
private fun Lists(shock: DailyShock, dates: DateTexts) {
    if (shock.alsoToday.isNotEmpty()) {
        SectionTitle(stringResource(Res.string.also_today))
        shock.alsoToday.forEach { AnniversaryCard(it, dates) }
    }
    if (shock.upcoming.isNotEmpty()) {
        SectionTitle(stringResource(Res.string.upcoming))
        shock.upcoming.forEach { UpcomingDaySection(it, dates) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontFamily = LocalStyle.current.handwriting,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.semantics { heading() },
    )
}

@Composable
private fun UpcomingDaySection(day: UpcomingDay, dates: DateTexts) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(Res.string.day_and_delay, dates.dayAndMonth(day.date).capitalized(), pluralStringResource(Res.plurals.in_days, day.inDays, day.inDays)),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        day.anniversaries.forEach { AnniversaryCard(it, dates) }
    }
}

@Composable
private fun AnniversaryCard(anniversary: Anniversary, dates: DateTexts) {
    val style = LocalStyle.current
    val colors = MaterialTheme.colorScheme
    val event = anniversary.event
    val description = pluralStringResource(
        Res.plurals.a11y_anniversary, anniversary.years, anniversary.years,
        event.title("fr"), stringResource(Res.string.kind_and_date, kindLabel(event.kind), dates.long(anniversary.eventDate)),
    )
    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(6.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = description },
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                pluralStringResource(Res.plurals.years_count, anniversary.years, anniversary.years),
                fontFamily = style.handwriting, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = colors.primary,
                maxLines = 1, softWrap = false, modifier = Modifier.widthIn(min = 76.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(event.title("fr"), style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    stringResource(Res.string.kind_and_year, kindLabel(event.kind), anniversary.eventDate.year),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}
