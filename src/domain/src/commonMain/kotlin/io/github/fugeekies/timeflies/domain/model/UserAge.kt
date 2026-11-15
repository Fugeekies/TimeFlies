package io.github.fugeekies.timeflies.domain.model

/** How old a person was on the date of an event (docs/UISpec/Today.template.md, decisions of 15/11/2026). */
sealed interface UserAge {
    /** "Tu avais −3 ans." (decision of 15/11/2026): before the birth, [years] is minus the age in completed years, at least 1. */
    data class BeforeBirth(val years: Int) : UserAge

    /** "C'était l'année de ta naissance.": in the birth year, after the birth. */
    data object BirthYear : UserAge

    /** "Tu avais moins d'un an.": after the birth year, but less than a year old. */
    data object UnderOne : UserAge

    /** "Tu avais 20 ans.": [years] completed years, at least 1. */
    data class Years(val years: Int) : UserAge
}
