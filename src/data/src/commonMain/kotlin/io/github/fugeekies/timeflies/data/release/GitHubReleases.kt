package io.github.fugeekies.timeflies.data.release

import io.github.fugeekies.timeflies.domain.model.AppRelease
import io.github.fugeekies.timeflies.domain.port.ReleaseSource
import io.github.fugeekies.timeflies.domain.rules.Versions
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The latest version published in the GitHub Releases of TimeFlies (decision of 16/11/2026). One anonymous request,
 * without any personal data: GitHub only sees the address of the device, as for any page.
 *
 * @param fetch reads a URL as text, or returns null on any failure; handed by the app, which keeps this layer free
 *   of a network library.
 */
class GitHubReleases(private val fetch: suspend (url: String) -> String?) : ReleaseSource {

    override suspend fun latest(): AppRelease? = try {
        fetch(LATEST_URL)?.let(::parse)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    companion object {
        const val LATEST_URL = "https://api.github.com/repos/Fugeekies/TimeFlies/releases/latest"

        /** Only the release pages of this repository are ever opened from the banner. */
        private const val RELEASE_PAGES = "https://github.com/Fugeekies/TimeFlies/releases/"

        private val json = Json { ignoreUnknownKeys = true }

        /** The release in GitHub's answer, or null when it is not a usable published version. */
        fun parse(text: String): AppRelease? {
            val release = runCatching { json.decodeFromString(LatestRelease.serializer(), text) }.getOrNull() ?: return null
            if (release.draft || release.prerelease) return null
            val version = Versions.parse(release.tagName)?.joinToString(".") ?: return null
            if (!release.htmlUrl.startsWith(RELEASE_PAGES)) return null
            return AppRelease(version, release.htmlUrl)
        }
    }
}

@Serializable
private data class LatestRelease(
    @SerialName("tag_name") val tagName: String,
    @SerialName("html_url") val htmlUrl: String,
    val draft: Boolean = false,
    val prerelease: Boolean = false,
)
