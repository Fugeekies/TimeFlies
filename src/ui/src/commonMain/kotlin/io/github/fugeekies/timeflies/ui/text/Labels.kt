package io.github.fugeekies.timeflies.ui.text

import androidx.compose.runtime.Composable
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.UserAge
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.date_day_and_month
import io.github.fugeekies.timeflies.ui.resources.date_long
import io.github.fugeekies.timeflies.ui.resources.date_short
import io.github.fugeekies.timeflies.ui.resources.date_with_day_name
import io.github.fugeekies.timeflies.ui.resources.days
import io.github.fugeekies.timeflies.ui.resources.family_cinema
import io.github.fugeekies.timeflies.ui.resources.family_contents_cinema
import io.github.fugeekies.timeflies.ui.resources.family_contents_games_tech
import io.github.fugeekies.timeflies.ui.resources.family_contents_history
import io.github.fugeekies.timeflies.ui.resources.family_contents_personal
import io.github.fugeekies.timeflies.ui.resources.family_contents_music
import io.github.fugeekies.timeflies.ui.resources.family_contents_series_tv
import io.github.fugeekies.timeflies.ui.resources.family_contents_sport
import io.github.fugeekies.timeflies.ui.resources.family_games_tech
import io.github.fugeekies.timeflies.ui.resources.family_history
import io.github.fugeekies.timeflies.ui.resources.family_personal
import io.github.fugeekies.timeflies.ui.resources.family_music
import io.github.fugeekies.timeflies.ui.resources.family_series_tv
import io.github.fugeekies.timeflies.ui.resources.family_sport
import io.github.fugeekies.timeflies.ui.resources.first_of_month
import io.github.fugeekies.timeflies.ui.resources.kind_album_release
import io.github.fugeekies.timeflies.ui.resources.kind_death
import io.github.fugeekies.timeflies.ui.resources.kind_election
import io.github.fugeekies.timeflies.ui.resources.kind_personal
import io.github.fugeekies.timeflies.ui.resources.kind_film_release
import io.github.fugeekies.timeflies.ui.resources.kind_game_release
import io.github.fugeekies.timeflies.ui.resources.kind_history
import io.github.fugeekies.timeflies.ui.resources.kind_product_launch
import io.github.fugeekies.timeflies.ui.resources.kind_series_finale
import io.github.fugeekies.timeflies.ui.resources.kind_series_premiere
import io.github.fugeekies.timeflies.ui.resources.kind_single_release
import io.github.fugeekies.timeflies.ui.resources.kind_sport
import io.github.fugeekies.timeflies.ui.resources.months
import io.github.fugeekies.timeflies.ui.resources.user_age_before_birth
import io.github.fugeekies.timeflies.ui.resources.user_age_birth_year
import io.github.fugeekies.timeflies.ui.resources.user_age_under_one
import io.github.fugeekies.timeflies.ui.resources.user_age_years

