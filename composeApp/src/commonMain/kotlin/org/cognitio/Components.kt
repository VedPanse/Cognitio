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
        modifier = Modifier
            .background(AppTheme.secondaryColor, shape = RoundedCornerShape(5.dp)) // Add background with rounded corners
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(24.dp)
            .width(250.dp),
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
