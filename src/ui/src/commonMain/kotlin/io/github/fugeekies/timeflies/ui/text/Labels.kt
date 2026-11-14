package io.github.fugeekies.timeflies.ui.text

import androidx.compose.runtime.Composable
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.days
import io.github.fugeekies.timeflies.ui.resources.family_cinema
import io.github.fugeekies.timeflies.ui.resources.family_games_tech
import io.github.fugeekies.timeflies.ui.resources.family_history
import io.github.fugeekies.timeflies.ui.resources.family_music
import io.github.fugeekies.timeflies.ui.resources.family_series_tv
import io.github.fugeekies.timeflies.ui.resources.family_sport
import io.github.fugeekies.timeflies.ui.resources.first_of_month
import io.github.fugeekies.timeflies.ui.resources.kind_album_release
import io.github.fugeekies.timeflies.ui.resources.kind_death
import io.github.fugeekies.timeflies.ui.resources.kind_election
import io.github.fugeekies.timeflies.ui.resources.kind_film_release
import io.github.fugeekies.timeflies.ui.resources.kind_game_release
import io.github.fugeekies.timeflies.ui.resources.kind_history
import io.github.fugeekies.timeflies.ui.resources.kind_product_launch
import io.github.fugeekies.timeflies.ui.resources.kind_series_finale
import io.github.fugeekies.timeflies.ui.resources.kind_series_premiere
import io.github.fugeekies.timeflies.ui.resources.kind_single_release
import io.github.fugeekies.timeflies.ui.resources.kind_sport
import io.github.fugeekies.timeflies.ui.resources.months
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/** Label shown under an event title (docs/EVENTS_DATA.md, "Natures"). */
@Composable
fun kindLabel(kind: EventKind): String = stringResource(
    when (kind) {
        EventKind.FILM_RELEASE -> Res.string.kind_film_release
        EventKind.SERIES_PREMIERE -> Res.string.kind_series_premiere
        EventKind.SERIES_FINALE -> Res.string.kind_series_finale
        EventKind.ALBUM_RELEASE -> Res.string.kind_album_release
        EventKind.SINGLE_RELEASE -> Res.string.kind_single_release
        EventKind.DEATH -> Res.string.kind_death
        EventKind.PRODUCT_LAUNCH -> Res.string.kind_product_launch
        EventKind.GAME_RELEASE -> Res.string.kind_game_release
        EventKind.SPORT -> Res.string.kind_sport
        EventKind.HISTORY -> Res.string.kind_history
        EventKind.ELECTION -> Res.string.kind_election
    },
)

@Composable
fun familyLabel(family: Family): String = stringResource(familyResource(family))

private fun familyResource(family: Family): StringResource = when (family) {
    Family.SERIES_TV -> Res.string.family_series_tv
    Family.CINEMA -> Res.string.family_cinema
    Family.MUSIC -> Res.string.family_music
    Family.SPORT -> Res.string.family_sport
    Family.GAMES_TECH -> Res.string.family_games_tech
    Family.HISTORY -> Res.string.family_history
}

/**
 * Long dates in the interface language: "2 novembre 2011", "lundi 2 novembre 2026", "1er mai 1991".
 * Names come from the string resources, so the same code serves every language (docs/ARCHITECTURE.md).
 */
class DateTexts(private val months: List<String>, private val days: List<String>, private val firstOfMonth: String) {
    fun long(date: LocalDate): String = "${dayNumber(date)} ${months[date.month.number - 1]} ${date.year}"

    fun dayAndMonth(date: LocalDate): String = "${dayName(date)} ${dayNumber(date)} ${months[date.month.number - 1]}"

    fun withDayName(date: LocalDate): String = "${dayName(date)} ${long(date)}"

    private fun dayName(date: LocalDate) = days[date.dayOfWeek.isoDayNumber - 1]

    private fun dayNumber(date: LocalDate) = if (date.day == 1) firstOfMonth else date.day.toString()
}

@Composable
fun rememberDateTexts(): DateTexts = DateTexts(
    months = stringArrayResource(Res.array.months),
    days = stringArrayResource(Res.array.days),
    firstOfMonth = stringResource(Res.string.first_of_month),
)

/** Capitalises the first letter, for a date at the start of a line ("Lundi 2 novembre"). */
fun String.capitalized(): String = replaceFirstChar { it.titlecase() }
