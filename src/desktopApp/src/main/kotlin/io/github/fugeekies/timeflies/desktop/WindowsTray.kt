package io.github.fugeekies.timeflies.desktop

import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.ui.text.TrayMenuTexts
import java.awt.Image
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.RenderingHints
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BaseMultiResolutionImage
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * The icon in the Windows notification area (docs/UISpec/WindowsTray.template.md): tooltip, left click, menu
 * and notifications. Written on AWT's `TrayIcon`, which Compose's `Tray` wraps, because Compose's version
 * opens the window on a double click only, where the spec asks for a single left click.
 */
class WindowsTray private constructor(private val icon: TrayIcon) {

    /** Updates the tooltip shown on hover. */
    fun setTooltip(text: String) {
        icon.toolTip = text
    }

    /** A Windows notification with a title and a text; clicking it calls the `onNotificationClicked` given to [create]. */
    fun notify(title: String, text: String) {
        icon.displayMessage(title, text, TrayIcon.MessageType.NONE)
    }

    fun remove() {
        SystemTray.getSystemTray().remove(icon)
    }

    companion object {
        private val log = Logger.withTag("Tray")

        /**
         * Adds the icon to the notification area, or returns null when there is none (another system, a session
         * without a taskbar): the window then never hides.
         *
         * @param onOpen left click on the icon, or "Ouvrir TimeFlies".
         * @param onNotificationClicked click on a notification (AWT reports a double click on the icon the same way).
         * @param onDailyShock "Coup de vieux du jour": sends today's notification at once.
         */
        fun create(
            texts: TrayMenuTexts,
            tooltip: String,
            onOpen: () -> Unit,
            onNotificationClicked: () -> Unit,
            onDailyShock: () -> Unit,
            onSettings: () -> Unit,
            onQuit: () -> Unit,
        ): WindowsTray? {
            if (!SystemTray.isSupported()) {
                log.w { "Pas de zone de notification : la fenêtre ne se cache pas" }
                return null
            }
            val menu = PopupMenu().apply {
                add(item(texts.open, onOpen))
                add(item(texts.dailyShock, onDailyShock))
                add(item(texts.settings, onSettings))
                addSeparator()
                add(item(texts.quit, onQuit))
            }
            val icon = TrayIcon(trayImage(), tooltip, menu).apply {
                addActionListener { onNotificationClicked() }
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (e.button == MouseEvent.BUTTON1 && e.clickCount == 1) onOpen()
                    }
                })
            }
            return runCatching { SystemTray.getSystemTray().add(icon) }
                .onFailure { log.w(it) { "L'icône n'a pas pu être ajoutée" } }
                .map { WindowsTray(icon) }
                .getOrNull()
        }

        private fun item(label: String, action: () -> Unit) = MenuItem(label).apply { addActionListener { action() } }

        /**
         * The winged hourglass in the sizes Windows uses for the notification area (100 % to 300 % display
         * scaling), smoothly reduced from the 256 px drawing: AWT picks the one that fits.
         */
        private fun trayImage(): Image {
            val source = checkNotNull(WindowsTray::class.java.getResourceAsStream("/icon/timeflies-256.png")) { "icon/timeflies-256.png is missing" }
                .use { ImageIO.read(it) }
            val sizes = listOf(16, 20, 24, 32, 40, 48, 64)
            return BaseMultiResolutionImage(*sizes.map { size -> scaled(source, size) }.toTypedArray())
        }

        private fun scaled(source: BufferedImage, size: Int): BufferedImage {
            val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
            image.createGraphics().apply {
                setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
                setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                drawImage(source.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null)
                dispose()
            }
            return image
        }
    }
}