import io.github.fugeekies.timeflies.ui.resources.type_sitcom_hit
import io.github.fugeekies.timeflies.ui.resources.type_drama_series_hit
import io.github.fugeekies.timeflies.ui.resources.type_french_series_hit
import io.github.fugeekies.timeflies.ui.resources.type_reality_tv
import io.github.fugeekies.timeflies.ui.resources.type_kids_tv_show
import io.github.fugeekies.timeflies.ui.resources.type_cartoon_on_tv
import io.github.fugeekies.timeflies.ui.resources.type_blockbuster
import io.github.fugeekies.timeflies.ui.resources.type_animated_feature
import io.github.fugeekies.timeflies.ui.resources.type_french_comedy_hit
import io.github.fugeekies.timeflies.ui.resources.type_saga_episode
import io.github.fugeekies.timeflies.ui.resources.type_music_icon_death
import io.github.fugeekies.timeflies.ui.resources.type_diva_hit
import io.github.fugeekies.timeflies.ui.resources.type_boy_band_hit
import io.github.fugeekies.timeflies.ui.resources.type_rap_album_hit
import io.github.fugeekies.timeflies.ui.resources.type_summer_hit
import io.github.fugeekies.timeflies.ui.resources.type_pop_album_hit
import io.github.fugeekies.timeflies.ui.resources.type_football_star_peak
import io.github.fugeekies.timeflies.ui.resources.type_world_cup_win_france
import io.github.fugeekies.timeflies.ui.resources.type_olympics_in_france
import io.github.fugeekies.timeflies.ui.resources.type_sports_record
import io.github.fugeekies.timeflies.ui.resources.type_tennis_grand_slam
import io.github.fugeekies.timeflies.ui.resources.type_tour_de_france_win
import io.github.fugeekies.timeflies.ui.resources.type_world_cup_win
import io.github.fugeekies.timeflies.ui.resources.type_nintendo_console
import io.github.fugeekies.timeflies.ui.resources.type_playstation_console
import io.github.fugeekies.timeflies.ui.resources.type_handheld_console
import io.github.fugeekies.timeflies.ui.resources.type_video_game_phenomenon
import io.github.fugeekies.timeflies.ui.resources.type_phone_launch
import io.github.fugeekies.timeflies.ui.resources.type_social_network_launch
import io.github.fugeekies.timeflies.ui.resources.type_music_format
import io.github.fugeekies.timeflies.ui.resources.type_french_president_elected
import io.github.fugeekies.timeflies.ui.resources.type_us_president_elected
import io.github.fugeekies.timeflies.ui.resources.type_actor_turned_politician
import io.github.fugeekies.timeflies.ui.resources.type_world_event
import io.github.fugeekies.timeflies.ui.resources.type_royal_event
import io.github.fugeekies.timeflies.ui.resources.person_age_birth_year
import io.github.fugeekies.timeflies.ui.resources.person_age_under_one
import io.github.fugeekies.timeflies.ui.resources.years_count
import io.github.fugeekies.timeflies.ui.resources.years_signed
import io.github.fugeekies.timeflies.domain.model.EventType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/** Label shown under an event title (docs/EVENTS_DATA.md, "Natures"). */
@Composable
fun kindLabel(kind: EventKind): String = stringResource(kindResource(kind))

internal fun kindResource(kind: EventKind): StringResource =
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
        EventKind.PERSONAL -> Res.string.kind_personal
    }

/** "Tu avais 20 ans.", or null when the user's birth is unknown (the line is not shown). */
@Composable
fun userAgeText(age: UserAge?): String? = when (age) {
    null -> null
    is UserAge.Years -> pluralStringResource(Res.plurals.user_age_years, age.years, age.years)
    UserAge.UnderOne -> stringResource(Res.string.user_age_under_one)
    UserAge.BirthYear -> stringResource(Res.string.user_age_birth_year)
    // "Tu avais −3 ans.", with a real minus sign, as on "Quel âge ?" (decision of 15/11/2026).
    is UserAge.BeforeBirth -> pluralStringResource(Res.plurals.user_age_before_birth, age.years, "−${age.years}")
}

@Composable
fun familyLabel(family: Family): String = stringResource(familyResource(family))

/** What a theme holds, in a few words, on the first launch screen (decision of 17/11/2026): its kinds of events. */
@Composable
fun familyContents(family: Family): String = stringResource(
    when (family) {
        Family.SERIES_TV -> Res.string.family_contents_series_tv
        Family.CINEMA -> Res.string.family_contents_cinema
        Family.MUSIC -> Res.string.family_contents_music
        Family.SPORT -> Res.string.family_contents_sport
        Family.GAMES_TECH -> Res.string.family_contents_games_tech
        Family.HISTORY -> Res.string.family_contents_history
        Family.PERSONAL -> Res.string.family_contents_personal
    },
)

private fun familyResource(family: Family): StringResource = when (family) {
    Family.SERIES_TV -> Res.string.family_series_tv
    Family.CINEMA -> Res.string.family_cinema
    Family.MUSIC -> Res.string.family_music
    Family.SPORT -> Res.string.family_sport
    Family.GAMES_TECH -> Res.string.family_games_tech
    Family.HISTORY -> Res.string.family_history
    Family.PERSONAL -> Res.string.family_personal
}

