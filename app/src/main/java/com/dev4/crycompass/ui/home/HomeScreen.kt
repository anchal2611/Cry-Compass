package com.dev4.crycompass.ui.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dev4.crycompass.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun HomeScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var parentName by remember { mutableStateOf("Parent") }
    var babyName by remember { mutableStateOf("Baby") }
    var babyPhotoUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val document = db.collection("users").document(userId).get().await()
        parentName = document.getString("fullName") ?: "Parent"
        babyName = document.getString("babyName") ?: "Baby"
        babyPhotoUri = document.getString("photoUri")
    }

    Scaffold(
        topBar = { TopBar(parentName, babyName) },
        bottomBar = { RoundedBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { BabyInfoCard(babyName, babyPhotoUri) }
            item { CryHistoryChart() }
            item { CalmStatusCard() }
            item { ChatbotCard(navController) }
            item { ClassifierCard(navController) }
            item { TipsCard() }
            item { RecentActivityCard() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(parentName: String, babyName: String) {
    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
            }
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Hello, $parentName", fontSize = 22.sp, color = Color.Black)
            Text("How’s $babyName?", fontSize = 18.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BabyInfoCard(babyName: String, babyPhotoUri: String?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF6F6)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val painter = rememberAsyncImagePainter(
                model = babyPhotoUri ?: R.drawable.baby_placeholder
            )
            Image(
                painter = painter,
                contentDescription = "Baby Photo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = babyName, fontSize = 20.sp)
        }
    }
}

@Composable
fun CryHistoryChart() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cry History", fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Legend("Hunger", Color(0xFFF4926F))
                Legend("Sleep", Color(0xFFB3E5DC))
                Legend("Discomfort", Color(0xFF80BFEA))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("S", "M", "T", "W", "T", "F").forEach {
                    StackedBar(day = it)
                }
            }
        }
    }
}

@Composable
fun Legend(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun StackedBar(day: String) {
    val hungerHeight = (20..40).random()
    val sleepHeight = (20..40).random()
    val discomfortHeight = (10..30).random()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height((hungerHeight + sleepHeight + discomfortHeight).dp)
        ) {
            Column {
                Box(modifier = Modifier.height(hungerHeight.dp).fillMaxWidth().background(Color(0xFFF4926F)))
                Box(modifier = Modifier.height(sleepHeight.dp).fillMaxWidth().background(Color(0xFFB3E5DC)))
                Box(modifier = Modifier.height(discomfortHeight.dp).fillMaxWidth().background(Color(0xFF80BFEA)))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(day, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun CalmStatusCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4F9F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Status", fontSize = 18.sp, color = Color.Black)
            Text("Baby is calm 😊", fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun ChatbotCard(navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Need Help?", fontSize = 18.sp, color = Color.Black)
                Text("Talk to our AI Chatbot", fontSize = 14.sp, color = Color.Gray)
            }
            ElevatedButton(onClick = {
                navController.navigate("chatbot")
            }) {
                Text("Chat")
            }
        }
    }
}

@Composable
fun ClassifierCard(navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Cry Analyzer", fontSize = 18.sp, color = Color.Black)
                Text("Start detecting baby’s cry", fontSize = 14.sp, color = Color.Gray)
            }
            ElevatedButton(onClick = {
                navController.navigate("classifier")
            }) {
                Text("Start")
            }
        }
    }
}

@Composable
fun TipsCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1F2EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tips", fontSize = 18.sp, color = Color.Black)
            Text("Make sure the baby is well-fed and comfortable before nap time.", fontSize = 14.sp)
        }
    }
}

@Composable
fun RecentActivityCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DAEF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Recent Activity", fontSize = 18.sp, color = Color.Black)
            Text("Crying detected: Hunger (3:45 PM)", fontSize = 14.sp)
        }
    }
}

@Composable
fun RoundedBottomNav(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
            NavigationBarItem(
                selected = currentRoute == "home",
                onClick = {
                    if (currentRoute != "home") navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = currentRoute == "classifier",
                onClick = {
                    if (currentRoute != "classifier") navController.navigate("classifier")
                },
                icon = { Icon(Icons.Filled.VolumeUp, contentDescription = "Classifier") },
                label = { Text("Classifier") }
            )
            NavigationBarItem(
                selected = currentRoute == "chat",
                onClick = {
                    if (currentRoute != "chat") navController.navigate("chat")
                },
                icon = { Icon(Icons.Filled.Chat, contentDescription = "Chatbot") },
                label = { Text("Chatbot") }
            )
            NavigationBarItem(
                selected = currentRoute == "settings",
                onClick = {
                    if (currentRoute != "settings") navController.navigate("settings")
                },
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                label = { Text("Settings") }
            )
        }
    }
}
