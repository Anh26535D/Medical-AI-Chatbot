package edu.hust.medicalaichatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.hust.medicalaichatbot.ui.screens.HomeScreen
import edu.hust.medicalaichatbot.ui.screens.LoginScreen
import edu.hust.medicalaichatbot.ui.screens.OnboardingScreen
import edu.hust.medicalaichatbot.ui.screens.RegisterScreen
import edu.hust.medicalaichatbot.ui.screens.SplashScreen
import edu.hust.medicalaichatbot.ui.theme.MedicalAIChatbotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicalAIChatbotTheme {
                MedicalApp()
            }
        }
    }
}

@Composable
fun MedicalApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onSkipLogin = { navController.navigate("home") },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = { 
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("home") {
            HomeScreen()
        }
    }
}
