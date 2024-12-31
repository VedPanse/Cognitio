package org.cognitio

actual fun handleDocumentUpload(quiz: Quiz, message: String): String {
    return """
        ```json
        {error: "Not implemented"}
        ```
    """.trimIndent()
}