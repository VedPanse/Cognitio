package org.cognitio

import java.io.File


actual fun getEnvPath(): String {
    return File(AppContextProvider.getContext().filesDir, ".env").path
}


actual fun writeFile(path: String, content: String) {
    val file = File(path)
    try {
        // Ensure the parent directories exist
        file.parentFile?.mkdirs()

        // Write to the file
        file.writeText(content)
    } catch (e: Exception) {
        throw IllegalStateException("Error writing to file", e)
    }
}