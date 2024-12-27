package org.cognitio

import okio.Path
import okio.Path.Companion.toPath

actual fun getJSONFilePath(): Path {
    return ".${appName.lowercase()}.json".toPath()
}
