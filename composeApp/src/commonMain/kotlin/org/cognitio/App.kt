package org.cognitio

import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import okio.FileSystem
import okio.Path
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

val gson = Gson()
val filePath: Path = getJSONFilePath()
val fileSystem = FileSystem.SYSTEM

// TODO comments and documentation for kotlin convention
// TODO improve the handwritten content
// TODO remove all println statements
// TODO Prepare a 300-word essay explaining your background (educational and professional background,
//  coding experience with different technologies, hobbies -- if relevant to the topic of the submission--,
//  the idea behind your project and Code, and the technologies used to develop it) (the “Essay”)


@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedQuiz by remember { mutableStateOf<Quiz?>(null) }

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

    if (!fileSystem.exists(filePath)) {
        fileSystem.write(filePath) {
            writeUtf8("{}") // Start with an empty JSON object
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
                    Screen.HOME -> HomeScreen {
                        selectedQuiz = it
                        currentScreen = Screen.QUIZ_SCREEN
                    }

                    Screen.SETTINGS -> SettingsScreen()
                    Screen.QUIZ_SCREEN -> QuizScreen(quiz = selectedQuiz!!)
                    Screen.QUIZ_FORM -> QuizFormScreen(showQuiz = { quiz ->
                        selectedQuiz = quiz
                        currentScreen = Screen.QUIZ_SCREEN
                    }) {
                        currentScreen = Screen.SETTINGS
                    }

                    Screen.SEARCH -> SearchScreen {
                        selectedQuiz = it
                        currentScreen = Screen.QUIZ_SCREEN
                    }
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


@Composable
fun SubjectRep(subjectList: List<Subject>) {
    Row {
        subjectList.forEach {
            Box(
                modifier = Modifier.background(color = it.bgColor)
                    .padding(7.dp)
            ) {
                Text(
                    text = it.toString(),
                    color = it.textColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}