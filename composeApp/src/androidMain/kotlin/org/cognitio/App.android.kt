package org.cognitio


import org.cognitio.AppContextProvider.context
import java.io.File


actual fun getEnvPath(): String {
    return File(context.filesDir, ".env").absolutePath
}