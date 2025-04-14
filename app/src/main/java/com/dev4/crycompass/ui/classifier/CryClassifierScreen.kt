package com.dev4.crycompass.ui.classifier

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev4.crycompass.ui.components.RoundedBottomNav
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryClassifierScreen(navController: NavController) {
    var isListening by remember { mutableStateOf(false) }
    var detectedCry by remember { mutableStateOf("Calm") }

    // Simulated classification loop
    LaunchedEffect(isListening) {
        if (isListening) {
            while (true) {
                delay(2000)
                detectedCry = listOf("Hunger", "Sleep", "Discomfort", "Calm").random()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cry Classifier", fontSize = 20.sp) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Outlined.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            RoundedBottomNav(
                selected = "classifier",
                onItemSelected = { route ->
                    navController.navigate(route) {
                        popUpTo("home")
                        launchSingleTop = true
                    }
                }
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF6F6))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isListening) "Listening..." else "Tap to Start",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Detected: $detectedCry",
                        fontSize = 24.sp,
                        color = when (detectedCry) {
                            "Hunger" -> Color(0xFFF4926F)
                            "Sleep" -> Color(0xFFB3E5DC)
                            "Discomfort" -> Color(0xFF80BFEA)
                            else -> Color(0xFF4CAF50)
                        }
                    )
                }
            }

            Waveform(isActive = isListening)

            Button(
                onClick = { isListening = !isListening },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isListening) "Stop Detection" else "Start Detection")
            }
        }
    }
}

@Composable
fun Waveform(isActive: Boolean) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFFEEF7F7), shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        val barCount = 20
        val space = size.width / (barCount * 2)

        for (i in 0 until barCount) {
            val height = if (isActive) Random.nextFloat() * size.height else size.height / 4
            val x = i * 2 * space
            drawLine(
                color = Color(0xFF80BFEA),
                start = androidx.compose.ui.geometry.Offset(x, size.height),
                end = androidx.compose.ui.geometry.Offset(x, size.height - height),
                strokeWidth = space
            )
        }
    }
}
