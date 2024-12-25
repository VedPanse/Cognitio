package org.cognitio.pages

import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.Line
import org.cognitio.SearchBar
import org.cognitio.appName

@Composable
fun HomeScreen() {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            item {
                Text(text = "Start learning with", fontSize = 64.sp)
                Text(text = appName, fontSize = 64.sp, color = AppTheme.themeColor)
                Spacer(modifier = Modifier.height(40.dp))

                Line()
                Spacer(modifier = Modifier.height(35.dp))
                Text(text = "Solving a quiz everyday can go a long way. Using AI generated quizzes, this app can help you understand the concepts of anything you want to learn. This application leverages Google Gemini to test you on these concepts.", color = AppTheme.primaryColor)
                Spacer(modifier = Modifier.height(35.dp))

                Line()

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        SearchBar()
        Spacer(modifier = Modifier.width(20.dp))
    }
}