/**
 * The order of the parts of a date in one language, from the string resources (milestone 17): `{d}` the day of the
 * month ("1er" for the first in French), `{m}` the month, `{y}` the year, `{w}` the day of the week, `{date}` the long
 * date, `{dd}` and `{mm}` the day and month on two digits.
 *
 * @property long "2 novembre 2011", "2 November 2011", "2. November 2011", "2 de noviembre de 2011".
 * @property dayAndMonth "lundi 2 novembre", "Montag, 2. November".
 * @property withDayName "lundi 2 novembre 2026", "Montag, 2. November 2026".
 * @property short the lists: "02/11/2011", "02.11.2011".
 */
data class DatePatterns(val long: String, val dayAndMonth: String, val withDayName: String, val short: String) {
    companion object {
        /** The French reference, the default of the tests. */
        val FRENCH = DatePatterns(long = "{d} {m} {y}", dayAndMonth = "{w} {d} {m}", withDayName = "{w} {date}", short = "{dd}/{mm}/{y}")
    }
}

/**
 * Dates in the interface language: "2 novembre 2011", "lundi 2 novembre 2026", "1er mai 1991". Names and the order of
 * the parts come from the string resources, so the same code serves every language (docs/ARCHITECTURE.md).
 */
class DateTexts(
    private val months: List<String>,
    private val days: List<String>,
    private val firstOfMonth: String,
    private val patterns: DatePatterns = DatePatterns.FRENCH,
) {
    fun long(date: LocalDate): String = fill(patterns.long, date)

    /** Short date of the lists: "02/11/2011" (docs/UI_DESIGN_SPEC.md, "Langues et formats"). */
    fun short(date: LocalDate): String = fill(patterns.short, date)

    fun dayAndMonth(date: LocalDate): String = fill(patterns.dayAndMonth, date)

    fun withDayName(date: LocalDate): String = fill(patterns.withDayName.replace("{date}", patterns.long), date)

    private fun fill(pattern: String, date: LocalDate): String = pattern
        .replace("{dd}", date.day.toString().padStart(2, '0'))
        .replace("{mm}", date.month.number.toString().padStart(2, '0'))
        .replace("{d}", if (date.day == 1) firstOfMonth else date.day.toString())
        .replace("{m}", months[date.month.number - 1])
        .replace("{w}", days[date.dayOfWeek.isoDayNumber - 1])
        .replace("{y}", date.year.toString())
}

@Composable
fun rememberDateTexts(): DateTexts = DateTexts(
    months = stringArrayResource(Res.array.months),
    days = stringArrayResource(Res.array.days),
    firstOfMonth = stringResource(Res.string.first_of_month),
    patterns = DatePatterns(
        long = stringResource(Res.string.date_long),
        dayAndMonth = stringResource(Res.string.date_day_and_month),
        withDayName = stringResource(Res.string.date_with_day_name),
        short = stringResource(Res.string.date_short),
    ),
)

/** The same outside a screen (widget, tray, notifications), in the current language. */
suspend fun loadDateTexts(): DateTexts = DateTexts(
    months = getStringArray(Res.array.months),
    days = getStringArray(Res.array.days),
    firstOfMonth = getString(Res.string.first_of_month),
    patterns = DatePatterns(
        long = getString(Res.string.date_long),
        dayAndMonth = getString(Res.string.date_day_and_month),
        withDayName = getString(Res.string.date_with_day_name),
        short = getString(Res.string.date_short),
    ),
)

/** Capitalises the first letter, for a date at the start of a line ("Lundi 2 novembre"). */
fun String.capitalized(): String = replaceFirstChar { it.titlecase() }

