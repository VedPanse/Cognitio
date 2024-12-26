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
import java.io.File
import java.util.Properties

var apiKey: String = ""
expect fun getEnvPath(): String

// TODO comments and documentation for kotlin convention

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedQuiz by remember { mutableStateOf<Quiz?>(null) }

    val quiz = Quiz("History", "World War II", intArrayOf(1, 1, 1), null, null)
    val question1: Question = Question("What is the capital of France?", "Paris", QType.MCQ, listOf("Paris", "London", "Berlin", "Madrid"))
    val question2: Question = Question("What is the largest planet in our solar system?", "Jupiter", QType.MCQ, listOf("Mars", "Jupiter", "Saturn", "Earth"))
    val question3: Question = Question("Who wrote the play 'Romeo and Juliet'?", null, QType.SHORT, null)
    val question4: Question = Question("What is the largest country in the world by land area?", null, QType.LONG, null)

    quiz.questionList.add(question1)
    quiz.questionList.add(question2)
    quiz.questionList.add(question3)
    quiz.questionList.add(question4)

    selectedQuiz = quiz
    currentScreen = Screen.QUIZ_SCREEN


    // Initialize app state and check for .env file
    LaunchedEffect(Unit) {
        val envPath = getEnvPath()
        val envFile = File(envPath)

        if (envFile.exists()) {
            // Load the API key from the .env file
            val properties = Properties().apply {
                load(envFile.reader())
            }
            apiKey = properties.getProperty("GEMINI_API_KEY", "").also {
                if (it.isEmpty()) {
                    currentScreen = Screen.SETTINGS
                }
            }
        } else {
            // Redirect to Settings if .env does not exist
            currentScreen = Screen.SETTINGS
        }
    }

    MaterialTheme(
        typography = AppTypography
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.bgColor)
        ) {
            Navbar(currentScreen = currentScreen, onScreenSelected = { currentScreen = it })
            Spacer(modifier = Modifier.width(30.dp))

            Column {
                Spacer(modifier = Modifier.height(30.dp))

                when (currentScreen) {
                    Screen.HOME -> HomeScreen()
                    Screen.SETTINGS -> SettingsScreen()
                    Screen.QUIZ_SCREEN -> QuizScreen(quiz = selectedQuiz!!)
                    Screen.QUIZ_FORM -> QuizFormScreen(showQuiz = { quiz ->
                        selectedQuiz = quiz
                        currentScreen = Screen.QUIZ_SCREEN
                    }) {
                        currentScreen = Screen.SETTINGS
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
