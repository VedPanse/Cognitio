package org.cognitio

import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import org.cognitio.pages.DashboardScreen
import org.cognitio.pages.HomeScreen
import org.cognitio.pages.QuizFormScreen
import org.cognitio.pages.QuizScreen
import org.cognitio.pages.SearchScreen
import org.cognitio.pages.SettingsScreen
import org.eidetic.Navbar
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedQuiz by remember { mutableStateOf<Quiz?>(null)}

    MaterialTheme(
        typography = AppTypography
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .background(AppTheme.bgColor)
        ) {
            Navbar(currentScreen = currentScreen, onScreenSelected = {currentScreen = it})
            Spacer(modifier = Modifier.width(30.dp))

            Column {
                Spacer(modifier = Modifier.height(30.dp))

                when(currentScreen) {
                    Screen.HOME -> HomeScreen()
                    Screen.SETTINGS -> SettingsScreen()
                    Screen.QUIZ_SCREEN -> QuizScreen(quiz = selectedQuiz!!)
                    Screen.QUIZ_FORM -> QuizFormScreen { quiz ->
                        selectedQuiz = quiz
                        currentScreen = Screen.QUIZ_SCREEN
                    }

                    Screen.DASHBOARD -> DashboardScreen()
                    Screen.SEARCH -> SearchScreen()
                }
            }
        }
    }
}


fun isDesktop(): Boolean {
    return try {
        Class.forName("java.awt.Desktop") != null
    } catch (e: ClassNotFoundException) {
        false
    }
}