package org.cognitio.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.cognitio.CustomTextField


@Composable
fun SettingsScreen() {
    var newAPIKey by remember { mutableStateOf("") }
    Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))

    CustomTextField("Enter your API key", true, getText = { newAPIKey = it}) { println(newAPIKey) }
}