package org.cognitio.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.CustomTextField
import org.cognitio.PopupType
import org.cognitio.TimedPopup
import org.cognitio.getEnvPath
import org.cognitio.writeFile
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.cognitio.GoButton
import org.cognitio.Line
import org.cognitio.apiKey
import org.cognitio.isDesktop
import java.io.File
import java.io.IOException


@Composable
fun SettingsScreen() {
    var newAPIKey by remember { mutableStateOf("") }
    var showErrorPopup by remember { mutableStateOf(false) }
    var showSuccessPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val innerPadding: Int = 2
    val outerPadding: Int = 5

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(outerPadding.dp))
        Line()
        Spacer(modifier = Modifier.height(innerPadding.dp))

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
            append(" ")
            append(
                """
                Follow these steps:-
                1. CLick on the link
                2. Sign in with your google account if you are not already signed in
                3. Click "Create an API Key"
                4. In order to get a free API key, you will have to select a project from Google Cloud Projects. 
                5. Make sure the API key you created is "GEMINI-1.5-FLASH"
                6. Copy the API Key and enter it below
                7. Hit enter
            """.trimIndent()
            )
        }
        // Displaying the text with the clickable link
        Text(
            text = annotatedString,
            modifier = Modifier.clickable {
                // Handle the click event for the link
                openUrlInBrowser(link)
            }
        )


        Spacer(modifier = Modifier.height(innerPadding.dp))
        Line()
        Spacer(modifier = Modifier.height(outerPadding.dp))

        if (showSuccessPopup) {
            TimedPopup(
                message = "Successfully Updated API Key",
                popupType = PopupType.SUCCESS,
                time = 3000
            )
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showSuccessPopup = false
            }
        }

        if (showErrorPopup) {
            TimedPopup(
                message = errorMessage,
                popupType = PopupType.ERROR,
                time = 3000
            )
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showErrorPopup = false
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        CustomTextField(
            placeholder = "Enter your API key",
            singleLine = true,
            getText = { newAPIKey = it },
        )

        var buttonText by remember { mutableStateOf("Update") }

        GoButton(buttonText) {
            CoroutineScope(Dispatchers.Main).launch {
                buttonText = "Updating..."
                try {
                    validateApiKey(newAPIKey) // Validate API key

                    val file = File(getEnvPath())
                    if (!file.exists()) file.createNewFile()
                    writeFile(getEnvPath(), "GEMINI_API_KEY=$newAPIKey")

                    showSuccessPopup = true
                    apiKey = newAPIKey
                } catch (e: IllegalArgumentException) {
                    errorMessage = "Invalid API Key. Make sure you are using the FREE Gemini-1.5-Flash model."
                    showErrorPopup = true
                } catch (e: IOException) {
                    errorMessage = "Device is not connected to the internet."
                    showErrorPopup = true
                } catch (e: Exception) {
                    errorMessage = "An unexpected error occurred: ${e.message ?: "Unknown error"}"
                    showErrorPopup = true
                } finally {
                    // Reset the button text back to "Update" in the `finally` block
                    buttonText = "Update"
                }
            }
        }

    }
}


suspend fun validateApiKey(apiKey: String) {
    val client = OkHttpClient()
    val gson = Gson()

    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
    val requestBody = """
        {
            "contents": [{
                "parts": [{"text": "Explain how AI works"}]
            }]
        }
    """.trimIndent()

    val request = Request.Builder()
        .url(url)
        .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
        .build()

    withContext(Dispatchers.IO) { // Run on a background thread
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorResponse = response.body?.string()

                    if (errorResponse != null) {
                        val errorJson = gson.fromJson(errorResponse, JsonObject::class.java)
                        val errorMessage = errorJson["error"]?.asJsonObject?.get("message")?.asString
                            ?: "Invalid API Key"
                        throw IllegalArgumentException(errorMessage)
                    } else {
                        throw IllegalArgumentException("Invalid API Key")
                    }
                }
            }
        } catch (e: IOException) {
            println("IOException occurred: ${e.message}")
            throw IOException("No internet connection or server unreachable")
        } catch (e: IllegalArgumentException) {
            println("IllegalArgumentException occurred: ${e.message}")
            throw e
        } catch (e: Exception) {
            println("Unexpected Exception occurred: ${e.javaClass.name}, message: ${e.message}")
            throw Exception("An unexpected error occurred: ${e.message ?: "Unknown error"}")
        }
    }
}


// Declare the expected platform-specific function
expect fun openUrlInBrowser(url: String)