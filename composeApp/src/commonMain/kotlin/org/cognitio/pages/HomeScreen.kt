package org.cognitio.pages

import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.customTextField
import org.cognitio.line
import org.cognitio.Quiz
import org.cognitio.appName
import org.cognitio.isDesktop
import org.cognitio.recallAllQuizzes


@Composable
fun homeScreen(showQuiz: (Quiz) -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.7f)
                .fillMaxHeight()
        ) {
            item {
                Text(text = "Start learning with", fontSize = if (isDesktop()) 56.sp else 48.sp)
                Text(
                    text = appName,
                    fontSize = if (isDesktop()) 70.sp else 48.sp,
                    color = AppTheme.primaryColor
                )
                Spacer(modifier = Modifier.height(40.dp))

                line()
                Spacer(modifier = Modifier.height(35.dp))
                Text(
                    text = "Embark on a journey of daily learning! With AI-generated quizzes tailored to your interests, $appName helps you master concepts and dive deeper into anything you want to explore. Powered by Google Gemini, each quiz challenges you to grow, learn, and thrive.",
                    color = AppTheme.primaryColor
                )
                Spacer(modifier = Modifier.height(35.dp))

                line()

                if (!isDesktop())
                    Spacer(modifier = Modifier.height(35.dp))

                val displayedQuizzes = recallAllQuizzes().reversed().take(4) // Take only the first 4 quizzes
                val thickness: Int = 2

                if (isDesktop()) {
                    if (displayedQuizzes.isEmpty())
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Recent quizzes appear here. Start by clicking the + icon on the left",
                                color = Color.Gray,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    else
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.5f)
                                    .drawBehind {
                                        // Draw the right border with specified thickness and color
                                        drawLine(
                                            color = Color.Gray,
                                            start = Offset(size.width - thickness.dp.toPx(), 0f),
                                            end = Offset(
                                                size.width - thickness.dp.toPx(),
                                                size.height
                                            ),
                                            strokeWidth = thickness.toFloat() // Set the thickness of the border
                                        )
                                    }
                            ) {
                                displayedQuizzes.filterIndexed { index, _ -> index % 2 == 0 }
                                    .forEach { quiz ->
                                        quiz.compose {
                                            showQuiz(quiz)
                                        }
                                        line()
                                    }

                            }

                            Column {
                                displayedQuizzes.filterIndexed { index, _ -> index % 2 == 1 }
                                    .forEach { quiz ->
                                        Row(modifier = Modifier.fillMaxSize()) {
                                            Spacer(modifier = Modifier.width(5.dp))
                                            quiz.compose { showQuiz(quiz) }
                                        }
                                        line()
                                    }
                            }
                        }
                } else {
                    displayedQuizzes.forEach { quiz ->
                        quiz.compose { showQuiz(quiz) }
                        Spacer(modifier = Modifier.height(20.dp))
                        line()
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Spacer(modifier = Modifier.fillMaxWidth(0.3f))

        if (isDesktop()) {
            var searchedText by remember { mutableStateOf("") }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    customTextField("Search", true, getText = { searchedText = it })
                }
                items(recallAllQuizzes().filter { it.search(searchedText) && searchedText.isNotEmpty() }) { quiz ->
                    quiz.compose(false) { showQuiz(quiz) }
                    Spacer(modifier = Modifier.height(20.dp))
                    line()
                }
            }

            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}