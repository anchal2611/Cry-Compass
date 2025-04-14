package com.dev4.crycompass.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev4.crycompass.ui.auth.LoginScreen
import com.dev4.crycompass.ui.auth.SignupScreen
import com.dev4.crycompass.ui.classifier.CryClassifierScreen
import com.dev4.crycompass.ui.chat.ChatbotScreen
import com.dev4.crycompass.ui.home.HomeScreen
import com.dev4.crycompass.ui.splash.SplashScreen
import com.dev4.crycompass.ui.onboarding.OnboardingScreen
import com.dev4.crycompass.ui.settings.SettingsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController)
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("classifier") {
            CryClassifierScreen(navController)
        }

        composable("chatbot") {
            ChatbotScreen(navController)
        }

        composable("settings") {
            SettingsScreen(navController)
        }
    }
}
