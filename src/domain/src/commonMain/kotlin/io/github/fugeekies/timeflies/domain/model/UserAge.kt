package io.github.fugeekies.timeflies.domain.model

/** How old the user was on the date of an event (docs/UISpec/Today.template.md, decision of 15/11/2026). */
sealed interface UserAge {
    /** "C'était 3 ans avant ta naissance." [years] is the difference of calendar years, at least 1. */
    data class BeforeBirth(val years: Int) : UserAge

    /** "C'était l'année de ta naissance.": the event happened in the user's birth year. */
    data object BirthYear : UserAge

    /** "Tu avais moins d'un an.": after the birth year, but less than a year old. */
    data object UnderOne : UserAge

    /** "Tu avais 20 ans.": [years] completed years, at least 1. */
    data class Years(val years: Int) : UserAge
}
