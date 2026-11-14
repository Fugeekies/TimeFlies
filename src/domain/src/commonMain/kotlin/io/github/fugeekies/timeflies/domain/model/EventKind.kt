package io.github.fugeekies.timeflies.domain.model

/** Nature of an event, which gives the label shown under its title (docs/EVENTS_DATA.md, "Natures"). */
enum class EventKind(val id: String) {
    FILM_RELEASE("film-release"),
    SERIES_PREMIERE("series-premiere"),
    SERIES_FINALE("series-finale"),
    ALBUM_RELEASE("album-release"),
    SINGLE_RELEASE("single-release"),
    DEATH("death"),
    PRODUCT_LAUNCH("product-launch"),
    GAME_RELEASE("game-release"),
    SPORT("sport"),
    HISTORY("history"),
    ELECTION("election"),
    ;

    companion object {
        private val byId = entries.associateBy { it.id }

        /** The kind with this JSON identifier, or null if it is unknown. */
        fun fromId(id: String): EventKind? = byId[id]
    }
}