/** Name of a type in plain words, at the top of an equivalence card ("Victoire des Bleus en Coupe du monde"). */
@Composable
fun typeLabel(type: EventType): String = stringResource(
    when (type) {
        EventType.SITCOM_HIT -> Res.string.type_sitcom_hit
        EventType.DRAMA_SERIES_HIT -> Res.string.type_drama_series_hit
        EventType.FRENCH_SERIES_HIT -> Res.string.type_french_series_hit
        EventType.REALITY_TV -> Res.string.type_reality_tv
        EventType.KIDS_TV_SHOW -> Res.string.type_kids_tv_show
        EventType.CARTOON_ON_TV -> Res.string.type_cartoon_on_tv
        EventType.BLOCKBUSTER -> Res.string.type_blockbuster
        EventType.ANIMATED_FEATURE -> Res.string.type_animated_feature
        EventType.FRENCH_COMEDY_HIT -> Res.string.type_french_comedy_hit
        EventType.SAGA_EPISODE -> Res.string.type_saga_episode
        EventType.MUSIC_ICON_DEATH -> Res.string.type_music_icon_death
        EventType.DIVA_HIT -> Res.string.type_diva_hit
        EventType.BOY_BAND_HIT -> Res.string.type_boy_band_hit
        EventType.RAP_ALBUM_HIT -> Res.string.type_rap_album_hit
        EventType.SUMMER_HIT -> Res.string.type_summer_hit
        EventType.POP_ALBUM_HIT -> Res.string.type_pop_album_hit
        EventType.FOOTBALL_STAR_PEAK -> Res.string.type_football_star_peak
        EventType.WORLD_CUP_WIN_FRANCE -> Res.string.type_world_cup_win_france
        EventType.OLYMPICS_IN_FRANCE -> Res.string.type_olympics_in_france
        EventType.SPORTS_RECORD -> Res.string.type_sports_record
        EventType.WORLD_CUP_WIN -> Res.string.type_world_cup_win
        EventType.TENNIS_GRAND_SLAM -> Res.string.type_tennis_grand_slam
        EventType.TOUR_DE_FRANCE_WIN -> Res.string.type_tour_de_france_win
        EventType.NINTENDO_CONSOLE -> Res.string.type_nintendo_console
        EventType.PLAYSTATION_CONSOLE -> Res.string.type_playstation_console
        EventType.HANDHELD_CONSOLE -> Res.string.type_handheld_console
        EventType.VIDEO_GAME_PHENOMENON -> Res.string.type_video_game_phenomenon
        EventType.PHONE_LAUNCH -> Res.string.type_phone_launch
        EventType.SOCIAL_NETWORK_LAUNCH -> Res.string.type_social_network_launch
        EventType.MUSIC_FORMAT -> Res.string.type_music_format
        EventType.FRENCH_PRESIDENT_ELECTED -> Res.string.type_french_president_elected
        EventType.US_PRESIDENT_ELECTED -> Res.string.type_us_president_elected
        EventType.ACTOR_TURNED_POLITICIAN -> Res.string.type_actor_turned_politician
        EventType.WORLD_EVENT -> Res.string.type_world_event
        EventType.ROYAL_EVENT -> Res.string.type_royal_event
        // Never on an equivalence card; the same words as its kind.
        EventType.PERSONAL -> Res.string.kind_personal
    },
)

/** A person's age on the date of an event, after their pictogram: "6 ans", "moins d'un an", "−2 ans" before the birth. */
@Composable
fun personAgeText(age: UserAge): String = when (age) {
    is UserAge.Years -> pluralStringResource(Res.plurals.years_count, age.years, age.years)
    UserAge.UnderOne -> stringResource(Res.string.person_age_under_one)
    UserAge.BirthYear -> stringResource(Res.string.person_age_birth_year)
    is UserAge.BeforeBirth -> signedYearsText(-age.years)
}

/**
 * An age that is negative before a birth ("−14 ans", "6 ans"), with a real minus sign (decision of 15/11/2026).
 * The plural follows the absolute value.
 */
@Composable
fun signedYearsText(years: Int): String {
    val number = if (years < 0) "−${-years}" else years.toString()
    return pluralStringResource(Res.plurals.years_signed, kotlin.math.abs(years), number)
}
