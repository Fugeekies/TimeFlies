package io.github.fugeekies.timeflies.ui.today

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.Event
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
import io.github.fugeekies.timeflies.ui.resources.vhs_play
import io.github.fugeekies.timeflies.ui.resources.will_turn
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.resources.years_unit
import io.github.fugeekies.timeflies.ui.text.DateTexts
import io.github.fugeekies.timeflies.ui.text.capitalized
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.kindLabel
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.text.userAgeText
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import io.github.fugeekies.timeflies.ui.theme.accent
import io.github.fugeekies.timeflies.ui.theme.rememberReducedMotion
import io.github.fugeekies.timeflies.ui.polaroid.PolaroidCard
import io.github.fugeekies.timeflies.ui.vhs.VhsTape
import io.github.fugeekies.timeflies.ui.vhs.VhsYellow
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/** A card touched: opens "Quel âge ?" on its event. */
private val LocalOpenEvent = staticCompositionLocalOf<(Event) -> Unit> { {} }

/** Whether the main photo develops this time, and what to call once it has (docs/UI_DESIGN_SPEC.md, "Animation"). */
private class Develop(val enabled: Boolean, val onDeveloped: () -> Unit)

private val LocalDevelop = staticCompositionLocalOf { Develop(false) {} }

/**
 * The Today screen (docs/UISpec/Today.template.md), Polaroïd style.
 *
 * @param onOpenEvent a card touched: "Quel âge ?" on its event.
 */
@Composable
fun TodayScreen(viewModel: TodayViewModel, onOpenEvent: (Event) -> Unit = {}, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // Asked before the events are loaded, so the answer is there when the photo shows (once a day, never when the
    // system asks for fewer animations).
    val reducedMotion = rememberReducedMotion()
    when (val current = state) {
        TodayUiState.Loading -> CenteredText(stringResource(Res.string.loading), modifier)
        TodayUiState.Error -> CenteredText(stringResource(Res.string.load_error), modifier)
        is TodayUiState.Loaded -> {
            val day = current.shock.today
            val develop = Develop(reducedMotion == false && viewModel.shouldDevelop(day)) { viewModel.onDeveloped(day) }
            CompositionLocalProvider(LocalOpenEvent provides onOpenEvent, LocalDevelop provides develop) {
                TodayContent(current.shock, modifier)
            }
        }
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
    val style = LocalStyle.current
    val vhs = style.displayStyle == DisplayStyle.VHS
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            stringResource(Res.string.app_name),
            style = style.accent(34f),
            color = if (vhs) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )
        if (vhs) {
            // The date is on the tape label: the header only plays the tape, as on the mock-up.
            Text(stringResource(Res.string.vhs_play), fontFamily = style.section, fontSize = 22.sp, color = VhsYellow, modifier = Modifier.clearAndSetSemantics {})
        } else {
            Text(today, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
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
    val userAge = userAgeText(anniversary.userAge)
    val description = describe(anniversary, dates, userAge)
    val open = LocalOpenEvent.current
    val develop = LocalDevelop.current
    val click = Modifier.clickable(role = Role.Button) { open(event) }
    when (style.displayStyle) {
        DisplayStyle.POLAROID -> PolaroidCard(
            anniversary, legend, userAge, description, click,
            develop = develop.enabled, onDeveloped = develop.onDeveloped,
        )
        DisplayStyle.VHS -> VhsTape(
            anniversary, dates.withDayName(anniversary.day).uppercase(), legend, userAge, description, click,
            rewind = develop.enabled, onRewound = develop.onDeveloped,
        )
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
    val style = LocalStyle.current
    when (style.displayStyle) {
        DisplayStyle.POLAROID -> Text(
            text,
            style = style.accent(26f),
            color = style.sectionColor,
            modifier = Modifier.semantics { heading() },
        )
        // "» BIENTÔT…" in the font of the tape counter, as on the mock-up; the chevrons are not read.
        DisplayStyle.VHS -> Text(
            "» ${text.uppercase()}",
            fontFamily = style.section,
            fontSize = 24.sp,
            color = style.sectionColor,
            modifier = Modifier.semantics { heading(); contentDescription = text },
        )
    }
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
    val userAge = userAgeText(anniversary.userAge)
    val description = describe(anniversary, dates, userAge)
    val open = LocalOpenEvent.current
    // Polaroïd: an off-white card with a light shadow. VHS: a dark card with a thin border, as on the mock-up.
    val vhs = style.displayStyle == DisplayStyle.VHS
    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(if (vhs) 10.dp else 6.dp),
        shadowElevation = if (vhs) 0.dp else 1.dp,
        border = if (vhs) BorderStroke(1.dp, colors.outline) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { open(event) }
            .clearAndSetSemantics {
                contentDescription = description
                onClick { open(event); true }
            },
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                pluralStringResource(Res.plurals.years_count, anniversary.years, anniversary.years),
                style = style.accent(30f), color = colors.primary,
                maxLines = 1, softWrap = false, modifier = Modifier.widthIn(min = 76.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(event.title("fr"), style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    stringResource(Res.string.kind_and_year, kindLabel(event.kind), anniversary.eventDate.year),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
                if (userAge != null) {
                    Text(userAge, style = MaterialTheme.typography.bodySmall, color = colors.onSurface)
                }
            }
        }
    }
}

/** What screen readers say for an anniversary, with the user's age when known. */
@Composable
private fun describe(anniversary: Anniversary, dates: DateTexts, userAge: String?): String {
    val event = anniversary.event
    val base = pluralStringResource(
        Res.plurals.a11y_anniversary, anniversary.years, anniversary.years,
        event.title("fr"), stringResource(Res.string.kind_and_date, kindLabel(event.kind), dates.long(anniversary.eventDate)),
    )
    return if (userAge == null) base else "$base $userAge"
}
