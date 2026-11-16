package io.github.fugeekies.timeflies.domain.model

/**
 * The file an event belongs to, and the unit of the event filter (decision of 16/11/2026, docs/EVENTS_DATA.md):
 * `<family>/<type>/<origin>` or `<family>/<type>/<origin>/<subcategory>`, for example `music/rap-album-hit/us` or
 * `music/music-icon-death/fr/chanson`.
 *
 * @property origin the country of the artist, the player, the studio, as an ISO 3166-1 code in lower case ("fr",
 *   "us"), or [INTERNATIONAL] for the few very big events that mix countries.
 * @property subcategory a finer split when it makes sense: a music genre ("rap", "chanson"), a sport discipline
 *   ("athletisme"); null otherwise.
 */
data class EventGroup(val type: EventType, val origin: String, val subcategory: String? = null) {
    val family: Family get() = type.family

    /** "music/music-icon-death/us/rap": the path of the file, without ".json", and the id kept in the settings. */
    val path: String get() = listOfNotNull(family.id, type.id, origin, subcategory).joinToString("/")

    companion object {
        const val INTERNATIONAL = "international"

        private val ORIGIN = Regex("^[a-z]{2}$")
        private val SUBCATEGORY = Regex("^[a-z]+(-[a-z]+)*$")

        /**
         * The group of a file path ("music/rap-album-hit/us"), or the reasons why it is not one: unknown family or
         * type, type of another family, origin that is not a country code nor "international", invalid subcategory.
         */
        fun parse(path: String): Result {
            val parts = path.split('/')
            if (parts.size !in 3..4) return Result.Invalid(listOf("chemin « $path » : famille/type/origine[/sous-catégorie] attendu"))
            val reasons = mutableListOf<String>()
            val family = Family.fromId(parts[0]) ?: null.also { reasons += "famille « ${parts[0]} » inconnue" }
            val type = EventType.fromId(parts[1]) ?: null.also { reasons += "type « ${parts[1]} » inconnu" }
            if (family != null && type != null && type.family != family) reasons += "type « ${type.id} » rangé dans la famille « ${family.id} »"
            val origin = parts[2]
            if (origin != INTERNATIONAL && !ORIGIN.matches(origin)) reasons += "origine « $origin » : code de pays en minuscules ou « international »"
            val subcategory = parts.getOrNull(3)
            if (subcategory != null && !SUBCATEGORY.matches(subcategory)) reasons += "sous-catégorie « $subcategory » : minuscules et tirets seulement"
            if (reasons.isNotEmpty()) return Result.Invalid(reasons)
            return Result.Valid(EventGroup(type!!, origin, subcategory))
        }
    }

    sealed interface Result {
        data class Valid(val group: EventGroup) : Result
        data class Invalid(val reasons: List<String>) : Result
    }
}

/** A group of the base with its number of valid events, for the filter of Settings and of the first launch. */
data class EventGroupCount(val group: EventGroup, val count: Int)
