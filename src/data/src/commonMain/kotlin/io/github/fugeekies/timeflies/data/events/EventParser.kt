package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.Event
import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.EventKind
import io.github.fugeekies.timeflies.domain.model.Region
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

/** Result of reading one file of the base: the valid events and the rejected entries. */
data class ParsedFile(val events: List<Event>, val rejections: List<Rejection>)

/**
 * Reads and validates one event file (docs/EVENTS_DATA.md, "Validation automatique"). Its path gives the family,
 * the type, the origin and the subcategory of every event in it.
 * An invalid entry is left out and reported; it never stops the other entries from loading.
 */
class EventParser(private val today: LocalDate) {

    /** @param file the path of the file in the base, without ".json" ("music/rap-album-hit/us"). */
    fun parse(file: String, bytes: ByteArray): ParsedFile {
        val group = when (val result = EventGroup.parse(file)) {
            is EventGroup.Result.Valid -> result.group
            is EventGroup.Result.Invalid -> return ParsedFile(emptyList(), listOf(Rejection(file, -1, null, result.reasons)))
        }
        val array = try {
            json.parseToJsonElement(bytes.decodeToString()) as? JsonArray
        } catch (e: SerializationException) {
            null
        } ?: return ParsedFile(emptyList(), listOf(Rejection(file, -1, null, listOf("le fichier n'est pas un tableau JSON valide"))))

        val events = mutableListOf<Event>()
        val rejections = mutableListOf<Rejection>()
        array.forEachIndexed { index, element ->
            val dto = try {
                json.decodeFromJsonElement(EventDto.serializer(), element)
            } catch (e: IllegalArgumentException) {
                // SerializationException (wrong type of a field) is an IllegalArgumentException.
                rejections += Rejection(file, index, null, listOf("entrée illisible : ${e.message?.lineSequence()?.firstOrNull()}"))
                return@forEachIndexed
            }
            when (val result = validate(group, dto)) {
                is Validation.Valid -> events += result.event
                is Validation.Invalid -> rejections += Rejection(file, index, dto.id, result.reasons)
            }
        }
        return ParsedFile(events, rejections)
    }

    private sealed interface Validation {
        data class Valid(val event: Event) : Validation
        data class Invalid(val reasons: List<String>) : Validation
    }

    private fun validate(group: EventGroup, dto: EventDto): Validation {
        val reasons = mutableListOf<String>()

        val id = dto.id
        if (id == null) reasons += "id manquant" else if (!ID_PATTERN.matches(id)) reasons += "id « $id » : minuscules, chiffres et tirets seulement"

        val date = dto.date.let { if (it == null) { reasons += "date manquante"; null } else parsePastDate(it, "date", reasons) }

        val regionalDates = buildMap {
            dto.regionalDates.forEach { (code, value) ->
                val region = Region.of(code)
                if (region == null) reasons += "regionalDates : code pays « $code » invalide (deux lettres majuscules)"
                val regionalDate = parsePastDate(value, "regionalDates.$code", reasons)
                if (region != null && regionalDate != null) put(region, regionalDate)
            }
        }

        val titles = dto.title.filterValues { it.isNotBlank() }
        if (titles[Event.FRENCH] == null) reasons += "title.fr manquant ou vide"
        // Only the languages of the interface (docs/EVENTS_DATA.md, "title"): a typo ("fr-FR", "ENG") would never show.
        val unknownLanguages = dto.title.keys.filter { AppLanguage.of(it)?.tag != it }
        if (unknownLanguages.isNotEmpty()) reasons += "title : langue inconnue ${unknownLanguages.joinToString()} (fr, en, de, it, es)"

        val kind = dto.kind.let { if (it == null) { reasons += "kind manquant"; null } else EventKind.fromId(it) ?: run { reasons += "kind « $it » inconnu"; null } }

        // The type comes from the folder; one still written in the entry must agree with it.
        dto.type?.let { if (it != group.type.id) reasons += "type « $it » rangé dans le dossier « ${group.type.id} »" }

        val importance = dto.importance
        if (importance == null) reasons += "importance manquante" else if (importance !in 1..3) reasons += "importance $importance hors de 1 à 3"

        val source = dto.source
        if (source == null || !source.startsWith("https://")) reasons += "source manquante ou pas en https://"

        if (reasons.isNotEmpty()) return Validation.Invalid(reasons)
        return Validation.Valid(
            Event(
                id = id!!,
                date = date!!,
                regionalDates = regionalDates,
                titles = titles,
                kind = kind!!,
                group = group,
                importance = importance!!,
                source = source!!,
            ),
        )
    }

    private fun parsePastDate(value: String, field: String, reasons: MutableList<String>): LocalDate? {
        if (!DATE_PATTERN.matches(value)) {
            reasons += "$field « $value » : format AAAA-MM-JJ attendu"
            return null
        }
        val date = try {
            LocalDate.parse(value)
        } catch (e: IllegalArgumentException) {
            reasons += "$field « $value » : date inexistante"
            return null
        }
        if (date >= today) {
            reasons += "$field « $value » : pas dans le passé"
            return null
        }
        return date
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
        val ID_PATTERN = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")
        val DATE_PATTERN = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    }
}
