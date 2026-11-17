package io.github.fugeekies.timeflies.ui.text

import androidx.compose.ui.text.intl.Locale
import io.github.fugeekies.timeflies.domain.model.Event

/**
 * The title of the event in the language of the interface, French when it has none (docs/EVENTS_DATA.md, "title").
 * Read from the default locale, which [AppLocale] sets to the chosen language: on screen as in the widget, the tray
 * and the notifications.
 */
val Event.shownTitle: String get() = title(Locale.current.language)
