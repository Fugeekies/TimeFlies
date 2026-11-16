package io.github.fugeekies.timeflies.ui.text

import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.domain.model.DailyShock
import io.github.fugeekies.timeflies.domain.model.UserAge
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.app_name
import io.github.fugeekies.timeflies.ui.resources.days
import io.github.fugeekies.timeflies.ui.resources.first_of_month
import io.github.fugeekies.timeflies.ui.resources.in_days
import io.github.fugeekies.timeflies.ui.resources.months
import io.github.fugeekies.timeflies.ui.resources.nav_settings
import io.github.fugeekies.timeflies.ui.resources.nothing_soon
import io.github.fugeekies.timeflies.ui.resources.notification_event
import io.github.fugeekies.timeflies.ui.resources.notification_title_today
import io.github.fugeekies.timeflies.ui.resources.notification_upcoming_title
import io.github.fugeekies.timeflies.ui.resources.tray_daily_shock
import io.github.fugeekies.timeflies.ui.resources.tray_hint
import io.github.fugeekies.timeflies.ui.resources.tray_open
import io.github.fugeekies.timeflies.ui.resources.tray_quit
import io.github.fugeekies.timeflies.ui.resources.tray_tooltip_today
import io.github.fugeekies.timeflies.ui.resources.tray_tooltip_upcoming
import io.github.fugeekies.timeflies.ui.resources.user_age_before_birth
import io.github.fugeekies.timeflies.ui.resources.user_age_birth_year
import io.github.fugeekies.timeflies.ui.resources.user_age_under_one
import io.github.fugeekies.timeflies.ui.resources.user_age_years
import io.github.fugeekies.timeflies.ui.resources.will_turn
import io.github.fugeekies.timeflies.ui.resources.years_count
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

/** A notification: a title and a text (docs/UISpec/WindowsTray.template.md, "Notification du matin"). */
data class ShockMessage(val title: String, val text: String)

/** Texts of the tray menu, in the order of docs/UISpec/WindowsTray.template.md. */
data class TrayMenuTexts(val open: String, val dailyShock: String, val settings: String, val quit: String)

/**
 * Texts of the Windows tray icon and notification, read outside of Compose (docs/UISpec/WindowsTray.template.md).
 * They live here because the string resources belong to the `ui` module.
 */
object ShockMessages {
    /** "TimeFlies — Intouchables : 15 ans aujourd'hui", "TimeFlies — Dans 3 jours : … fêtera ses 25 ans", or "TimeFlies". */
    suspend fun tooltip(shock: DailyShock): String {
        val main = shock.main ?: return getString(Res.string.app_name)
        val title = main.event.title("fr")
        return if (shock.mainInDays == 0) {
            getString(Res.string.tray_tooltip_today, title, getPluralString(Res.plurals.years_count, main.years, main.years))
        } else {
            getString(Res.string.tray_tooltip_upcoming, inDays(shock.mainInDays), title, willTurn(main))
        }
    }

    /**
     * Title "15 ans aujourd'hui" and text "Intouchables · Sortie au cinéma, 2 novembre 2011. Tu avais 20 ans.";
     * without today's anniversary, title "Dans 3 jours" and the next one. Nothing in the next 7 days: "TimeFlies"
     * and the line of the Today screen.
     */
    suspend fun notification(shock: DailyShock): ShockMessage {
        val main = shock.main ?: return ShockMessage(getString(Res.string.app_name), getString(Res.string.nothing_soon))
        val title = if (shock.mainInDays == 0) {
            getString(Res.string.notification_title_today, getPluralString(Res.plurals.years_count, main.years, main.years))
        } else {
            inDays(shock.mainInDays)
        }
        val eventTitle = if (shock.mainInDays == 0) {
            main.event.title("fr")
        } else {
            getString(Res.string.notification_upcoming_title, main.event.title("fr"), willTurn(main))
        }
        val event = getString(Res.string.notification_event, eventTitle, getString(kindResource(main.event.kind)), longDate(main))
        val text = listOfNotNull(event, userAge(main.userAge)).joinToString(" ")
        return ShockMessage(title, text)
    }

    /** Shown the first time the window is closed into the tray. */
    suspend fun trayHint(): ShockMessage = ShockMessage(getString(Res.string.app_name), getString(Res.string.tray_hint))

    suspend fun trayMenu(): TrayMenuTexts = TrayMenuTexts(
        open = getString(Res.string.tray_open),
        dailyShock = getString(Res.string.tray_daily_shock),
        settings = getString(Res.string.nav_settings),
        quit = getString(Res.string.tray_quit),
    )

    /** "Dans 3 jours", capitalised at the start of a title. */
    private suspend fun inDays(days: Int) = getPluralString(Res.plurals.in_days, days, days).capitalized()

    private suspend fun willTurn(anniversary: Anniversary) = getPluralString(Res.plurals.will_turn, anniversary.years, anniversary.years)

    private suspend fun longDate(anniversary: Anniversary): String =
        DateTexts(getStringArray(Res.array.months), getStringArray(Res.array.days), getString(Res.string.first_of_month)).long(anniversary.eventDate)

    private suspend fun userAge(age: UserAge?): String? = when (age) {
        null -> null
        is UserAge.Years -> getPluralString(Res.plurals.user_age_years, age.years, age.years)
        UserAge.UnderOne -> getString(Res.string.user_age_under_one)
        UserAge.BirthYear -> getString(Res.string.user_age_birth_year)
        is UserAge.BeforeBirth -> getPluralString(Res.plurals.user_age_before_birth, age.years, "−${age.years}")
    }
}
