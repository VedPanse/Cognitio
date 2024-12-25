package org.cognitio


import java.time.LocalDate
import java.util.UUID

class Quiz (
    val subject: String,
    val topic: String,
    val numQuestions: IntArray,
    var id: String?,
    val documentPath: String?
){
    var questionList: MutableList<Question> = mutableListOf()
    private val dateModified = LocalDate.now().toString()
    var grade: Double = 0.0

    init {
        if (id == null) id = UUID.randomUUID().toString()
    }

    /**
     * Presents the string implementation of the quiz
     */
    override fun toString(): String {
        return "Quiz(subject='$subject', topic='$topic', numQuestions=${numQuestions.contentToString()}, id=$id, documentPath=$documentPath, questionList=$questionList, dateModified='$dateModified', grade=$grade)"
    }
}