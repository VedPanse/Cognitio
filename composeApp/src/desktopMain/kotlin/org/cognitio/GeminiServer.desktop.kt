package org.cognitio

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream

actual fun handleDocumentUpload(quiz: Quiz, message: String): String {
    if (quiz.documentPath.isNullOrEmpty()) throw IllegalArgumentException("Document path is null")

    val file = File(quiz.documentPath)
    if (!file.exists() || !file.canRead()) throw IllegalArgumentException("Invalid or unreadable document path")

    val mimeType = "text/plain"
    val tempFile = if (file.extension == "txt") {
        // Use the file directly if it's already a plain text file
        file
    } else {
        // Convert PDF or DOCX to text and save as a temporary file
        File("temp.txt").apply {
            when (file.extension.lowercase()) {
                "pdf" -> {
                    val pdfDocument = PDDocument.load(file)
                    if (pdfDocument.isEncrypted) throw IllegalAccessException("PDF is encrypted and cannot be processed")
                    val textStripper = PDFTextStripper().apply {
                        sortByPosition = true
                        addMoreFormatting = true
                    }
                    val rawText = textStripper.getText(pdfDocument)
                    pdfDocument.close()

                    // Preprocess text to handle formatting issues
                    val cleanedText = preprocessPdfText(rawText)
                    writeText(cleanedText)
                }
                "docx" -> {
                    val docx = XWPFDocument(FileInputStream(file))
                    val text = docx.paragraphs.joinToString("\n") { it.text }
                    writeText(text)
                    docx.close()
                }
                else -> throw IllegalArgumentException("Unsupported file type: ${file.extension}")
            }
        }
    }

    val numBytes = tempFile.length()

    // Define constants for upload and API interactions
    val baseUrl = "https://generativelanguage.googleapis.com"
    val client = OkHttpClient()
    val gson = Gson()

    // Step 1: Initialize resumable upload
    val metadataJson = JsonObject().apply {
        add("file", JsonObject().apply {
            addProperty("display_name", "TEXT")
        })
    }
    val initRequest = Request.Builder()
        .url("$baseUrl/upload/v1beta/files?key=$apiKey")
        .header("X-Goog-Upload-Protocol", "resumable")
        .header("X-Goog-Upload-Command", "start")
        .header("X-Goog-Upload-Header-Content-Length", numBytes.toString())
        .header("X-Goog-Upload-Header-Content-Type", mimeType)
        .header("Content-Type", "application/json")
        .post(RequestBody.create("application/json".toMediaTypeOrNull(), gson.toJson(metadataJson)))
        .build()

    val initResponse = client.newCall(initRequest).execute()
    val uploadUrl = initResponse.header("X-Goog-Upload-URL")
        ?: throw IllegalStateException("Failed to get upload URL")

    // Step 2: Upload file
    val uploadRequest = Request.Builder()
        .url(uploadUrl)
        .header("Content-Length", numBytes.toString())
        .header("X-Goog-Upload-Offset", "0")
        .header("X-Goog-Upload-Command", "upload, finalize")
        .post(RequestBody.create(mimeType.toMediaTypeOrNull(), tempFile))
        .build()

    val uploadResponse = client.newCall(uploadRequest).execute()
    val fileInfo = gson.fromJson(uploadResponse.body?.string(), JsonObject::class.java)
    val fileUri = fileInfo["file"].asJsonObject["uri"].asString
    val fileName = fileInfo["file"].asJsonObject["name"].asString

    // Step 3: Generate content using the file
    val generateJson = JsonObject().apply {
        add("contents", gson.toJsonTree(listOf(
            JsonObject().apply {
                add("parts", gson.toJsonTree(listOf(
                    JsonObject().apply { addProperty("text", message) },
                    JsonObject().apply {
                        add("file_data", JsonObject().apply {
                            addProperty("mime_type", mimeType)
                            addProperty("file_uri", fileUri)
                        })
                    }
                )))
            }
        )))
    }
    val generateRequest = Request.Builder()
        .url("$baseUrl/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
        .header("Content-Type", "application/json")
        .post(RequestBody.create("application/json".toMediaTypeOrNull(), gson.toJson(generateJson)))
        .build()

    val generateResponse = client.newCall(generateRequest).execute()
    val responseJson = gson.fromJson(generateResponse.body?.string(), JsonObject::class.java)

    // Step 4: Delete the uploaded file
    val deleteRequest = Request.Builder()
        .url("$baseUrl/v1beta/files/$fileName?key=$apiKey")
        .delete()
        .build()

    val deleteResponse = client.newCall(deleteRequest).execute()
    if (!deleteResponse.isSuccessful) {
        println("Warning: Failed to delete the uploaded file: $fileName")
    }

    // Delete temp file if created locally
    if (file.extension != "txt") tempFile.delete()

    // Extract and return the response text
    return responseJson["candidates"].asJsonArray.joinToString("") { candidate ->
        candidate.asJsonObject["content"].asJsonObject["parts"].asJsonArray.joinToString("") { part ->
            part.asJsonObject["text"].asString
        }
    }
}

/**
 * Preprocess PDF text to improve readability.
 */
private fun preprocessPdfText(rawText: String): String {
    return rawText.lines()
        .map { it.trim() } // Trim unnecessary whitespace
        .filter { it.isNotBlank() } // Remove blank lines
        .joinToString(" ") { line ->
            if (line.endsWith("-")) {
                // Remove hyphenation at line breaks
                line.removeSuffix("-")
            } else {
                line
            }
        }
        .replace("\\s+".toRegex(), " ") // Normalize spaces
}
