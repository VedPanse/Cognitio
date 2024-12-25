package org.eidetic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.cognitio.AppTheme
import org.cognitio.Screen


@Composable
fun Navbar(currentScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.height(30.dp)) // Space between icons

        // Home Icon
        NavbarItem(
            isSelected = currentScreen == Screen.HOME,
            icon = Icons.Filled.Home,
            outlinedIcon = Icons.Outlined.Home,
            onClick = { onScreenSelected(Screen.HOME) }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between icons

        // Add Icon
        NavbarItem(
            isSelected = currentScreen == Screen.QUIZ_FORM,
            icon = Icons.Filled.Add,
            outlinedIcon = Icons.Outlined.Add,
            onClick = { onScreenSelected(Screen.QUIZ_FORM) }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between icons

        // Search Icon
        NavbarItem(
            isSelected = currentScreen == Screen.SEARCH,
            icon = Icons.Filled.Search,
            outlinedIcon = Icons.Outlined.Search,
            onClick = { onScreenSelected(Screen.SEARCH) }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between icons

        // Settings Icon
        NavbarItem(
            isSelected = currentScreen == Screen.DASHBOARD,
            icon = Icons.Filled.Dashboard,
            outlinedIcon = Icons.Outlined.Dashboard,
            onClick = { onScreenSelected(Screen.DASHBOARD) }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between icons

        // Settings Icon
        NavbarItem(
            isSelected = currentScreen == Screen.SETTINGS,
            icon = Icons.Filled.Settings,
            outlinedIcon = Icons.Outlined.Settings,
            onClick = { onScreenSelected(Screen.SETTINGS) }
        )


    }
}

@Composable
fun NavbarItem(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 5.dp) // 5.dp from the left
            .size(28.dp) // Size of the circular background
//            .background(
//                color = if (isSelected) Color.White else Color.Transparent,
//                shape = CircleShape
//            ) // Change background based on selection
            .clickable { onClick() }, // Handle click action
        contentAlignment = Alignment.Center // Align the icon in the center
    ) {
        Icon(
            imageVector = if (isSelected) icon else outlinedIcon,
            contentDescription = null,
            tint = if (isSelected) AppTheme.primaryColor else Color.White, // Icon color remains black
            modifier = Modifier.size(20.dp)
        )
    }
}