package org.cognitio


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.util.UUID

class Quiz(
    val subject: String,
    val topic: String,
    val numQuestions: IntArray,
    var id: String?,
    val documentPath: String?
) {
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

    fun search(query: String): Boolean {
        var rep: String = listOf(
            subject,
            topic,
            documentPath,
            questionList.joinToString(" ") { it.toString() }).joinToString(" ")

        return rep.lowercase().contains(query.lowercase())
    }

    @Composable
    fun compose(fill: Boolean = true, showQuiz: (Quiz) -> Unit) {
        MaterialTheme {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                subjectRep(listOf(mapSubjectNameToEnum(subject)))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = topic,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                val stringRepresentation: String = String.format("%.2f", grade)

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = AppTheme.themeColor)) {
                            append("Grade: ")
                        }
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append("$stringRepresentation%")
                        }
                    },
                    fontSize = 18.sp
                )


                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    numQuestions.forEachIndexed { i, it ->
                        Column {
                            Text(
                                it.toString(),
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 7.dp)
                            )

                            val list = listOf("MCQ", "SAQ", "LAQ")
                            Text(list[i], color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                var displayText by remember { mutableStateOf("Review Quiz") }

                if (fill)
                    goButton(displayText) {
                        displayText = "Opening Quiz..."
                        showQuiz(this@Quiz)
                    }
                else
                    Button(
                        onClick = {
                            displayText = "Opening Quiz..."
                            showQuiz(this@Quiz)
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = AppTheme.themeColor,
                            disabledBackgroundColor = Color.Transparent,
                            disabledContentColor = Color.LightGray
                        ),
                        border = null,
                        elevation = null,
                    ) {
                        Text(
                            text = "Review Quiz",
                            color = AppTheme.themeColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Review Quiz",
                            modifier = Modifier.size(20.dp) // Adjusted icon size
                        )
                    }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}