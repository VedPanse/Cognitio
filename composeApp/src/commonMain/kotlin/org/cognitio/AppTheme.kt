package org.cognitio

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*

expect val JetBrainsMono: FontFamily

val appName: String = "Cognitio"

object AppTheme {
    val bgColor = Color(0xFF333333)
    val textColor = Color.White
    val primaryColor = Color(0xFFFBFF8F)
    val themeColor = Color(0xFF7084FF)
    val secondaryColor = Color.DarkGray
}

val AppTypography = Typography(
    h1 = TextStyle(
        fontFamily = JetBrainsMono,
        fontSize = 24.sp,
        color = Color.White
    ),
    body1 = TextStyle(
        fontFamily = JetBrainsMono,
        fontSize = 14.sp,
        color = Color.White
    )
)
