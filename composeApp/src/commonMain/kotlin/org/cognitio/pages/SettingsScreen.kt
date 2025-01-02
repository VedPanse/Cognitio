package org.cognitio.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.customTextField
import org.cognitio.PopupType
import org.cognitio.timedPopup
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
import org.cognitio.goButton
import org.cognitio.line
import org.cognitio.apiKey
import org.cognitio.appName
import org.cognitio.isDesktop
import java.io.File
import java.io.IOException


@Composable
fun settingsScreen() {
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
        line()
        Spacer(modifier = Modifier.height(innerPadding.dp))

        val link = "https://aistudio.google.com/app/apikey"
        val annotatedString = buildAnnotatedString {
            append("To use $appName's features, you'll need a FREE Gemini-1.5-Flash API key. Here's how you can get one:")
            pushStringAnnotation(tag = "URL", annotation = link)

            pop()
            append(" ")
            withStyle(
                style = SpanStyle(
                    color = AppTheme.primaryColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(link)
            }
            append("\n")
            append(
                """
                Follow these steps:-
                1. Click here to access the API key page.
                2. Sign in with your Google account if you haven’t already.
                3. Click "Create an API Key".
                4. Select a project under Google Cloud Projects to generate your free API key.
                5. Ensure the API key is specifically for the FREE version of Gemini-1.5-flash.
                6. Copy the API key and paste it below.
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
        line()
        Spacer(modifier = Modifier.height(outerPadding.dp))

        if (showSuccessPopup) {
            timedPopup(
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
            timedPopup(
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
        customTextField(
            placeholder = "Enter your API key",
            singleLine = true,
            getText = { newAPIKey = it },
            modifier = Modifier.fillMaxWidth(if (isDesktop()) 0.4f else 0.9f)
        )

        var buttonText by remember { mutableStateOf("Update") }

        goButton(buttonText) {
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
            throw IOException("No internet connection or server unreachable")
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw Exception("An unexpected error occurred: ${e.message ?: "Unknown error"}")
        }
    }
}


// Declare the expected platform-specific function
expect fun openUrlInBrowser(url: String)