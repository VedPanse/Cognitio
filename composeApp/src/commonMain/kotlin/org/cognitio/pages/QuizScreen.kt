package org.cognitio.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.cognitio.AppTheme
import org.cognitio.GoButton
import org.cognitio.Line
import org.cognitio.QType
import org.cognitio.Quiz
import org.cognitio.isDesktop
import kotlinx.coroutines.launch
import org.cognitio.GeminiServer
import org.cognitio.apiKey

@Composable
fun QuizScreen(quiz: Quiz) {
    var currentIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope() // Create a coroutine scope for asynchronous work
    var percentage by remember { mutableStateOf(0.0) }
    var graded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        // Left Column: Question Numbers
        if (isDesktop()) {
            Column(
                modifier = Modifier.fillMaxWidth(0.2f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    items(quiz.questionList.size) { index ->
                        Text(
                            text = "Question ${index + 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (index == currentIndex) AppTheme.primaryColor else AppTheme.textColor,
                            modifier = Modifier
                                .clickable { currentIndex = index }
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Line()
                Spacer(modifier = Modifier.height(40.dp))
                GoButton("Submit quiz", onClick = {
                    scope.launch {
                        GeminiServer(apiKey).gradeQuestions(quiz)
                        graded = true
                        percentage =
                            quiz.questionList.sumOf { it.points } / (quiz.questionList.size)
                    }
                })

                Spacer(modifier = Modifier.height(30.dp))

                if (graded) {
                    Text(
                        "Final Quiz Grade",
                        color = AppTheme.themeColor,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        "$percentage%",
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        // Right Column: Render current question
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight()
                .padding(start = 16.dp),
        ) {
            item {
                val question = quiz.questionList[currentIndex]

                if (graded && !isDesktop()) {
                    Text(
                        "Final Quiz Grade: ${percentage}%",
                        color = AppTheme.themeColor,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Question Text
                Text(
                    text = "${currentIndex + 1}. ${question.question}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.primaryColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Render Answer Options or TextField
                when (question.type) {
                    QType.MCQ -> {
                        question.options?.withIndex()?.forEach { (index, option) ->
                            val prefix =
                                ('A' + index).toString() // Calculate the letter (A, B, C, ...)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (question.enteredAnswer == option) AppTheme.themeColor.copy(
                                            alpha = 0.2f
                                        ) else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(8.dp)
                                    .clickable { question.enteredAnswer = option },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "$prefix. $option", // Add the letter prefix
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
                            value = question.enteredAnswer as? String ?: "",
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

                // Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = if (currentIndex > 0) AppTheme.themeColor else AppTheme.secondaryColor,
                            disabledBackgroundColor = Color.Transparent,
                            disabledContentColor = Color.LightGray
                        ),
                        border = null,
                        elevation = null,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous",
                            modifier = Modifier.size(20.dp) // Adjusted icon size
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Previous",
                            color = if (currentIndex > 0) AppTheme.themeColor else Color.LightGray
                        )
                    }

                    Button(
                        onClick = { if (currentIndex < quiz.questionList.size - 1) currentIndex++ },
                        enabled = currentIndex < quiz.questionList.size - 1,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = if (currentIndex < quiz.questionList.size - 1) AppTheme.themeColor else AppTheme.secondaryColor,
                            disabledBackgroundColor = Color.Transparent,
                            disabledContentColor = Color.LightGray
                        ),
                        border = null,
                        elevation = null,
                    ) {
                        Text(
                            text = "Next",
                            color = if (currentIndex < quiz.questionList.size - 1) AppTheme.themeColor else Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            modifier = Modifier.size(20.dp) // Adjusted icon size
                        )
                    }
                }
                if (graded) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Line()
                    Spacer(modifier = Modifier.height(40.dp))
                    quiz.questionList[currentIndex].feedback?.let { Text(it) }
                }

                if (!isDesktop()) {
                    GoButton("Submit quiz", onClick = {
                        scope.launch {
                            GeminiServer(apiKey).gradeQuestions(quiz)
                            graded = true
                            percentage = quiz.questionList.sumOf { it.points } / (quiz.questionList.size)
                        }
                    })
                }
            }
        }
    }
}
