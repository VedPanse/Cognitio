package org.cognitio

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main() = application {
    // Create window state for initial position and size
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized,
        position = WindowPosition(Alignment.Center)
    )

    // Set up the main window
    Window(
        onCloseRequest = ::exitApplication,
        title = "Cognitio",
        state = windowState
    ) {
        App() // Load the app content
    }
}
