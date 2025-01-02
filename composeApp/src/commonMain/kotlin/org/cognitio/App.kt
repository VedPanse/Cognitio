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
import org.cognitio.pages.feedback
import org.cognitio.pages.homeScreen
import org.cognitio.pages.quizFormScreen
import org.cognitio.pages.quizScreen
import org.cognitio.pages.searchScreen
import org.cognitio.pages.settingsScreen
import org.eidetic.navbar
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File
import java.util.Properties

/**
 * Defines an entry point to the main composable
 *
 * @author Ved Panse
 * @bugs none
 */

var apiKey: String = ""

/**
 * Gets the path to .env file for different platforms
 */
expect fun getEnvPath(): String


val gson = Gson()
val filePath: Path = getJSONFilePath()
val fileSystem = FileSystem.SYSTEM

// TODO comments and documentation for kotlin convention
// TODO Prepare a 300-word essay explaining your background (educational and professional background,
//  coding experience with different technologies, hobbies -- if relevant to the topic of the submission--,
//  the idea behind your project and Code, and the technologies used to develop it) (the “Essay”)


/**
 * Entry point for main composable
 */
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
            navbar(currentScreen = currentScreen, onScreenSelected = { currentScreen = it })
            Spacer(modifier = Modifier.width(30.dp))

            Column {
                Spacer(modifier = Modifier.height(30.dp))

                when (currentScreen) {
                    Screen.HOME -> homeScreen {
                        selectedQuiz = it
                        currentScreen = Screen.FEEDBACK
                    }

                    Screen.SETTINGS -> settingsScreen()
                    Screen.QUIZ_SCREEN -> quizScreen(quiz = selectedQuiz!!)
                    Screen.QUIZ_FORM -> quizFormScreen(showQuiz = { quiz ->
                        selectedQuiz = quiz
                        currentScreen = Screen.QUIZ_SCREEN
                    }) {
                        currentScreen = Screen.SETTINGS
                    }

                    Screen.SEARCH -> searchScreen {
                        selectedQuiz = it
                        currentScreen = Screen.QUIZ_SCREEN
                    }

                    Screen.FEEDBACK -> feedback(selectedQuiz!!)
                }
            }
        }
    }
}


/**
 * Checks if the current runtime platform is desktop or android
 *
 * @return if current runtime platform is desktop
 */
fun isDesktop(): Boolean {
    return try {
        Class.forName("java.awt.Desktop") != null
    } catch (e: ClassNotFoundException) {
        false
    }
}


/**
 * Displays the subjects in a row
 */
@Composable
fun subjectRep(subjectList: List<Subject>) {
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