package org.cognitio

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okio.Path

const val PASSKEY = "I_AM_SURE_I_AM_A_PASSKEY"


expect fun getJSONFilePath(): Path


fun addQuiz(quiz: Quiz) {
    val existingJson = readJsonFile()
    val quizJson = JsonObject()

    // Add basic quiz properties
    quizJson.addProperty("subject", quiz.subject)
    quizJson.addProperty("topic", quiz.topic)
    quizJson.add("numQuestions", gson.toJsonTree(quiz.numQuestions))
    quizJson.addProperty("grade", quiz.grade)
    quizJson.addProperty("documentPath", quiz.documentPath)

    // Process and clean up the question list
    val questionListJson = quiz.questionList.map { question ->
        val questionJson = gson.toJsonTree(question).asJsonObject

        // Clean up enteredAnswer field
        if (questionJson.has("enteredAnswer\$delegate")) {
            val enteredAnswer = questionJson["enteredAnswer\$delegate"]
                ?.asJsonObject?.get("next")
                ?.asJsonObject?.get("value")
                ?.asString
            questionJson.addProperty("enteredAnswer", enteredAnswer ?: "")
            questionJson.remove("enteredAnswer\$delegate") // Remove unnecessary field
        }

        questionJson
    }

    quizJson.add("questionList", gson.toJsonTree(questionListJson))

    // Combine with existing JSON
    val quizId = quiz.id ?: "unknown"
    existingJson.add(quizId, quizJson)

    // Write back to the file
    writeJsonFile(existingJson)
}


fun quizToJSON(quiz: Quiz): String {
    val quizJson = JsonObject()
    quizJson.addProperty("subject", quiz.subject)
    quizJson.addProperty("topic", quiz.topic)
    quizJson.add("numQuestions", gson.toJsonTree(quiz.numQuestions))
    quizJson.add("questionList", gson.toJsonTree(quiz.questionList))
    quizJson.addProperty("grade", quiz.grade)
    quizJson.addProperty("documentPath", quiz.documentPath)
    return gson.toJson(quizJson)
}


fun recallQuiz(id: String): Quiz {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Question::class.java, QuestionDeserializer())
        .create()

    val existingJson = readJsonFile()

    val quizJson = existingJson.getAsJsonObject(id)
        ?: return Quiz("unknown", "unknown", intArrayOf(), null, null)

    val subject = quizJson.get("subject").asString
    val topic = quizJson.get("topic").asString
    val numQuestions = gson.fromJson(quizJson.get("numQuestions"), IntArray::class.java)
    val documentPath = quizJson.get("documentPath")?.asString

    val quiz = Quiz(subject, topic, numQuestions, id, documentPath)
    quiz.grade = quizJson.get("grade").asDouble
    quiz.questionList = gson.fromJson(
        quizJson.get("questionList"),
        Array<Question>::class.java
    ).toMutableList()

    return quiz
}


fun recallAllQuizzes(): List<Quiz> {
    val quizList = mutableListOf<Quiz>()
    val existingJson = readJsonFile()

    for (id in existingJson.keySet()) {
        quizList.add(recallQuiz(id))
    }
    return quizList.toList()
}


fun removeQuiz(id: String) {
    val existingJson = readJsonFile()
    if (existingJson.has(id)) {
        existingJson.remove(id)
        writeJsonFile(existingJson)
    }
}


fun removeAll(key: String) {
    if (key == PASSKEY) {
        writeJsonFile(JsonObject())
    }
}


fun readJsonFile(): JsonObject {
    if (fileSystem.exists(filePath)) {
        fileSystem.read(filePath) {
            val jsonString = readUtf8()
            return gson.fromJson(jsonString, JsonObject::class.java) ?: JsonObject()
        }
    }
    return JsonObject()
}


fun writeJsonFile(jsonObject: JsonObject) {
    fileSystem.write(filePath) {
        writeUtf8(gson.toJson(jsonObject))
    }
}
