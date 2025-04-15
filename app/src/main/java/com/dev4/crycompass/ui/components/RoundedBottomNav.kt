package com.dev4.crycompass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundedBottomNav(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = selectedItem == "home",
                onClick = { onItemSelected("home") },
                icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                label = { Text("Home") },
                alwaysShowLabel = true
            )
            NavigationBarItem(
                selected = selectedItem == "classifier",
                onClick = { onItemSelected("classifier") },
                icon = { Icon(Icons.Outlined.GraphicEq, contentDescription = "Cry Classifier") },
                label = { Text("Classifier") },
                alwaysShowLabel = true
            )
            NavigationBarItem(
                selected = selectedItem == "chatbot",
                onClick = { onItemSelected("chatbot") },
                icon = { Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Chatbot") },
                label = { Text("Chatbot") },
                alwaysShowLabel = true
            )
            NavigationBarItem(
                selected = selectedItem == "settings",
                onClick = { onItemSelected("settings") },
                icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                alwaysShowLabel = true
            )
        }
    }
}
