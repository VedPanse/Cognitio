package org.cognitio.pages

import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import org.cognitio.Quiz


@Composable
fun QuizScreen(quiz: Quiz) {
    Text(quiz.toString())
}