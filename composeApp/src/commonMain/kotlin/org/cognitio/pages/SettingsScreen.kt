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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException


@Composable
fun SettingsScreen() {
    var newAPIKey by remember { mutableStateOf("") }
    var showErrorPopup by remember { mutableStateOf(false) }
    var showSuccessPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Creating an AnnotatedString with a link annotation
        // TODO add more instructions on getting an API key
        val link = "https://aistudio.google.com/app/apikey"
        val annotatedString = buildAnnotatedString {
            append("You can get a GEMINI-1.5-FLASH API key from ")
            pushStringAnnotation(tag = "URL", annotation = link)
            withStyle(
                style = SpanStyle(
                    color = AppTheme.primaryColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
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

        val coroutineScope = rememberCoroutineScope()

        CustomTextField(
            placeholder = "Enter your API key",
            singleLine = true,
            getText = { newAPIKey = it },
            onEnterKeyPressed = {
                try {
                    // Validate API key (suspend function)
                    validateApiKey(newAPIKey)

                    // Save the key
                    if (!showErrorPopup) {
                        val file = File(getEnvPath())
                        if (!file.exists()) file.createNewFile()

                        writeFile(getEnvPath(), "GEMINI_API_KEY=$newAPIKey")

                        // Show success popup
                        showSuccessPopup = true
                    }
                } catch (e: IllegalArgumentException) {
                    // Show error popup for validation failure
                    errorMessage = "Invalid API Key. Make sure you are using Gemini-1.5-Flash model"
                    showErrorPopup = true
                } catch (e: Exception) {
                    // Show error popup for other failures
                    errorMessage = "Encountered an error while updating API Key. Make sure the device is connected to the internet."
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
                message = errorMessage,
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

fun validateApiKey(apiKey: String): Boolean {
    val client = OkHttpClient()
    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    val requestBody = """
        {
            "contents": [{
                "parts": [{"text": "say hi"}]
            }]
        }
    """.trimIndent()

    val request = Request.Builder()
        .url(url)
        .post(okhttp3.RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
        .build()

    try {
        val response: Response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (!response.isSuccessful || (responseBody?.contains("API Key invalid", ignoreCase = true) == true)) {
            throw IllegalArgumentException("API key is invalid. Error: ${response.code} - ${response.message}")
        }
        return true
    } catch (e: IOException) {
        println("Failed to make request: ${e.message}")
        throw IllegalArgumentException("Network error occurred while validating the API key")
    }
}
