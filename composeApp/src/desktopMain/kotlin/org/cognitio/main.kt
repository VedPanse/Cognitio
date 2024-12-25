package org.cognitio

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Toolkit

fun main() = application {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    Window(
        onCloseRequest = ::exitApplication,
        title = "Cognitio",
        state = rememberWindowState(
            placement = WindowPlacement.Maximized,
            position = WindowPosition(Alignment.Center)
        )
    ) {
        // Set the window size to the maximum screen size
        window.setSize(screenSize.width, screenSize.height)
        App()
    }
}
