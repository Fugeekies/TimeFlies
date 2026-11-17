package io.github.fugeekies.timeflies.ui.text

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import java.util.Locale

actual object AppLocale {
    /** The locale of Windows, kept before the app changes the default one. */
    private val system: Locale = Locale.getDefault()

    actual fun systemTag(): String = system.toLanguageTag()

    actual fun set(language: AppLanguage) {
        if (Locale.getDefault().language != language.tag) Locale.setDefault(Locale.forLanguageTag(language.tag))
    }
}
