package edu.hust.medicalaichatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.hust.medicalaichatbot.data.local.AppDatabase
import edu.hust.medicalaichatbot.data.repository.AuthRepository
import edu.hust.medicalaichatbot.data.repository.ChatRepositoryImpl
import edu.hust.medicalaichatbot.domain.usecase.chat.GetMessagesUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.SendMessageUseCase
import edu.hust.medicalaichatbot.ui.screens.HistoryScreen
import edu.hust.medicalaichatbot.ui.screens.HomeScreen
import edu.hust.medicalaichatbot.ui.screens.LoginScreen
import edu.hust.medicalaichatbot.ui.screens.OnboardingScreen
import edu.hust.medicalaichatbot.ui.screens.RegisterScreen
import edu.hust.medicalaichatbot.ui.screens.SplashScreen
import edu.hust.medicalaichatbot.ui.theme.MedicalAIChatbotTheme
import edu.hust.medicalaichatbot.ui.viewmodel.AuthViewModel
import edu.hust.medicalaichatbot.ui.viewmodel.ChatViewModel
import edu.hust.medicalaichatbot.utils.Constants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicalAIChatbotTheme {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)
                
                val authRepository = AuthRepository(database.userDao())
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModel.Factory(authRepository)
                )

                val chatRepository = ChatRepositoryImpl(database.chatDao(), Constants.DEFAULT_MODEL)
                val getMessagesUseCase = GetMessagesUseCase(chatRepository)
                val sendMessageUseCase = SendMessageUseCase(chatRepository)
                
                val chatViewModel: ChatViewModel = viewModel(
                    factory = ChatViewModel.Factory(getMessagesUseCase, sendMessageUseCase)
                )

                MedicalApp(authViewModel, chatViewModel)
            }
        }
    }
}

@Composable
fun MedicalApp(authViewModel: AuthViewModel, chatViewModel: ChatViewModel) {
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
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate("home") },
                onSkipLogin = { navController.navigate("home") },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
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
            HomeScreen(
                chatViewModel = chatViewModel,
                onHistoryClick = { navController.navigate("history") }
            )
        }
        composable("history") {
            HistoryScreen(
                onHomeClick = { 
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
