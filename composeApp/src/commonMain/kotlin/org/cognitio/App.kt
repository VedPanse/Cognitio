package org.cognitio

import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.HOME) }
    var selectedQuiz by remember { mutableStateOf<Quiz?>(null)}

    MaterialTheme(
        typography = AppTypography
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .background(AppTheme.bgColor)
        ) {
            Text("Hello World")
        }
    }
}