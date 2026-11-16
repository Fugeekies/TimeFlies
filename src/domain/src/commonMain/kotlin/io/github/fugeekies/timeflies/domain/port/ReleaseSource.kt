package io.github.fugeekies.timeflies.domain.port

import io.github.fugeekies.timeflies.domain.model.AppRelease

/** Where the latest published version is read (GitHub Releases). Implemented by the data layer. */
fun interface ReleaseSource {
    /** The latest published version, or null when it cannot be read (offline, GitHub unreachable). */
    suspend fun latest(): AppRelease?
}
