package io.github.fugeekies.timeflies.android

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import io.github.fugeekies.timeflies.ui.personal.FileExchange
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * The export and import of Settings › Mes événements through the Storage Access Framework (decision of 19/11/2026):
 * the system picker lets the user choose Drive, Téléchargements, a USB key… No storage permission is needed.
 *
 * Built in [ComponentActivity.onCreate], before the activity starts: the launchers must be registered then.
 */
class AndroidFileExchange(private val activity: ComponentActivity) : FileExchange {

    private var pending: CompletableDeferred<Uri?>? = null

    private val createLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.CreateDocument(MIME_TYPE)) { uri -> complete(uri) }

    private val openLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> complete(uri) }

    override suspend fun save(suggestedName: String, content: String): Boolean {
        val uri = pick { createLauncher.launch(suggestedName) } ?: return false
        withContext(Dispatchers.IO) {
            val stream = activity.contentResolver.openOutputStream(uri, "wt") ?: throw IOException("No output stream")
            stream.use { it.write(content.encodeToByteArray()) }
        }
        return true
    }

    override suspend fun open(): String? {
        // Some file managers give a JSON file another type: every file is offered, the content is checked on import.
        val uri = pick { openLauncher.launch(arrayOf("*/*")) } ?: return null
        return withContext(Dispatchers.IO) {
            val stream = activity.contentResolver.openInputStream(uri) ?: throw IOException("No input stream")
            stream.use { input ->
                val buffer = ByteArrayOutputStream()
                val chunk = ByteArray(8 * 1024)
                while (true) {
                    val read = input.read(chunk)
                    if (read < 0) break
                    buffer.write(chunk, 0, read)
                    if (buffer.size() > FileExchange.MAX_IMPORT_BYTES) throw IOException("File too large")
                }
                buffer.toByteArray().decodeToString()
            }
        }
    }

    /** Launches a picker and waits for its answer; one picker at a time. */
    private suspend fun pick(launch: () -> Unit): Uri? {
        pending?.cancel()
        val deferred = CompletableDeferred<Uri?>()
        pending = deferred
        withContext(Dispatchers.Main) { launch() }
        return deferred.await()
    }

    private fun complete(uri: Uri?) {
        pending?.complete(uri)
        pending = null
    }

    private companion object {
        const val MIME_TYPE = "application/json"
    }
}
