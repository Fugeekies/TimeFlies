package io.github.fugeekies.timeflies.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem
import okio.Path

/**
 * A DataStore on one JSON file of the app's folder (`settings.json`, `personal-events.json`): atomic writes, and a file
 * that cannot be read is replaced by [serializer]'s default value.
 *
 * @param scope where DataStore runs its writes; DataStore's own I/O scope when null.
 * @param onCorruption called when an unreadable file is replaced, for the logs.
 */
internal fun <T> jsonDataStore(
    fileSystem: FileSystem,
    file: Path,
    serializer: OkioSerializer<T>,
    scope: CoroutineScope?,
    onCorruption: (Throwable) -> Unit,
): DataStore<T> {
    val storage = OkioStorage(fileSystem, serializer, producePath = { file })
    val corruptionHandler = ReplaceFileCorruptionHandler { error ->
        onCorruption(error)
        serializer.defaultValue
    }
    return if (scope == null) {
        DataStoreFactory.create(storage, corruptionHandler)
    } else {
        DataStoreFactory.create(storage, corruptionHandler, scope = scope)
    }
}
