package org.cognitio.pages

// In desktopMain
import java.net.HttpURLConnection
import java.net.URL

actual fun isInternetAvailable(): Boolean {
    return try {
        val url = URL("https://www.google.com")
        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "GET"
            connectTimeout = 1500  // Timeout after 1.5 seconds
            readTimeout = 1500
        }
        connection.connect()  // Try connecting
        connection.responseCode == HttpURLConnection.HTTP_OK // Check for a successful response
    } catch (e: Exception) {
        false  // If there's an error (e.g., no internet connection), return false
    }
}

