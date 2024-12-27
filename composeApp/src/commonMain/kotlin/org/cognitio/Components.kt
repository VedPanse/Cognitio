package org.cognitio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun Line(color: Color = Color.Gray, thickness: Dp = 1.dp, size: Float = 1f) {
    Box(
        modifier = Modifier
            .fillMaxWidth(size)
            .height(thickness)
            .background(color = color)
    )
}


@Composable
fun CustomTextField(
    placeholder: String,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    getText: (String) -> Unit,
    onEnterKeyPressed: () -> Unit = { }
) {
    var text by remember { mutableStateOf("") }

    BasicTextField(
        value = text,
        onValueChange = {
            text = it
            getText(it) // Pass the updated text to the getText function
        },
        modifier = modifier.then(Modifier
            .background(AppTheme.secondaryColor, shape = RoundedCornerShape(5.dp)) // Add background with rounded corners
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(24.dp)
            .width(250.dp)
        ),
        textStyle = TextStyle(fontSize = 14.sp, color = AppTheme.textColor), // Set font size and color
        singleLine = singleLine,
        cursorBrush = SolidColor(AppTheme.themeColor),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (singleLine) ImeAction.Done else ImeAction.Default
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (singleLine) {
                    onEnterKeyPressed() // Trigger the onEnterKeyPressed callback
                }
            }
        ),
        decorationBox = { innerTextField -> // Optional: Customize the text field decoration
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(placeholder, color = Color(0xFF999999), fontSize = 14.sp) // Placeholder text
                }
                innerTextField()
            }
        }
    )
}


enum class PopupType {
    SUCCESS, INFO, ERROR
}


@Composable
fun TimedPopup(message: String, popupType: PopupType, time: Int = 5000) {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(time.toLong())
            isVisible = false // Automatically hide the popup after the delay
        }

        Box(
            modifier = Modifier
                .background(color = when(popupType) {
                    PopupType.INFO -> AppTheme.themeColor
                    PopupType.ERROR -> Color.Red
                    PopupType.SUCCESS -> Color.Green
                }, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (popupType) {
                        PopupType.SUCCESS -> Icons.Default.Check
                        PopupType.INFO -> Icons.Default.Info
                        PopupType.ERROR -> Icons.Default.Warning
                    },
                    contentDescription = null,
                    tint = Color.Black, // Icon color
                    modifier = Modifier.size(24.dp) // Icon size
                )

                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text

                Text(
                    text = message,
                    color = Color.Black,
                    style = MaterialTheme.typography.body1 // Optional: Use theme typography
                )
            }
        }
    }
}


@Composable
fun GoButton(text: String, onClick: () -> Unit) {
    var clicked by remember { mutableStateOf(false) }

    // Handle resetting the clicked state with a delay
    if (clicked && !text.contains("...")) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            clicked = false
        }
    }

    Button(
        onClick = {
            clicked = true
            onClick()
        },
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (clicked) Color.Black else AppTheme.buttonColor,
            contentColor = Color.White // Ensure the text and icon use white color
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically // Ensures text and icon alignment
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(end = 8.dp) // Spacing between text and icon
            )
            Icon(
                imageVector = Icons.Default.ArrowForward, // Proper arrow icon
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
