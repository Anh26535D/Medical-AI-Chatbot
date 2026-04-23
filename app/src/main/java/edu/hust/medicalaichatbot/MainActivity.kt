package edu.hust.medicalaichatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import edu.hust.medicalaichatbot.data.local.AppDatabase
import edu.hust.medicalaichatbot.data.repository.AuthRepository
import edu.hust.medicalaichatbot.data.repository.ChatRepositoryImpl
import edu.hust.medicalaichatbot.domain.usecase.chat.CreateThreadUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.DeleteThreadUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.GetMessagesUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.GetThreadsUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.SendMessageUseCase
import edu.hust.medicalaichatbot.ui.components.CommonTopBar
import edu.hust.medicalaichatbot.ui.components.MainBottomNavigation
import edu.hust.medicalaichatbot.ui.components.MessageInput
import edu.hust.medicalaichatbot.ui.screens.AccountSettingsScreen
import edu.hust.medicalaichatbot.ui.screens.HelpScreen
import edu.hust.medicalaichatbot.ui.screens.HistoryScreen
import edu.hust.medicalaichatbot.ui.screens.HomeScreen
import edu.hust.medicalaichatbot.ui.screens.LoginScreen
import edu.hust.medicalaichatbot.ui.screens.MedicalSummaryScreen
import edu.hust.medicalaichatbot.ui.screens.OnboardingScreen
import edu.hust.medicalaichatbot.ui.screens.ProfileScreen
import edu.hust.medicalaichatbot.ui.screens.RegisterScreen
import edu.hust.medicalaichatbot.ui.screens.SplashScreen
import edu.hust.medicalaichatbot.ui.theme.BackgroundGray
import edu.hust.medicalaichatbot.ui.theme.MedicalAIChatbotTheme
import edu.hust.medicalaichatbot.ui.viewmodel.AuthState
import edu.hust.medicalaichatbot.ui.viewmodel.AuthViewModel
import edu.hust.medicalaichatbot.ui.viewmodel.ChatViewModel
import edu.hust.medicalaichatbot.ui.viewmodel.HistoryViewModel
import edu.hust.medicalaichatbot.utils.Constants
import edu.hust.medicalaichatbot.utils.PreferenceManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceManager = PreferenceManager(this)
        preferenceManager.updateLastVisit()
        
        enableEdgeToEdge()
        setContent {
            MedicalAIChatbotTheme {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)
                
                val authRepository = AuthRepository(database.userDao(), database.chatDao())
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModel.Factory(authRepository)
                )

                val locationService = edu.hust.medicalaichatbot.data.service.LocationService(context)
                val chatRepository = ChatRepositoryImpl(
                    chatDao = database.chatDao(),
                    modelName = Constants.DEFAULT_MODEL,
                    locationService = locationService
                )
                val getMessagesUseCase = GetMessagesUseCase(chatRepository)
                val sendMessageUseCase = SendMessageUseCase(chatRepository)
                val createThreadUseCase = CreateThreadUseCase(chatRepository)
                
                val chatViewModel: ChatViewModel = viewModel(
                    factory = ChatViewModel.Factory(getMessagesUseCase, sendMessageUseCase, createThreadUseCase)
                )

                val getThreadsUseCase = GetThreadsUseCase(chatRepository)
                val deleteThreadUseCase = DeleteThreadUseCase(chatRepository)
                val historyViewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.Factory(getThreadsUseCase, deleteThreadUseCase, getMessagesUseCase)
                )

                MedicalApp(authViewModel, chatViewModel, historyViewModel, preferenceManager)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicalApp(
    authViewModel: AuthViewModel, 
    chatViewModel: ChatViewModel,
    historyViewModel: HistoryViewModel,
    preferenceManager: PreferenceManager
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isImeVisible = WindowInsets.isImeVisible

    val showBars = currentRoute in listOf("home", "history", "profile", "help")
    var prefillText by remember { mutableStateOf("") }
    val currentThreadId by chatViewModel.currentThreadId.collectAsState()

    // Save thread ID whenever it changes
    LaunchedEffect(currentThreadId) {
        chatViewModel.saveCurrentThreadId(preferenceManager)
    }

    // Restore thread ID on start
    LaunchedEffect(Unit) {
        chatViewModel.restoreLastThread(preferenceManager)
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val userId = (authState as AuthState.Success).user.phoneNumber
                chatViewModel.setUserId(userId)
                historyViewModel.setUserId(userId)
            }

            is AuthState.Guest -> {
                chatViewModel.setUserId("guest")
                historyViewModel.setUserId("guest")
            }

            else -> {
                chatViewModel.setUserId("guest")
                historyViewModel.setUserId("guest")
            }
        }
    }

    Scaffold(
        topBar = {
            if (showBars) {
                val title = when (currentRoute) {
                    "history" -> stringResource(R.string.history_title)
                    "profile" -> "Hồ sơ sức khỏe"
                    "help" -> "Trung tâm hỗ trợ"
                    else -> null
                }
                CommonTopBar(
                    title = title,
                    onProfileClick = { navController.navigate("account_settings") }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                Column(modifier = Modifier.imePadding()) {
                    if (currentRoute == "home") {
                        MessageInput(
                            onSendMessage = { chatViewModel.sendMessage(it) },
                            prefillText = prefillText,
                            onPrefillConsumed = { prefillText = "" },
                            showInitialChips = chatViewModel.messages.collectAsLazyPagingItems().itemCount == 0
                        )
                        androidx.compose.material3.HorizontalDivider(
                            color = edu.hust.medicalaichatbot.ui.theme.SurfaceGray.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                    }
                    if (!isImeVisible) {
                        MainBottomNavigation(navController = navController)
                    }
                }
            }
        },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(onTimeout = {
                        val destination = if (preferenceManager.shouldShowOnboarding()) {
                            "onboarding"
                        } else {
                            "login"
                        }
                        navController.navigate(destination) {
                            popUpTo("splash") { inclusive = true }
                        }
                    })
                }
                composable("onboarding") {
                    OnboardingScreen(onFinish = {
                        preferenceManager.setFirstTimeLaunch(false)
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
                        onSendMessage = { chatViewModel.sendMessage(it) },
                        onQuickReplyClick = { question -> prefillText = question }
                    )
                }
                composable("history") {
                    HistoryScreen(
                        viewModel = historyViewModel,
                        authViewModel = authViewModel,
                        onThreadClick = { threadId ->
                            chatViewModel.setCurrentThread(threadId)
                            navController.navigate("home") {
                                popUpTo("history") { inclusive = false }
                            }
                        },
                        onViewSummary = { threadId ->
                            navController.navigate("summary/$threadId")
                        },
                        onLoginClick = {
                            navController.navigate("login")
                        },
                        onNewChatClick = {
                            chatViewModel.startNewChat()
                            navController.navigate("home") {
                                popUpTo("history") { inclusive = false }
                            }
                        }
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onLoginClick = {
                            navController.navigate("login")
                        }
                    )
                }
                composable("account_settings") {
                    AccountSettingsScreen(
                        authViewModel = authViewModel,
                        onBackClick = { navController.popBackStack() },
                        onLogoutSuccess = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onLoginClick = {
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    )
                }
                composable("help") {
                    HelpScreen()
                }
                composable("summary/{threadId}") { backStackEntry ->
                    val threadId =
                        backStackEntry.arguments?.getString("threadId") ?: return@composable
                    MedicalSummaryScreen(
                        threadId = threadId,
                        viewModel = historyViewModel,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onContinueChat = { id ->
                            chatViewModel.setCurrentThread(id)
                            navController.navigate("home") {
                                popUpTo("history") { inclusive = false }
                            }
                        }
                    )
                }
            }
        }
    }
}
