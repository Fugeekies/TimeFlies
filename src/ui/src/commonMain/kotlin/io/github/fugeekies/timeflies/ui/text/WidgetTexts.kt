package io.github.fugeekies.timeflies.ui.text

import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.rules.WidgetSheets
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.days
import io.github.fugeekies.timeflies.ui.resources.first_of_month
import io.github.fugeekies.timeflies.ui.resources.in_days
import io.github.fugeekies.timeflies.ui.resources.kind_and_year
import io.github.fugeekies.timeflies.ui.resources.months
import io.github.fugeekies.timeflies.ui.resources.nothing_soon
import io.github.fugeekies.timeflies.ui.resources.widget_compact
import io.github.fugeekies.timeflies.ui.resources.widget_next
import io.github.fugeekies.timeflies.ui.resources.widget_open_app
import io.github.fugeekies.timeflies.ui.resources.years_count
import kotlinx.datetime.daysUntil
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

/**
 * One sheet of the Android widget (docs/UISpec/AndroidWidget.template.md, "Contenu").
 *
 * @property eyebrow "LUNDI 2 NOVEMBRE", or "DANS 3 JOURS" when nothing is today.
 * @property years "15 ans", written by hand on the little Polaroïd.
 * @property detail "Sortie au cinéma · 2011".
 * @property userAge "Tu avais 20 ans.", or null without the birth month and year.
 * @property eventYear year of the event, for the decade colour of the frame.
 * @property compact the two following anniversaries in one line each (4 × 3 and larger).
 */
data class WidgetSheet(
    val eyebrow: String,
    val years: String,
    val title: String,
    val detail: String,
    val userAge: String?,
    val eventYear: Int,
    val compact: List<String>,
)

/**
 * Everything the widget shows, computed once per update so the "next" button needs no new computation.
 *
 * @property sheets the anniversaries the "next" button goes through ([WidgetSheets.of]); empty with a [message].
 * @property message "Ouvre TimeFlies pour commencer." before the first launch, or the line of the Today screen
 *   when nothing comes in the next 7 days.
 * @property nextLabel spoken name of the "next" button.
 */
data class WidgetContent(val sheets: List<WidgetSheet>, val message: String?, val nextLabel: String)

/** Texts of the Android widget, read outside of Compose: Glance has no access to the Compose resources. */
object WidgetTexts {
    suspend fun of(shock: DailyShock, onboardingDone: Boolean): WidgetContent {
        val nextLabel = getString(Res.string.widget_next)
        if (!onboardingDone) return WidgetContent(emptyList(), getString(Res.string.widget_open_app), nextLabel)
        val sheets = WidgetSheets.of(shock)
        if (sheets.isEmpty()) return WidgetContent(emptyList(), getString(Res.string.nothing_soon), nextLabel)
        val dates = DateTexts(getStringArray(Res.array.months), getStringArray(Res.array.days), getString(Res.string.first_of_month))
        val eyebrow = if (shock.mainInDays == 0) dates.dayAndMonth(shock.today) else ShockMessages.inDays(shock.mainInDays)
        return WidgetContent(
            sheets = sheets.mapIndexed { index, anniversary ->
                WidgetSheet(
                    eyebrow = eyebrow.uppercase(),
                    years = years(anniversary),
                    title = anniversary.event.title("fr"),
                    detail = getString(Res.string.kind_and_year, getString(kindResource(anniversary.event.kind)), anniversary.eventDate.year),
                    userAge = ShockMessages.userAge(anniversary.userAge),
                    eventYear = anniversary.eventDate.year,
                    compact = WidgetSheets.following(shock, index).map { compact(it, shock, sheets.first()) },
                )
            },
            message = null,
            nextLabel = nextLabel,
        )
    }

    private suspend fun years(anniversary: Anniversary) = getPluralString(Res.plurals.years_count, anniversary.years, anniversary.years)

    /** "20 ans · Matrix", with "dans 2 jours · " in front when it is not on the day of the sheets. */
    private suspend fun compact(anniversary: Anniversary, shock: DailyShock, first: Anniversary): String {
        val line = getString(Res.string.widget_compact, years(anniversary), anniversary.event.title("fr"))
        if (anniversary.day == first.day) return line
        val inDays = shock.today.daysUntil(anniversary.day)
        return getString(Res.string.widget_compact, getPluralString(Res.plurals.in_days, inDays, inDays), line)
    }
}
