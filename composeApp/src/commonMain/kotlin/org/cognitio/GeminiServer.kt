package org.cognitio

import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream


class GeminiServer(private val apiKey: String) {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    /**
     * Generate the list of questions for a quiz by sending a prompt to Gemini API.
     */
    suspend fun generateQuestionList(quiz: Quiz) {
        // Create the prompt message
        val message = """
            You are an AI tool that can generate a list of questions. Generate a list of questions
            belonging to the subject: ${quiz.subject} on the topic: ${quiz.topic}. You must generate
            ${quiz.numQuestions[0]} MCQ questions, ${quiz.numQuestions[1]} short answer questions, and 
            ${quiz.numQuestions[2]} long answer questions.
            
            The output must be in the form:
            {
                <question_number>: {
                question: <question_text>,
                answer: <answer_text only if it is a MCQ question, else null>,
                type: <question_type (can be any one of MCQ, SHORT, or LONG)>,
                options: <list of options only if it is a MCQ question, else null>
                }
            }
        """.trimIndent()

        val response: String?

        if (quiz.documentPath != null) {
            response = respondWithDoc(quiz, message)
        } else {
            // Send the prompt to Gemini API
            response = withContext(Dispatchers.IO) {
                try {
                    val content =
                        generativeModel.generateContent(message) // Assuming this is of type Content or String
                    content.text
                        ?: ""  // Extract the text if it's a Content object, or return an empty string
                } catch (e: Exception) {
                    println("Error while sending prompt: ${e.localizedMessage}")
                    null
                }
            }
        }
        // If response is null, exit early
        if (response.isNullOrEmpty()) {
            throw IllegalAccessError("Error generating question list")
        }

        // Clean and parse the response into a JSON object
        val cleanResponse = response.replace("```json", "").replace("```", "")
        val gson = Gson()
        val jsonObject: JsonObject = gson.fromJson(cleanResponse, JsonObject::class.java)

        // Process the response and create questions
        for (entry in jsonObject.entrySet()) {
            val questionDetails = entry.value.asJsonObject
            val questionText = questionDetails.get("question").asString
            val answer: String? = questionDetails.get("answer")?.takeIf { !it.isJsonNull }?.asString

            val type = when (questionDetails.get("type").asString) {
                "MCQ" -> QType.MCQ
                "SHORT" -> QType.SHORT
                "LONG" -> QType.LONG
                else -> throw IllegalArgumentException("Invalid question type")
            }

            // Handle options for MCQ questions
            val options: List<String>? = questionDetails.get("options")?.takeIf { it.isJsonArray }?.asJsonArray?.map { it.asString }

            // Create and add the question to the quiz's question list
            val question = Question(
                questionText.replace("  ", " "),
                answer?.replace("  ", " "),
                type,
                options
            )
            quiz.questionList.add(question)
        }
    }

    suspend fun gradeQuestions(quiz: Quiz) {
        if (quiz.questionList.isEmpty())
            throw IllegalArgumentException("Quiz must have at least one question")

        var questionBar: String = "{\n"

        quiz.questionList.forEach {
            questionBar += when(it.type) {
                QType.MCQ -> "(MCQ, the options were: [${it.options}])"
                QType.LONG -> "(Long answer question 200 words)"
                QType.SHORT -> "(Short answer question 50 words)"
            } + it.question + ": " + it.enteredAnswer + ",\n"
        }

        questionBar += "}"

        val message: String = """
        You are a quiz grader AI. Rate each answer in the quiz (subject: ${quiz.subject}, topic: ${quiz.topic}) from 0 to 100. For MCQ, the grade can only be 0 or 100
        The questions and user-entered answers are:
        $questionBar
        
        DO NOT JUDGE THE QUESTIONS. Even if they may seem irrelevant to the subject or topic, grade them in a fair way.

        Provide your output in the following format:
        {
            <question_number>: {
                "grade": <grade>,
                "feedback": <feedback>
            }
        }
    """.trimIndent()

        var response: String?

        if (quiz.documentPath != null) {
            response = respondWithDoc(quiz, message)
        } else {

            response = withContext(Dispatchers.IO) {
                try {
                    val content =
                        generativeModel.generateContent(message) // Assuming this is of type Content or String
                    content.text
                        ?: ""  // Extract the text if it's a Content object, or return an empty string
                } catch (e: Exception) {
                    println("Error while sending prompt: ${e.localizedMessage}")
                    null
                }
            }
        }

        if (response.isNullOrEmpty())
            throw IllegalAccessException("Error communicating with the API: Failed to grade questions - AI returned empty response")

        response = response.replace("```json", "").replace("```", "")

        val gson = Gson()
        val jsonObject = gson.fromJson(response, JsonObject::class.java)

        for ((i, entry) in jsonObject.entrySet().withIndex()) {
            val questionDetails = entry.value.asJsonObject
            val feedback: String = questionDetails.get("feedback").asString
            val grade: Double = questionDetails.get("grade").asDouble

            quiz.questionList[i].feedback = feedback.replace("  ", " ")
            quiz.questionList[i].points = grade
        }
    }

    private fun respondWithDoc(quiz: Quiz, message: String): String {
        if (quiz.documentPath.isNullOrEmpty()) throw IllegalArgumentException("Document path is null")

        val file = File(quiz.documentPath)
        if (!file.exists() || !file.canRead()) throw IllegalArgumentException("Invalid or unreadable document path")

        val mimeType = "text/plain"
        val tempFile = if (file.extension == "txt") {
            // If the file is already a .txt, use it directly
            file
        } else {
            // Convert PDF or DOCX to text and save as temp.txt
            File("temp.txt").apply {
                when (file.extension.lowercase()) {
                    "pdf" -> {
                        val pdfDocument = PDDocument.load(file)
                        if (pdfDocument.isEncrypted) throw IllegalAccessException("PDF is encrypted and cannot be processed")
                        val textStripper = PDFTextStripper()
                        writeText(textStripper.getText(pdfDocument))
                        pdfDocument.close()
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

        // Define constants
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
        val candidates = responseJson["candidates"].asJsonArray
        return candidates.joinToString("") { candidate ->
            candidate.asJsonObject["content"].asJsonObject["parts"].asJsonArray.joinToString("") { part ->
                part.asJsonObject["text"].asString
            }
        }
    }
}
