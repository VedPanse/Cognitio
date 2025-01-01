package org.cognitio

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class QuestionDeserializer : JsonDeserializer<Question> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Question {
        val jsonObject = json.asJsonObject

        val question = jsonObject.get("question").asString
        val answer = jsonObject.get("answer")?.asString
        val type = Qtype.valueOf(jsonObject.get("type").asString)
        val options = jsonObject.get("options")?.let {
            context.deserialize<List<String>>(it, List::class.java)
        }
        val feedback = jsonObject.get("feedback")?.asString
        val points = jsonObject.get("points")?.asDouble ?: 0.0
        val enteredAnswer = jsonObject.get("enteredAnswer")?.asString

        return Question(question, answer, type, options).apply {
            this.feedback = feedback
            this.points = points
            this.enteredAnswer = enteredAnswer
        }
    }
}
