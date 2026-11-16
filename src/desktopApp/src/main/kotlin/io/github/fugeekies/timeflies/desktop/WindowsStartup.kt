package io.github.fugeekies.timeflies.desktop

import co.touchlab.kermit.Logger

/**
 * Starting TimeFlies with Windows (docs/UISpec/Settings.template.md, "Notification et démarrage"): a value under
 * `HKCU\Software\Microsoft\Windows\CurrentVersion\Run`, current user, no administrator rights, written with
 * `reg.exe`. Windows then launches the app with [BACKGROUND_ARGUMENT]: window hidden, tray icon only.
 */
object WindowsStartup {
    /** Command-line argument of a launch at startup: the window stays hidden. */
    const val BACKGROUND_ARGUMENT = "--background"

    private const val RUN_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    private const val VALUE_NAME = "TimeFlies"

    private val log = Logger.withTag("Startup")

    /**
     * Writes or removes the startup value. Only an installed app has an executable to start (the launcher made by
     * jpackage gives its path in `jpackage.app-path`): a run from Gradle changes nothing.
     */
    fun apply(enabled: Boolean) {
        if (!System.getProperty("os.name").orEmpty().startsWith("Windows")) return
        val executable = System.getProperty("jpackage.app-path")
        if (executable == null) {
            log.i { "Lancement au démarrage ${if (enabled) "coché" else "décoché"} : ignoré hors de l'application installée" }
            return
        }
        val command = if (enabled) {
            // Backslashed quotes: Java quotes the whole argument, reg.exe keeps the inner quotes around the path.
            listOf("reg", "add", RUN_KEY, "/v", VALUE_NAME, "/t", "REG_SZ", "/d", "\\\"$executable\\\" $BACKGROUND_ARGUMENT", "/f")
        } else {
            listOf("reg", "delete", RUN_KEY, "/v", VALUE_NAME, "/f")
        }
        runCatching {
            val process = ProcessBuilder(command).redirectErrorStream(true).start()
            process.inputStream.readAllBytes()
            process.waitFor()
        }.onSuccess { code ->
            // reg delete fails when the value is already missing: nothing to do then.
            if (code == 0 || !enabled) log.i { "Lancement au démarrage ${if (enabled) "activé" else "désactivé"}" }
            else log.w { "reg.exe a échoué (code $code)" }
        }.onFailure { log.w(it) { "reg.exe n'a pas pu être lancé" } }
    }
}
