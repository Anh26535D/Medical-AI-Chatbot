package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import edu.hust.medicalaichatbot.R
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.ui.theme.*
import edu.hust.medicalaichatbot.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel = viewModel(),
    onHistoryClick: () -> Unit = {}
) {
    val messages = chatViewModel.messages.collectAsLazyPagingItems()
    val isLoading by chatViewModel.isLoading.collectAsState()
    
    // Ensure we have a thread set (for development/testing if not set by navigation)
    LaunchedEffect(Unit) {
        if (chatViewModel.currentThreadId.value == null) {
            chatViewModel.setCurrentThread("default_thread")
        }
    }
    
    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomNavigation(onHistoryClick) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ChatSection(
                messages = messages,
                isLoading = isLoading,
                modifier = Modifier.weight(1f)
            )
            QuickActionsSection()
            MessageInput(onSendMessage = { chatViewModel.sendMessage(it) })
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = PrimaryBlue
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.app_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
        
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun ChatSection(
    messages: LazyPagingItems<ChatMessage>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.itemCount) {
        if (messages.itemCount > 0) {
            listState.animateScrollToItem(messages.itemCount - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val dateString = SimpleDateFormat("dd MMMM", Locale.forLanguageTag("vi")).format(Date())
                Text(
                    text = stringResource(R.string.today_label, dateString),
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier
                        .background(SurfaceGray, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        items(
            count = messages.itemCount,
            key = messages.itemKey { it.id },
            contentType = messages.itemContentType { "chat_message" }
        ) { index ->
            val message = messages[index]
            if (message != null) {
                if (message.role == MessageRole.ASSISTANT || message.role == MessageRole.ERROR) {
                    AiMessage(text = message.content, timestamp = message.timestamp)
                } else {
                    UserMessage(text = message.content, timestamp = message.timestamp)
                }
            }
        }

        if (messages.itemCount == 1) {
            val firstMessage = messages[0]
            if (firstMessage != null && (firstMessage.role == MessageRole.ASSISTANT || firstMessage.role == MessageRole.ERROR)) {
                item {
                    UserSelectionSection()
                }
            }
        }

        if (isLoading) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = SurfaceGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.ai_analyzing),
                        fontSize = 12.sp,
                        color = TextGray,
                        modifier = Modifier
                            .background(SurfaceGray, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiMessage(text: String, timestamp: Long = System.currentTimeMillis()) {
    val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = PrimaryBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Surface(
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = Color.White,
                tonalElevation = 1.dp
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
            Text(
                text = timeFormat.format(Date(timestamp)),
                fontSize = 10.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun UserMessage(text: String, timestamp: Long = System.currentTimeMillis()) {
    val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 0.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = PrimaryBlue
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
            Text(
                text = timeFormat.format(Date(timestamp)),
                fontSize = 10.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = UserAvatarGreen
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun UserSelectionSection() {
    Column(modifier = Modifier.padding(start = 44.dp)) {
        Text(text = stringResource(R.string.ask_for_whom), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SelectionAvatar(stringResource(R.string.label_me), true)
            SelectionAvatar(stringResource(R.string.label_dad), false)
            SelectionAvatar(stringResource(R.string.label_mom), false)
            SelectionAvatar(stringResource(R.string.label_baby), false)
        }
    }
}

@Composable
fun SelectionAvatar(label: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) PrimaryBlue else Color.Transparent,
                    shape = CircleShape
                ),
            shape = CircleShape,
            color = if (isSelected) Color.White else SurfaceGray
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (label == stringResource(R.string.label_me)) Icons.Default.Person else Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    tint = if (isSelected) PrimaryBlue else TextGray
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) PrimaryBlue else TextGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun QuickActionsSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.quick_actions_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(stringResource(R.string.action_add_symptom), Icons.Default.AddCircleOutline, LightBlue, PrimaryBlue, Modifier.weight(1f))
            QuickActionCard(stringResource(R.string.action_emergency), Icons.Default.Emergency, EmergencyRed, Color.Red, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(stringResource(R.string.action_ask_pharmacist), Icons.Default.MedicalServices, SurfaceGray, TextGray, Modifier.weight(1f))
            QuickActionCard(stringResource(R.string.action_see_doctor), Icons.Default.MedicalInformation, SuccessGreen, Color(0xFF4CAF50), Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionCard(label: String, icon: ImageVector, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(shape = CircleShape, color = bgColor, modifier = Modifier.size(32.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun MessageInput(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(30.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AttachFile, contentDescription = null, tint = TextGray)
            Spacer(modifier = Modifier.width(8.dp))
            
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.input_placeholder),
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = false,
                maxLines = 4
            )
            
            Icon(Icons.Default.Mic, contentDescription = null, tint = TextGray)
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                    }
                },
                modifier = Modifier.size(40.dp),
                enabled = text.isNotBlank()
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (text.isNotBlank()) PrimaryBlue else Color.LightGray
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation(onHistoryClick: () -> Unit = {}) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_home)) },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = LightBlue
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_history)) },
            selected = false,
            onClick = onHistoryClick,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.PersonOutline, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_profile)) },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_help)) },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
    }
}
