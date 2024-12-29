package org.cognitio.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cognitio.CustomTextField
import org.cognitio.Line
import org.cognitio.Quiz
import org.cognitio.recallAllQuizzes


@Composable
fun SearchScreen(showQuiz: (Quiz) -> Unit) {
    var searchedText by remember { mutableStateOf("") }

    MaterialTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                CustomTextField("Search", true, getText = { searchedText = it })
            }
            items(recallAllQuizzes().filter { it.search(searchedText) && searchedText.isNotEmpty() }) { quiz ->
                quiz.compose(false) { showQuiz(quiz) }
                Spacer(modifier = Modifier.height(20.dp))
                Line()
            }
        }
    }
}