package io.github.fugeekies.timeflies.domain.model

/**
 * Theme an event belongs to. Drives the theme filters of the Equivalences screen and the first folder of the event
 * base (assets/events/<family>/…, docs/EVENTS_DATA.md).
 *
 * @property id the folder name: "series", "cinema", "games-tech"…
 */
enum class Family(val id: String) {
    SERIES_TV("series"),
    CINEMA("cinema"),
    MUSIC("music"),
    SPORT("sport"),
    GAMES_TECH("games-tech"),
    HISTORY("history"),
    ;

    companion object {
        fun fromId(id: String): Family? = entries.firstOrNull { it.id == id }
    }
}
