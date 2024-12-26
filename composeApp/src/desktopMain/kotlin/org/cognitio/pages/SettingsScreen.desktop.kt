package org.cognitio.pages

import java.awt.Desktop
import java.net.URI

actual fun openUrlInBrowser(url: String) {
    if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI.create(url))
        }
    }
}
