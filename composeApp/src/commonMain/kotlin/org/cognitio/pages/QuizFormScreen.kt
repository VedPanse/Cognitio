package org.cognitio.pages

import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.CustomTextField
import org.cognitio.Line
import org.cognitio.Quiz
import org.cognitio.appName
import org.cognitio.isDesktop

@Composable
fun QuizFormScreen(showQuiz: (Quiz) -> Unit) {
    var subject by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp)  // Adding padding to the whole screen for better spacing
            .fillMaxWidth(0.8f)
    ) {
        Text("Generate Quiz", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(40.dp))
        Line()
        Spacer(modifier = Modifier.height(35.dp))

        Text("$appName will use the API key you provided in Settings to generate questions and grade quizzes." +
                " Therefore, content created in the quiz may not be always true.", color = AppTheme.primaryColor)

        Spacer(modifier = Modifier.height(35.dp))
        Line()
        Spacer(modifier = Modifier.height(40.dp))

        // Check if the platform is Desktop or Android
        if (isDesktop()) {
            // Desktop layout: Inputs side by side using Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp) // Spacing between the inputs
            ) {
                CustomTextField(
                    "Enter subject. Ex: History",
                    true,
                    getText = { subject = it },
                    modifier = Modifier.fillMaxWidth(0.4f)  // 40% width for subject input
                )
                CustomTextField(
                    "Enter topic. Ex: World War II",
                    true,
                    getText = { topic = it },
                    modifier = Modifier.fillMaxWidth(0.7f) // 70% width for topic input
                )
            }
        } else {
            // Android layout: Inputs stacked one below the other using Column
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp) // Space between stacked inputs
            ) {
                CustomTextField(
                    "Enter subject. Ex: History",
                    true,
                    getText = { subject = it },
                    modifier = Modifier.fillMaxWidth()
                )
                CustomTextField(
                    "Enter topic. Ex: World War",
                    true,
                    getText = { topic = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
