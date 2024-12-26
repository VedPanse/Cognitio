package org.cognitio.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.CustomTextField
import org.cognitio.PopupType
import org.cognitio.TimedPopup
import org.cognitio.getEnvPath
import org.cognitio.writeFile
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.io.File


@Composable
fun SettingsScreen() {
    var newAPIKey by remember { mutableStateOf("") }
    var showErrorPopup by remember { mutableStateOf(false) }
    var showSuccessPopup by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Creating an AnnotatedString with a link annotation
        val link = "https://aistudio.google.com/app/apikey"
        val annotatedString = buildAnnotatedString {
            append("You can get a GEMINI-1.5-FLASH API key from ")
            pushStringAnnotation(tag = "URL", annotation = link)
            withStyle(style = SpanStyle(color = AppTheme.primaryColor, textDecoration = TextDecoration.Underline)) {
                append(link)
            }
            pop()
        }

        // Displaying the text with the clickable link
        Text(
            text = annotatedString,
            modifier = Modifier.clickable {
                // Handle the click event for the link
                openUrlInBrowser(link)
            }
        )

        CustomTextField(
            placeholder = "Enter your API key",
            singleLine = true,
            getText = { newAPIKey = it },
            onEnterKeyPressed = {
                try {
                    val file = File(getEnvPath())
                    if (!file.exists()) file.createNewFile()

                    writeFile(getEnvPath(), "GEMINI_API_KEY=$newAPIKey")

                    // Show success popup
                    showSuccessPopup = true
                } catch (e: Exception) {
                    // Show error popup
                    showErrorPopup = true
                }
            }
        )

        // Show success popup if the operation succeeds
        if (showSuccessPopup) {
            TimedPopup(
                message = "Successfully Updated API Key",
                popupType = PopupType.SUCCESS,
                time = 3000 // Show popup for 3 seconds
            )

            // Automatically reset the success popup visibility after a delay
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showSuccessPopup = false
            }
        }

        // Show error popup if there is an error
        if (showErrorPopup) {
            TimedPopup(
                message = "Encountered an error while updating API Key",
                popupType = PopupType.ERROR,
                time = 3000 // Show popup for 3 seconds
            )

            // Automatically reset the error popup visibility after a delay
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showErrorPopup = false
            }
        }
    }
}


// Declare the expected platform-specific function
expect fun openUrlInBrowser(url: String)
