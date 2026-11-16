package io.github.fugeekies.timeflies.data.release

import io.github.fugeekies.timeflies.domain.model.AppRelease
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GitHubReleasesTest {

    private fun answer(tag: String = "v0.2.0", url: String = "https://github.com/Fugeekies/TimeFlies/releases/tag/v0.2.0", draft: Boolean = false, prerelease: Boolean = false) =
        """{"url":"https://api.github.com/x","html_url":"$url","tag_name":"$tag","draft":$draft,"prerelease":$prerelease,"assets":[]}"""

    @Test
    fun readsTheVersionAndThePageOfTheLatestRelease() = runTest {
        var asked: String? = null
        val release = GitHubReleases { asked = it; answer() }.latest()
        assertEquals(GitHubReleases.LATEST_URL, asked)
        assertEquals(AppRelease("0.2.0", "https://github.com/Fugeekies/TimeFlies/releases/tag/v0.2.0"), release)
    }

    @Test
    fun ignoresWhatIsNotAPublishedVersion() {
        assertNull(GitHubReleases.parse(answer(draft = true)))
        assertNull(GitHubReleases.parse(answer(prerelease = true)))
        assertNull(GitHubReleases.parse(answer(tag = "nightly")))
        assertNull(GitHubReleases.parse("{ not json"))
    }

    @Test
    fun neverOffersAPageOutsideTheReleasesOfTimeFlies() {
        assertNull(GitHubReleases.parse(answer(url = "https://example.org/TimeFlies-Setup.exe")))
    }

    @Test
    fun givesNothingWhenOffline() = runTest {
        assertNull(GitHubReleases { null }.latest())
        assertNull(GitHubReleases { throw IllegalStateException("no network") }.latest())
    }
}
