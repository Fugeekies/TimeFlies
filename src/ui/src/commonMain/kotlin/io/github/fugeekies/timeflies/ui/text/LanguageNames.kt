package io.github.fugeekies.timeflies.ui.text

import io.github.fugeekies.timeflies.domain.model.AppLanguage

/** Each language written in its own language, the same in every translation (docs/UISpec/Settings.template.md). */
val AppLanguage.nativeName: String
    get() = when (this) {
        AppLanguage.FRENCH -> "Français"
        AppLanguage.ENGLISH -> "English"
        AppLanguage.GERMAN -> "Deutsch"
        AppLanguage.ITALIAN -> "Italiano"
        AppLanguage.SPANISH -> "Español"
    }
