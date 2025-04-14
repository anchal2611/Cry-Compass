package com.dev4.crycompass.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoundedBottomNav(
    selected: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            selected = selected == "home",
            onClick = { onItemSelected("home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.GraphicEq, contentDescription = "Cry") },
            selected = selected == "classifier",
            onClick = { onItemSelected("classifier") },
            label = { Text("Classifier") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Chat") },
            selected = selected == "chatbot",
            onClick = { onItemSelected("chatbot") },
            label = { Text("Chatbot") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            selected = selected == "settings",
            onClick = { onItemSelected("settings") },
            label = { Text("Settings") }
        )
    }
}