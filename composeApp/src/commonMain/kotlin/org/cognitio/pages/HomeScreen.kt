package org.cognitio.pages

import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.CustomTextField
import org.cognitio.Line
import org.cognitio.Quiz
import org.cognitio.appName
import org.cognitio.isDesktop
import org.cognitio.recallAllQuizzes


@Composable
fun HomeScreen(showQuiz: (Quiz) -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.8f)
                .fillMaxHeight()
        ) {
            item {
                Text(text = "Start learning with", fontSize = 56.sp, color = Color.Gray)
                Text(
                    text = appName,
                    fontSize = if (isDesktop()) 70.sp else 56.sp,
                )
                Spacer(modifier = Modifier.height(40.dp))

                Line()
                Spacer(modifier = Modifier.height(35.dp))
                Text(
                    text = "Solving a quiz everyday can go a long way. Using AI generated quizzes, this app can help you understand the concepts of anything you want to learn. This application leverages Google Gemini to test you on these concepts.",
                    color = AppTheme.primaryColor
                )
                Spacer(modifier = Modifier.height(35.dp))

                Line()

                Spacer(modifier = Modifier.height(40.dp))

                // TODO render past quizzes
                val displayedQuizzes = recallAllQuizzes().take(4) // Take only the first 4 quizzes
                val thickness: Int = 2


                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.5f)
                            .drawBehind {
                                // Draw the right border with specified thickness and color
                                drawLine(
                                    color = Color.Gray,
                                    start = Offset(size.width - thickness.dp.toPx(), 0f),
                                    end = Offset(size.width - thickness.dp.toPx(), size.height),
                                    strokeWidth = thickness.toFloat() // Set the thickness of the border
                                )
                            }
                    ) {
                        displayedQuizzes.filterIndexed { index, _ -> index % 2 == 0 }.forEach {quiz ->
                            quiz.compose {
                                showQuiz(quiz)
                            }
                            Line()
                        }

                    }

                    Column {
                        displayedQuizzes.filterIndexed { index, _ -> index % 2 == 1 }.forEach {quiz ->
                            Row(modifier = Modifier.fillMaxSize()) {
                                Spacer(modifier = Modifier.width(5.dp))
                                quiz.compose {showQuiz(quiz)}
                            }
                            Line()
                        }
                    }
                }
            }
        }

        if (isDesktop()) {
            var searchedText by remember { mutableStateOf("") }
            CustomTextField("Search", true, getText = { searchedText = it })

            // TODO implement search
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}