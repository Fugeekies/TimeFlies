package io.github.fugeekies.timeflies.desktop

import co.touchlab.kermit.Logger
import java.io.File
import java.io.RandomAccessFile
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.channels.FileLock
import java.nio.channels.OverlappingFileLockException

/**
 * One TimeFlies at a time (decision of 16/11/2026): started with Windows, then opened from the Start menu, the app
 * would otherwise run twice, with two tray icons on the same settings file. The first instance holds a lock in
 * `%APPDATA%\TimeFlies` and listens on a local port (loopback only, never the network); a second launch asks it to
 * show its window, then stops.
 */
object SingleInstance {
    private const val SHOW = "show"
    private val log = Logger.withTag("Instance")

    /** Kept for the whole life of the process: releasing it would let a second instance start. */
    private var lock: FileLock? = null

    /**
     * Becomes the only instance, or hands over to the running one.
     *
     * @param showWindow true for a launch by the user (the running instance shows its window); false for a launch at
     *   Windows startup, which stays silent.
     * @param onShowRequest called, on a background thread, when a later launch asks for the window.
     * @return true when this process is the only instance and goes on; false when it must stop.
     */
    fun acquire(folder: File, showWindow: Boolean, onShowRequest: () -> Unit): Boolean {
        folder.mkdirs()
        val portFile = File(folder, "instance.port")
        val acquired = try {
            RandomAccessFile(File(folder, "instance.lock"), "rw").channel.tryLock()
        } catch (e: OverlappingFileLockException) {
            null
        } catch (e: Exception) {
            // The lock cannot be taken at all (read-only folder…): better two instances than none.
            log.w(e) { "Verrou d'instance impossible : lancement sans contrôle" }
            return true
        }
        if (acquired == null) {
            if (showWindow) askRunningInstance(portFile)
            log.i { "TimeFlies tourne déjà : ${if (showWindow) "sa fenêtre est ramenée" else "lancement au démarrage ignoré"}" }
            return false
        }
        lock = acquired
        listen(portFile, onShowRequest)
        return true
    }

    private fun listen(portFile: File, onShowRequest: () -> Unit) {
        val server = runCatching { ServerSocket(0, 5, InetAddress.getLoopbackAddress()) }
            .onFailure { log.w(it) { "Canal d'instance impossible : un second lancement ne ramènera pas la fenêtre" } }
            .getOrNull() ?: return
        portFile.writeText(server.localPort.toString())
        Thread({
            while (true) {
                runCatching {
                    server.accept().use { socket ->
                        socket.soTimeout = 2_000
                        if (socket.getInputStream().bufferedReader().readLine() == SHOW) onShowRequest()
                    }
                }
            }
        }, "TimeFlies-instance").apply { isDaemon = true }.start()
    }

    private fun askRunningInstance(portFile: File) {
        runCatching {
            val port = portFile.readText().trim().toInt()
            Socket().use { socket ->
                socket.connect(InetSocketAddress(InetAddress.getLoopbackAddress(), port), 2_000)
                socket.getOutputStream().write("$SHOW\n".toByteArray())
            }
        }.onFailure { log.w(it) { "L'instance déjà lancée n'a pas répondu" } }
    }
}
