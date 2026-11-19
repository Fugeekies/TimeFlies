package io.github.fugeekies.timeflies.ui.personal

/**
 * The system file picker, for the export and the import of Settings › Mes événements (decision of 19/11/2026): the
 * Storage Access Framework on Android (Drive, Téléchargements…), the Save and Open dialogs on Windows. Nothing leaves
 * the device unless the user picks a place.
 */
interface FileExchange {
    /**
     * Asks where to save a file named [suggestedName] and writes [content] into it.
     *
     * @return false when the user cancelled.
     * @throws Exception when the file could not be written.
     */
    suspend fun save(suggestedName: String, content: String): Boolean

    /**
     * Asks for a file and reads it as UTF-8 text.
     *
     * @return null when the user cancelled.
     * @throws Exception when the file could not be read.
     */
    suspend fun open(): String?

    companion object {
        /** The name offered for the export. */
        const val EXPORT_FILE_NAME = "timeflies-events.json"

        /** Files larger than this are not an export of TimeFlies: not read, to keep the memory safe. */
        const val MAX_IMPORT_BYTES = 1_000_000
    }
}
