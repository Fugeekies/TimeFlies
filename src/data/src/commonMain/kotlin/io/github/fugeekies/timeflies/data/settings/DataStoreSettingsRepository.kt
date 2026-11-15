package io.github.fugeekies.timeflies.data.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path

/**
 * Settings in a JSON file through DataStore, the same on Android and Windows (docs/ARCHITECTURE.md, "Réglages"):
 * atomic writes, exposed as a [Flow]. A file that cannot be read is replaced by the defaults.
 *
 * Only one instance may use a given [file] in a process (a DataStore rule): the app's composition root builds it once.
 *
 * @param fileSystem the system file system, handed by the app ([FileSystem.SYSTEM] on both platforms).
 * @param file `settings.json` in the app's private folder (Android) or in `%APPDATA%\TimeFlies` (Windows).
 * @param scope where DataStore runs its writes; DataStore's own I/O scope when null.
 * @param onCorruption called when an unreadable file is replaced by the defaults, for the logs.
 */
class DataStoreSettingsRepository(
    fileSystem: FileSystem,
    file: Path,
    scope: CoroutineScope? = null,
    onCorruption: (Throwable) -> Unit = {},
) : SettingsRepository {

    private val store: DataStore<SettingsDto> = run {
        val storage = OkioStorage(fileSystem, SettingsSerializer, producePath = { file })
        val corruptionHandler = ReplaceFileCorruptionHandler<SettingsDto> { error ->
            onCorruption(error)
            SettingsDto()
        }
        if (scope == null) {
            DataStoreFactory.create(storage, corruptionHandler)
        } else {
            DataStoreFactory.create(storage, corruptionHandler, scope = scope)
        }
    }

    override val settings: Flow<Settings> = store.data.map { it.toDomain() }.distinctUntilChanged()

    override suspend fun update(transform: (Settings) -> Settings) {
        store.updateData { current -> SettingsDto.of(transform(current.toDomain())) }
    }
}

internal object SettingsSerializer : OkioSerializer<SettingsDto> {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    override val defaultValue: SettingsDto = SettingsDto()

    override suspend fun readFrom(source: BufferedSource): SettingsDto =
        try {
            json.decodeFromString(SettingsDto.serializer(), source.readUtf8())
        } catch (e: IllegalArgumentException) {
            // SerializationException (malformed JSON, wrong type) is an IllegalArgumentException.
            throw CorruptionException("settings.json cannot be read", e)
        }

    override suspend fun writeTo(t: SettingsDto, sink: BufferedSink) {
        sink.writeUtf8(json.encodeToString(SettingsDto.serializer(), t))
    }
}
