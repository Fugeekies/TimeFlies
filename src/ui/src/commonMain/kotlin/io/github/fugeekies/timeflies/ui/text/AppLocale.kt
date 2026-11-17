package io.github.fugeekies.timeflies.ui.text

import io.github.fugeekies.timeflies.domain.model.AppLanguage

/**
 * The language of the texts (docs/ARCHITECTURE.md, "Langues de l'interface"; milestone 17). The Compose resources
 * pick their translation from the default locale of the process, on screen as well as in the widget, the tray and
 * the notifications: changing it is enough, before the texts are read.
 */
expect object AppLocale {
    /** The language of the system, as a tag ("en-GB"), whatever the app has chosen. */
    fun systemTag(): String

    /** Makes [language] the default locale of the process. */
    fun set(language: AppLanguage)
}

/**
 * Applies the language chosen in Settings, or the system's when available, or French, and returns it. Cheap: called
 * before reading texts wherever they may be read first (the window, the widget, the tray).
 */
fun AppLocale.apply(chosen: AppLanguage?): AppLanguage = AppLanguage.resolve(chosen, systemTag()).also(::set)
