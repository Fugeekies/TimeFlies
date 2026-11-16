package io.github.fugeekies.timeflies.data.events

/**
 * An entry of an event file that was left out, with every reason found.
 *
 * @property file path of the file in the base, without ".json" ("music/rap-album-hit/us").
 * @property index position of the entry in its file, starting at 0 (-1 when the whole file is rejected).
 */
data class Rejection(
    val file: String,
    val index: Int,
    val id: String?,
    val reasons: List<String>,
) {
    override fun toString(): String =
        "$file.json[${index}]${id?.let { " ($it)" } ?: ""}: ${reasons.joinToString("; ")}"
}
