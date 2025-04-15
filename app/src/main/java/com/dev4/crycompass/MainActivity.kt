package com.dev4.crycompass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.dev4.crycompass.ui.nav.AppNavGraph
import com.dev4.crycompass.ui.theme.CryCompassTheme // Corrected theme import
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Set the content view using Compose
        setContent {
            // Apply the CryCompassTheme to ensure the whole app uses consistent theming
            CryCompassTheme {
                // Remember the NavController to handle navigation across the app
                val navController = rememberNavController()

                // Set up the app's navigation graph
                AppNavGraph(navController = navController)
            }
        }
    }
}
