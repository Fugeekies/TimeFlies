package io.github.fugeekies.timeflies.data.personal

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import io.github.fugeekies.timeflies.domain.port.PersonalEventRepository
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path

/**
 * The user's own events in `personal-events.json` through DataStore, next to `settings.json` (decision of 19/11/2026):
 * atomic writes, exposed as a [Flow]. A file that cannot be read is replaced by an empty list, as the settings are.
 *
 * Only one instance may use a given [file] in a process (a DataStore rule): the app's composition root builds it once.
 *
 * @param onCorruption called when an unreadable file is replaced, for the logs.
 * @param random makes the ids; fixed in the tests.
 */
class DataStorePersonalEventRepository(
    fileSystem: FileSystem,
    file: Path,
    scope: CoroutineScope? = null,
    onCorruption: (Throwable) -> Unit = {},
    private val random: Random = Random.Default,
) : PersonalEventRepository {

    private val store: DataStore<PersonalEventsDto> = run {
        val storage = OkioStorage(fileSystem, PersonalEventsSerializer, producePath = { file })
        val corruptionHandler = ReplaceFileCorruptionHandler<PersonalEventsDto> { error ->
            onCorruption(error)
            PersonalEventsDto()
        }
        if (scope == null) {
            DataStoreFactory.create(storage, corruptionHandler)
        } else {
            DataStoreFactory.create(storage, corruptionHandler, scope = scope)
        }
    }

    override val events: Flow<List<PersonalEvent>> = store.data.map { dto -> dto.toDomain() }.distinctUntilChanged()

    override suspend fun add(title: String, date: LocalDate) = addAll(listOf(title to date))

    override suspend fun addAll(events: List<Pair<String, LocalDate>>) {
        store.updateData { current ->
            val used = current.events.mapNotNullTo(HashSet()) { it.id }
            val added = events.map { (title, date) -> PersonalEventDto(newId(used).also { used += it }, title, date.toString()) }
            current.copy(version = PersonalEventsDto.CURRENT_VERSION, events = current.events + added)
        }
    }

    override suspend fun update(id: String, title: String, date: LocalDate) {
        store.updateData { current ->
            current.copy(events = current.events.map { if (it.id == id) it.copy(title = title, date = date.toString()) else it })
        }
    }

    override suspend fun remove(id: String) {
        store.updateData { current -> current.copy(events = current.events.filterNot { it.id == id }) }
    }

    override suspend fun encode(): String = encodeExport(events.first())

    override fun decode(text: String): PersonalEventRepository.Decoded? = decodeExport(text)

    private fun newId(used: Set<String>): String {
        while (true) {
            val id = ID_PREFIX + random.nextLong().toULong().toString(16).padStart(16, '0')
            if (id !in used) return id
        }
    }

    internal companion object {
        const val ID_PREFIX = "personal-"
    }
}

internal val personalJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
    prettyPrint = true
}

/** The valid entries of a stored file, sorted by date then title; entries without a readable date or id are dropped. */
internal fun PersonalEventsDto.toDomain(): List<PersonalEvent> =
    events.mapNotNull { dto ->
        val id = dto.id ?: return@mapNotNull null
        val date = parseDate(dto.date) ?: return@mapNotNull null
        PersonalEvent(id, dto.title, date)
    }.sortedWith(compareBy<PersonalEvent> { it.date }.thenBy { it.title }.thenBy { it.id })

/** The text of the exported file: the events sorted by date, without their ids. */
internal fun encodeExport(events: List<PersonalEvent>): String = personalJson.encodeToString(
    PersonalEventsDto.serializer(),
    PersonalEventsDto(events = events.map { PersonalEventDto(id = null, title = it.title, date = it.date.toString()) }),
)

/** The events of an exported (or stored) file, or null when it is not one this version reads. */
internal fun decodeExport(text: String): PersonalEventRepository.Decoded? {
    val dto = try {
        personalJson.decodeFromString(PersonalEventsDto.serializer(), text)
    } catch (e: IllegalArgumentException) {
        // SerializationException (malformed JSON, wrong type) is an IllegalArgumentException.
        return null
    }
    if (dto.format != PersonalEventsDto.FORMAT || dto.version !in 1..PersonalEventsDto.CURRENT_VERSION) return null
    var unreadable = 0
    val events = dto.events.mapNotNull { entry ->
        parseDate(entry.date)?.let { entry.title to it } ?: null.also { unreadable++ }
    }
    return PersonalEventRepository.Decoded(events, unreadable)
}

private fun parseDate(text: String): LocalDate? = try {
    LocalDate.parse(text)
} catch (e: IllegalArgumentException) {
    null
}

internal object PersonalEventsSerializer : OkioSerializer<PersonalEventsDto> {
    override val defaultValue: PersonalEventsDto = PersonalEventsDto()

    override suspend fun readFrom(source: BufferedSource): PersonalEventsDto =
        try {
            personalJson.decodeFromString(PersonalEventsDto.serializer(), source.readUtf8())
        } catch (e: IllegalArgumentException) {
            throw CorruptionException("personal-events.json cannot be read", e)
        }

    override suspend fun writeTo(t: PersonalEventsDto, sink: BufferedSink) {
        sink.writeUtf8(personalJson.encodeToString(PersonalEventsDto.serializer(), t))
    }
}
