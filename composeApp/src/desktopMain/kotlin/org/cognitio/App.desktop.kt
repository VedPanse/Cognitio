package org.cognitio


import java.io.File


actual fun getEnvPath(): String {
    return File("src", ".env").absolutePath
}