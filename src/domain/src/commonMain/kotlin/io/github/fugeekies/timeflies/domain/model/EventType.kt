package io.github.fugeekies.timeflies.domain.model

/**
 * Broad type used to pair equivalences: two events are paired only if they share the same type
 * (decision of 13/11/2026, docs/EVENTS_DATA.md, "Types").
 */
enum class EventType(val id: String, val family: Family) {
    // Series & TV
    SITCOM_HIT("sitcom-hit", Family.SERIES_TV),
    DRAMA_SERIES_HIT("drama-series-hit", Family.SERIES_TV),
    FRENCH_SERIES_HIT("french-series-hit", Family.SERIES_TV),
    REALITY_TV("reality-tv", Family.SERIES_TV),
    KIDS_TV_SHOW("kids-tv-show", Family.SERIES_TV),
    CARTOON_ON_TV("cartoon-on-tv", Family.SERIES_TV),

    // Cinema
    BLOCKBUSTER("blockbuster", Family.CINEMA),
    ANIMATED_FEATURE("animated-feature", Family.CINEMA),
    FRENCH_COMEDY_HIT("french-comedy-hit", Family.CINEMA),
    SAGA_EPISODE("saga-episode", Family.CINEMA),

    // Music
    MUSIC_ICON_DEATH("music-icon-death", Family.MUSIC),
    DIVA_HIT("diva-hit", Family.MUSIC),
    BOY_BAND_HIT("boy-band-hit", Family.MUSIC),
    RAP_ALBUM_HIT("rap-album-hit", Family.MUSIC),
    SUMMER_HIT("summer-hit", Family.MUSIC),
    POP_ALBUM_HIT("pop-album-hit", Family.MUSIC),

    // Sport
    FOOTBALL_STAR_PEAK("football-star-peak", Family.SPORT),
    WORLD_CUP_WIN_FRANCE("world-cup-win-france", Family.SPORT),
    OLYMPICS_IN_FRANCE("olympics-in-france", Family.SPORT),
    SPORTS_RECORD("sports-record", Family.SPORT),

    // Video games & tech
    NINTENDO_CONSOLE("nintendo-console", Family.GAMES_TECH),
    PLAYSTATION_CONSOLE("playstation-console", Family.GAMES_TECH),
    HANDHELD_CONSOLE("handheld-console", Family.GAMES_TECH),
    VIDEO_GAME_PHENOMENON("video-game-phenomenon", Family.GAMES_TECH),
    PHONE_LAUNCH("phone-launch", Family.GAMES_TECH),
    SOCIAL_NETWORK_LAUNCH("social-network-launch", Family.GAMES_TECH),
    MUSIC_FORMAT("music-format", Family.GAMES_TECH),

    // Politics & history
    FRENCH_PRESIDENT_ELECTED("french-president-elected", Family.HISTORY),
    US_PRESIDENT_ELECTED("us-president-elected", Family.HISTORY),
    ACTOR_TURNED_POLITICIAN("actor-turned-politician", Family.HISTORY),
    WORLD_EVENT("world-event", Family.HISTORY),
    ROYAL_EVENT("royal-event", Family.HISTORY),
    ;

    companion object {
        private val byId = entries.associateBy { it.id }

        /** The type with this JSON identifier, or null if it is unknown. */
        fun fromId(id: String): EventType? = byId[id]
    }
}
