package io.github.fugeekies.timeflies.desktop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI

/**
 * Reads [url] as text, or null on any failure (offline, timeout, error answer). Only used to look for a new
 * version on GitHub (decision of 16/11/2026): no personal data is sent, the User-Agent names the app only.
 */
internal suspend fun httpText(url: String): String? = withContext(Dispatchers.IO) {
    runCatching {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("User-Agent", "TimeFlies")
        try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) connection.inputStream.bufferedReader().use { it.readText() } else null
        } finally {
            connection.disconnect()
        }
    }.getOrNull()
}
