package org.cognitio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Question(
    val question: String,
    val answer: String?,
    val type: Qtype,
    val options: List<String>? = null
) {
    var feedback: String? = null
    var points: Double = 0.0
    var enteredAnswer by mutableStateOf<String?>(null)

    /**
     * Returns string representation
     */
    override fun toString(): String {
        return "Question(question='$question', answer=$answer, type=$type, options=$options, feedback=$feedback, points=$points, enteredAnswer=$enteredAnswer)"
    }
}