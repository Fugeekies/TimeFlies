package io.github.fugeekies.timeflies.ui.text

import android.content.res.Resources
import android.os.LocaleList
import io.github.fugeekies.timeflies.domain.model.AppLanguage
import java.util.Locale

actual object AppLocale {
    /** Read from the system configuration each time: the user may change the phone's language while the app runs. */
    actual fun systemTag(): String = Resources.getSystem().configuration.locales[0].toLanguageTag()

    /** Compose reads the locale list, the resources outside Compose the default locale: both are set. */
    actual fun set(language: AppLanguage) {
        val locale = Locale.forLanguageTag(language.tag)
        if (Locale.getDefault().language != locale.language) Locale.setDefault(locale)
        if (LocaleList.getDefault()[0]?.language != locale.language) LocaleList.setDefault(LocaleList(locale))
    }
}
