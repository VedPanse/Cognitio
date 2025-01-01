package org.cognitio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
        modifier = modifier.then(
            Modifier
                .background(
                    AppTheme.secondaryColor,
                    shape = RoundedCornerShape(5.dp)
                ) // Add background with rounded corners
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(24.dp)
                .width(250.dp)
        ),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = AppTheme.textColor
        ), // Set font size and color
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
                    Text(
                        placeholder,
                        color = Color(0xFF999999),
                        fontSize = 14.sp
                    ) // Placeholder text
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
                .background(
                    color = when (popupType) {
                        PopupType.INFO -> AppTheme.themeColor
                        PopupType.ERROR -> Color.Red
                        PopupType.SUCCESS -> Color.Green
                    }, shape = RoundedCornerShape(20.dp)
                )
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
fun GoButton(text: String, delayTime: Long = 2000, onClick: () -> Unit) {
    var clicked by remember { mutableStateOf(false) }

    // Handle resetting the clicked state with a delay
    if (clicked && !text.contains("...")) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(delayTime)
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

@Composable
fun Dropdown(
    optionList: List<Subject>,
    modifier: Modifier = Modifier,
    onOptionSelected: (Subject) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(optionList.firstOrNull() ?: Subject.MATHEMATICS) }

    Box {
        // Main dropdown box
        Box(
            modifier = modifier
                .background(AppTheme.secondaryColor, shape = RoundedCornerShape(5.dp))
                .width(250.dp)
                .height(40.dp) // Ensure sufficient height for centering
                .clickable { showMenu = !showMenu }, // Toggle menu visibility
            contentAlignment = Alignment.CenterStart // Center vertically, align text to the start horizontally
        ) {
            Text(
                text = selectedOption.toString(),
                color = Color(0xFF999999),
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        // Dropdown menu with LazyColumn
        if (showMenu) {
            LazyColumn(
                modifier = Modifier
                    .background(AppTheme.secondaryColor, shape = RoundedCornerShape(15.dp))
                    .width(250.dp)
                    .height(250.dp) // Fixed height for scrolling
            ) {
                items(optionList) { option ->
                    Box(
                        modifier = Modifier
                            .background(option.bgColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .clickable {
                                selectedOption = option
                                onOptionSelected(option)
                                showMenu = false // Close menu after selection
                            }
                    ) {
                        Text(
                            text = option.toString(),
                            color = option.textColor
                        )
                    }
                }
            }
        }
    }
}
