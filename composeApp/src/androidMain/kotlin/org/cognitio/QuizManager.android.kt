package org.cognitio

import android.content.Context
import okio.Path
import okio.Path.Companion.toPath


actual fun getJSONFilePath(): Path {
    val context: Context = AppContextProvider.context // Get the context from a global provider
    return context.filesDir.resolve(".${appName.lowercase()}.json").absolutePath.toPath()
}
