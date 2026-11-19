package io.github.fugeekies.timeflies.desktop

import io.github.fugeekies.timeflies.ui.personal.FileExchange
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext

/**
 * The export and import of Settings › Mes événements through the Windows Save and Open dialogs (decision of
 * 19/11/2026), on top of [owner].
 */
class DesktopFileExchange(private val owner: Frame?) : FileExchange {

    override suspend fun save(suggestedName: String, content: String): Boolean {
        val file = choose(FileDialog.SAVE, suggestedName) ?: return false
        // The dialog does not add the extension the user left out.
        val target = if (file.extension.isEmpty()) File(file.parentFile, "${file.name}.json") else file
        withContext(Dispatchers.IO) { target.writeText(content, Charsets.UTF_8) }
        return true
    }

    override suspend fun open(): String? {
        val file = choose(FileDialog.LOAD, null) ?: return null
        return withContext(Dispatchers.IO) {
            if (file.length() > FileExchange.MAX_IMPORT_BYTES) throw IOException("File too large")
            file.readText(Charsets.UTF_8)
        }
    }

    /** The native dialog, modal, on the Swing thread; null when cancelled. */
    private suspend fun choose(mode: Int, suggestedName: String?): File? = withContext(Dispatchers.Swing) {
        val dialog = FileDialog(owner, "TimeFlies", mode)
        dialog.file = suggestedName ?: "*.json"
        dialog.isVisible = true
        val name = dialog.file ?: return@withContext null
        File(dialog.directory, name)
    }
}
