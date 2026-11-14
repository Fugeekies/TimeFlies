package io.github.fugeekies.timeflies.domain.model

/**
 * Theme an event belongs to. Drives the theme filters of the Equivalences screen and the
 * split of the event files (one file per family, see docs/EVENTS_DATA.md).
 */
enum class Family(val fileName: String) {
    SERIES_TV("series.json"),
    CINEMA("cinema.json"),
    MUSIC("music.json"),
    SPORT("sport.json"),
    GAMES_TECH("games-tech.json"),
    HISTORY("history.json"),
}
