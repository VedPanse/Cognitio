package org.cognitio.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.cognitio.AppTheme
import org.cognitio.Qtype
import org.cognitio.Quiz
import org.cognitio.isDesktop
import org.cognitio.line


@Composable
fun feedback(quiz: Quiz) {
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxWidth(if (isDesktop()) 0.6f else 0.95f)
                .fillMaxHeight()
        ) {
            LazyColumn {
                item {
                    Text(
                        text = "Final grade: " + String.format("%.2f", quiz.grade),
                        color = AppTheme.themeColor,
                        fontSize = 24.sp
                    )
                }
                items(quiz.questionList.size) { index ->
                    val question = quiz.questionList[index]
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "${index + 1}. ${question.question}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.primaryColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    when (question.type) {
                        Qtype.MCQ -> {
                            question.options?.withIndex()?.forEach { (index, option) ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = if (question.enteredAnswer == option) AppTheme.themeColor.copy(
                                                alpha = 0.2f
                                            ) else Color.Transparent,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(8.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = option, // Add the letter prefix
                                        fontSize = 16.sp,
                                        color = if (question.enteredAnswer == option) AppTheme.themeColor else AppTheme.textColor,
                                        fontWeight = if (question.enteredAnswer == option) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp)) // Reduced spacing
                            }
                        }

                        else -> {
                            TextField(
                                value = question.enteredAnswer ?: "",
                                onValueChange = {
                                    question.enteredAnswer =
                                        it // Update the unique answer for the question
                                },
                                placeholder = { Text("Enter answer here...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp)
                                    .background(AppTheme.secondaryColor),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.Transparent,
                                    focusedIndicatorColor = AppTheme.themeColor,
                                    unfocusedIndicatorColor = AppTheme.secondaryColor,
                                    textColor = AppTheme.textColor,
                                    cursorColor = AppTheme.themeColor
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = quiz.questionList[index].points.toString(),
                        color = AppTheme.themeColor
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    quiz.questionList[index].feedback?.let { Text(it) }

                    Spacer(modifier = Modifier.height(10.dp))
                    line()
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}