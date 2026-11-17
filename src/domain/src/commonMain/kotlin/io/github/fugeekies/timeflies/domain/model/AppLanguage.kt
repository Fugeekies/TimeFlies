package io.github.fugeekies.timeflies.domain.model

/**
 * A language of the interface (docs/UISpec/Settings.template.md, "Langue"; milestone 17, decision of 17/11/2026):
 * French, the reference, and the languages of the user guide.
 *
 * @property tag the ISO 639-1 code, written in `settings.json` and used to pick the translated texts.
 */
enum class AppLanguage(val tag: String) {
    FRENCH("fr"),
    ENGLISH("en"),
    GERMAN("de"),
    ITALIAN("it"),
    SPANISH("es");

    companion object {
        /** The language of [tag] ("de", "de-AT", "DE_ch"), or null when the interface is not translated into it. */
        fun of(tag: String): AppLanguage? {
            val language = tag.substringBefore('-').substringBefore('_').lowercase()
            return entries.firstOrNull { it.tag == language }
        }

        /** The language shown: the one chosen in Settings, else the system's when available, else French. */
        fun resolve(chosen: AppLanguage?, systemTag: String): AppLanguage = chosen ?: of(systemTag) ?: FRENCH
    }
}
