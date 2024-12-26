package org.cognitio

import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

        // Send the prompt to Gemini API
        val response = withContext(Dispatchers.IO) {
            try {
                val content = generativeModel.generateContent(message) // Assuming this is of type Content or String
                content.text ?: ""  // Extract the text if it's a Content object, or return an empty string
            } catch (e: Exception) {
                println("Error while sending prompt: ${e.localizedMessage}")
                null
            }
        }

        // If response is null, exit early
        if (response.isNullOrEmpty()) {
            println("Error generating question list")
            return
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
                questionText,
                answer,
                type,
                options
            )
            quiz.questionList.add(question)
        }
    }
}
