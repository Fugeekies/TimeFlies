package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Windows: "Afficher les animations dans Windows" turned off also turns off the window animations, stored as
 * `MinAnimate = 0` under `HKCU\Control Panel\Desktop\WindowMetrics`. Read once per app, off the main thread.
 */
@Composable
actual fun rememberReducedMotion(): Boolean? = produceState(WindowsAnimations.cached) {
    if (value == null) value = withContext(Dispatchers.IO) { WindowsAnimations.read() }
}.value

private object WindowsAnimations {
    @Volatile
    var cached: Boolean? = null

    fun read(): Boolean = (cached ?: query()).also { cached = it }

    private fun query(): Boolean = runCatching {
        if (!System.getProperty("os.name").orEmpty().startsWith("Windows")) return false
        val process = ProcessBuilder("reg", "query", "HKCU\\Control Panel\\Desktop\\WindowMetrics", "/v", "MinAnimate")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor()
        Regex("""MinAnimate\s+REG_SZ\s+(\d+)""").find(output)?.groupValues?.get(1) == "0"
    }.getOrDefault(false)
}
