package org.cognitio.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.cognitio.AppTheme
import org.cognitio.CustomTextField
import org.cognitio.Dropdown
import org.cognitio.GeminiServer
import org.cognitio.GoButton
import org.cognitio.Line
import org.cognitio.PopupType
import org.cognitio.Quiz
import org.cognitio.Subject
import org.cognitio.TimedPopup
import org.cognitio.apiKey
import org.cognitio.appName
import org.cognitio.isDesktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.FilenameFilter



@Composable
fun QuizFormScreen(showQuiz: (Quiz) -> Unit, settingsRedirect: () -> Unit) {
    var subject by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var numQuestions by remember { mutableStateOf(List(3) { 1 }) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var documentPath by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(0.8f)
    ) {
        item {
            Text("Generate Quiz", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))
            Line()
            Spacer(modifier = Modifier.height(35.dp))

            Text(
                "$appName will use the API key you provided in Settings to generate questions and grade quizzes. " +
                        "Therefore, content created in the quiz may not always be true.",
                color = AppTheme.primaryColor
            )

            Spacer(modifier = Modifier.height(35.dp))
            Line()
            Spacer(modifier = Modifier.height(40.dp))

            // Render input fields for Subject and Topic
            errorMessage?.let { message ->
                LaunchedEffect(message) {
                    // Clear the error message after 2 seconds
                    kotlinx.coroutines.delay(2000)
                    errorMessage = null
                }
                TimedPopup(message, PopupType.ERROR)
            }

            Spacer(modifier = Modifier.height(10.dp))

            InputFields(
                onSubjectChange = { subject = it.toString() },
                onTopicChange = { topic = it })

            Spacer(modifier = Modifier.height(20.dp))

            val labels = listOf("Multiple-Choice", "Short Answer", "Long Answer")

            Text(
                "Number of Questions",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = AppTheme.themeColor
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Render numeric input fields for number of questions
            NumericInputFields(labels, numQuestions) { index, newValue ->
                numQuestions = numQuestions.toMutableList().apply { set(index, newValue) }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // File Picker Dialog
            if (isDesktop()) {
                FilePickerBox(
                    documentPath = documentPath,
                    onClick = {
                        if (isDesktop()) {
                            documentPath = pickFile()
                        }
                    },
                    textColor = AppTheme.textColor
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Show error message below the button, if errorMessage is not null


            Spacer(modifier = Modifier.height(20.dp))

            var buttonText by remember { mutableStateOf("Generate Quiz") }
            GoButton(buttonText) {
                when {
                    subject.isEmpty() || topic.isEmpty() -> {
                        errorMessage = "Subject and topic fields cannot be empty"
                    }

                    numQuestions.max() == 0 -> {
                        errorMessage = "Each question type must have at least one question"
                    }

                    apiKey.isNullOrEmpty() -> {
                        settingsRedirect()
                    }

                    !isInternetAvailable() -> {
                        errorMessage = "No internet connection available to generate quiz"
                    }

                    else -> {
                        buttonText = "Generating..."
                        val quiz = Quiz(subject, topic, numQuestions.toIntArray(), null, documentPath)

                        scope.launch {
                            try {
                                // Call the method to generate questions asynchronously
                                GeminiServer(apiKey).generateQuestionList(quiz)

                                for (question in quiz.questionList) {
                                    println(question)
                                }

                                showQuiz(quiz) // Show the generated quiz
                                errorMessage = null // Clear any existing error
                            } catch (e: Exception) {
                                errorMessage = "Failed to generate quiz: ${e.localizedMessage}"
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FilePickerBox(
    documentPath: String?,
    onClick: () -> Unit,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                AppTheme.secondaryColor,
                shape = RoundedCornerShape(20.dp)
            ) // Background color of the box
            .clickable(onClick = onClick), // File picker click handler
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Display the "+" icon before the text
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add File",
                tint = AppTheme.themeColor,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Display the text: if a file is selected, show the file name

            Text(
                text = documentPath
                    ?: "(Optional) Select a file to be quizzed on (pdf, docx, or txt extensions only)",
                color = textColor,
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}


fun pickFile(): String? {
    val fileDialog = FileDialog(Frame(), "Select a File", FileDialog.LOAD)

    // Apply a custom filename filter that only allows .pdf, .docx, and .txt files
    fileDialog.filenameFilter = FilenameFilter { _, name ->
        val fileExtension = name.substringAfterLast('.', "").lowercase()
        fileExtension in listOf("pdf", "docx", "txt")
    }

    // Make the dialog visible
    fileDialog.isVisible = true

    // Return the file path if a valid file is selected, otherwise null
    return fileDialog.file?.let {
        val fileExtension = it.substringAfterLast('.', "")
        if (fileExtension in listOf("pdf", "docx", "txt")) {
            "${fileDialog.directory}$it"
        } else {
            null // Return null if the selected file has an invalid extension
        }
    }
}


@Composable
fun InputFields(
    onSubjectChange: (Subject) -> Unit,
    onTopicChange: (String) -> Unit
) {
    if (isDesktop()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Dropdown(Subject.entries.toList(), modifier = Modifier.fillMaxWidth(0.4f).height(34.dp), onSubjectChange)
//            CustomTextField(
//                "Enter subject. Ex: History",
//                true,
//                getText = onSubjectChange,
//                modifier = Modifier.fillMaxWidth(0.4f)
//            )
            CustomTextField(
                "Enter topic. Ex: World War II",
                true,
                getText = onTopicChange,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Dropdown(Subject.entries.toList(), onOptionSelected=onSubjectChange)
//            CustomTextField(
//                "Enter subject. Ex: History",
//                true,
//                getText = onSubjectChange,
//                modifier = Modifier.fillMaxWidth()
//            )
            CustomTextField(
                "Enter topic. Ex: World War",
                true,
                getText = onTopicChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun NumericInputFields(
    labels: List<String>,
    numQuestions: List<Int>,
    onValueChange: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        labels.forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.825f)
            ) {
                Text(label, fontSize = 16.sp)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            AppTheme.secondaryColor,
                            shape = MaterialTheme.shapes.small
                        )
                        .width(40.dp)
                        .height(40.dp)
                        .padding(top = 10.dp)
                ) {
                    BasicTextField(
                        value = numQuestions[index].toString(),
                        onValueChange = {
                            val newValue = it.toIntOrNull() ?: 0
                            onValueChange(index, newValue)
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = AppTheme.textColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxSize(),
                        cursorBrush = SolidColor(AppTheme.primaryColor),
                        decorationBox = { innerTextField -> innerTextField() }
                    )
                }
            }
        }
    }
}


expect fun isInternetAvailable(): Boolean