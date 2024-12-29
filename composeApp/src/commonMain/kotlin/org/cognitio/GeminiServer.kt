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

        var response: String? = null

        if (quiz.documentPath != null) {
            response = docGeneration(quiz, message)
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

        var response: String? = null

        if (quiz.documentPath != null) {
            response = docGeneration(quiz, message)
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

    fun docGeneration(quiz: Quiz, prompt: String): String? {
        if (quiz.documentPath == null) {
            println("Document path is null. Cannot generate.")
            return null
        }

        // Command to activate the virtual environment and run the Python script
        val command = listOf(
            "bash", "-c", // Use bash to execute a shell command
            """
        source myenv/bin/activate && python3 doc-genai.py $apiKey ${quiz.documentPath.replace(" ", "%20")} "$prompt"
        """.trimIndent()
        )

        try {
            // Create a process to execute the command
            val process = ProcessBuilder(command)
                .redirectErrorStream(true) // Merge error stream with output stream
                .start()

            // Capture the output of the Python script
            var output = process.inputStream.bufferedReader().use { it.readText() }
            output = output.split("WARNING: All log messages before absl::InitializeLog()")[0].trimIndent()

            // Wait for the process to complete
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                return output
            } else {
                throw IllegalStateException("Python script execution failed with exit code $exitCode.")
            }
        } catch (e: Exception) {
            throw IllegalStateException("Error running Python script: ${e.localizedMessage}")
        }
    }
}
