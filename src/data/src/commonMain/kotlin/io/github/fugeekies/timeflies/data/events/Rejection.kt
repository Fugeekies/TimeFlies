package io.github.fugeekies.timeflies.data.events

import io.github.fugeekies.timeflies.domain.model.Family

/**
 * An entry of an event file that was left out, with every reason found.
 *
 * @property index position of the entry in its file, starting at 0 (-1 when the whole file is unreadable).
 */
data class Rejection(
    val family: Family,
    val index: Int,
    val id: String?,
    val reasons: List<String>,
) {
    override fun toString(): String =
        "${family.fileName}[${index}]${id?.let { " ($it)" } ?: ""}: ${reasons.joinToString("; ")}"
}
