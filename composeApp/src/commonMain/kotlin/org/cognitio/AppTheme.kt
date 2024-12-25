package org.cognitio

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*

expect val JetBrainsMono: FontFamily

object AppTheme {
    val bgColor = Color(0xFF333333)
    val textColor = Color(0xFFFFFF)
    val primaryColor = Color(0xFBFF8F)
    val themeColor = Color(0xFF7084FF)
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
