package com.dev4.crycompass.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev4.crycompass.R
import com.dev4.crycompass.ui.components.RoundedBottomNav

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    parentName: String = "Alex",
    babyName: String = "Luna",
    babyAge: String = "6 months",
    babyPhotoResId: Int = R.drawable.baby_placeholder
) {
    Scaffold(
        bottomBar = {
            RoundedBottomNav(
                selected = "home",
                onItemSelected = { route ->
                    navController.navigate(route)
                }
            )
        },
        backgroundColor = Color(0xFFF9F9F9)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .background(Color(0xFFF9F9F9))
        ) {
            item {
                TopBar(parentName, babyName, navController)
                BabyInfoSection(babyName, babyAge, babyPhotoResId)
                CryHistoryChart()
                RecentActivitySection()
                TipsSection(navController)
            }
        }
    }
}

@Composable
fun TopBar(parentName: String, babyName: String, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    // Handle notification click
                }
        )

        Column {
            Text(text = "Hello, $parentName", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "How's $babyName?", fontSize = 16.sp, color = Color.Gray)
        }

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    navController.navigate("settings")
                }
        )
    }
}

@Composable
fun BabyInfoSection(babyName: String, babyAge: String, photoResId: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = photoResId),
                contentDescription = "Baby Photo",
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = babyName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "$babyAge old", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun CryHistoryChart() {
    Text(
        text = "Cry History",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(16.dp)
            .background(Color(0xFFE0F7FA), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Stacked Bar Chart Here", color = Color.Gray)
    }
}

@Composable
fun RecentActivitySection() {
    Text(
        text = "Recent Activity",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Today - 10:45 AM - Cry detected: Hunger", fontSize = 14.sp)
        }
    }
}

@Composable
fun TipsSection(navController: NavController) {
    Text(
        text = "Tips for You",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Chatbot",
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    navController.navigate("chatbot")
                },
            tint = Color(0xFF7E57C2)
        )
        Icon(
            imageVector = Icons.Outlined.GraphicEq,
            contentDescription = "Cry Classifier",
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    navController.navigate("classifier")
                },
            tint = Color(0xFF29B6F6)
        )
    }
}
