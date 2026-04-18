package edu.hust.medicalaichatbot.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import edu.hust.medicalaichatbot.domain.model.TriageTag
import edu.hust.medicalaichatbot.ui.theme.BackgroundGray
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SuccessGreen
import edu.hust.medicalaichatbot.ui.theme.SurfaceGray
import edu.hust.medicalaichatbot.ui.theme.TextGray
import edu.hust.medicalaichatbot.ui.viewmodel.ChatViewModel
import edu.hust.medicalaichatbot.utils.LlmParser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel = viewModel(),
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    val messages = chatViewModel.messages.collectAsLazyPagingItems()
    val isLoading by chatViewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        if (chatViewModel.currentThreadId.value == null) {
            chatViewModel.setCurrentThread("default_thread")
        }
    }
    
    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { 
            HomeBottomNavigation(
                onHistoryClick = onHistoryClick,
                onProfileClick = onProfileClick,
                onHelpClick = onHelpClick,
                onSendMessage = { chatViewModel.sendMessage(it) }
            ) 
        },
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
        }
    }
}

@Composable
fun HomeTopBar() {
    Surface(
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Trợ lý sức khỏe AI",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = SurfaceGray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = PrimaryBlue)
                }
            }
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
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                val dateString = SimpleDateFormat("dd MMMM", Locale.forLanguageTag("vi")).format(Date())
                Text(
                    text = stringResource(R.string.today_label, dateString),
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
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

        if (isLoading) {
            item {
                AiLoadingIndicator()
            }
        }
    }
}

@Composable
fun AiMessage(text: String, timestamp: Long) {
    val parsedResponse = remember(text) { LlmParser.parse(text) }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(modifier = Modifier.fillMaxWidth().padding(end = 40.dp), horizontalArrangement = Arrangement.Start) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = PrimaryBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SmartToy, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Surface(
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Hiển thị phần Nhận định Sơ bộ và Triệu chứng nếu có
                    if (parsedResponse.diagnosisGuess != null || parsedResponse.symptomsObserved.isNotEmpty()) {
                        AiAssessmentSection(parsedResponse)
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SurfaceGray)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = parsedResponse.message,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.Black
                    )

                    parsedResponse.triageTag?.let { tag ->
                        Spacer(modifier = Modifier.height(12.dp))
                        TriageLevelBox(tag)
                        Spacer(modifier = Modifier.height(12.dp))
                        TriageActionButtons(tag)
                    }
                }
            }
            Text(
                text = timeFormat.format(Date(timestamp)),
                fontSize = 11.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun AiAssessmentSection(response: edu.hust.medicalaichatbot.utils.ParsedLlmResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MedicalServices, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bác sĩ AI nhận định",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
        
        if (response.diagnosisGuess != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Dự đoán: ${response.diagnosisGuess}",
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }

        if (response.symptomsObserved.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(text = "Triệu chứng: ", fontSize = 12.sp, color = TextGray)
                Text(
                    text = response.symptomsObserved.joinToString(", "),
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun TriageLevelBox(tag: TriageTag) {
    val (color, title, description) = when (tag) {
        TriageTag.RED -> Triple(Color.Red, "CẤP CỨU NGAY (Mức 4)", "Triệu chứng nguy hiểm tính mạng. Cần can thiệp y tế ngay lập tức.")
        TriageTag.ORANGE -> Triple(Color(0xFFFFA500), "CẦN ĐI KHÁM (Mức 3)", "Triệu chứng cần bác sĩ chẩn đoán sớm để tránh diễn biến xấu.")
        TriageTag.YELLOW -> Triple(Color(0xFFFFD700), "HỎI DƯỢC SĨ (Mức 2)", "Triệu chứng nhẹ, có thể điều trị bằng thuốc không kê đơn dưới sự hướng dẫn.")
        TriageTag.GREEN -> Triple(SuccessGreen, "TỰ CHĂM SÓC (Mức 1)", "Tình trạng ổn định. Có thể tự theo dõi và chăm sóc tại nhà.")
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (tag == TriageTag.RED) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = color
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun TriageActionButtons(tag: TriageTag) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (tag) {
            TriageTag.RED -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:115"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Phone, null)
                    Spacer(Modifier.width(8.dp))
                    Text("GỌI CẤP CỨU 115", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=bệnh viện gần nhất"))
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tìm bệnh viện gần đây")
                }
            }
            TriageTag.ORANGE -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = android.provider.CalendarContract.Events.CONTENT_URI
                            putExtra(android.provider.CalendarContract.Events.TITLE, "Khám bệnh (Hẹn từ AI Chatbot)")
                            putExtra(android.provider.CalendarContract.Events.DESCRIPTION, "Tư vấn sức khỏe định kỳ")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Đặt lịch khám bác sĩ")
                }
            }
            TriageTag.YELLOW -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=nhà thuốc gần nhất"))
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocalPharmacy, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tìm nhà thuốc gần nhất")
                }
            }
            TriageTag.GREEN -> {
                // No specific action button for GREEN yet, or can add "Health Tips"
            }
        }
    }
}

@Composable
fun UserMessage(text: String, timestamp: Long) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth().padding(start = 60.dp), horizontalArrangement = Arrangement.End) {
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 0.dp),
                color = PrimaryBlue
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(14.dp),
                    fontSize = 15.sp,
                    color = Color.White,
                    lineHeight = 22.sp
                )
            }
            Text(
                text = timeFormat.format(Date(timestamp)),
                fontSize = 11.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun AiLoadingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
        Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = SurfaceGray) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SmartToy, null, modifier = Modifier.size(16.dp), tint = TextGray)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "AI đang phân tích triệu chứng...",
            fontSize = 13.sp,
            color = TextGray,
            modifier = Modifier
                .background(SurfaceGray, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun MessageInput(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceGray
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Mô tả triệu chứng của bạn...", fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                maxLines = 5
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSendMessage(text)
                    text = ""
                }
            },
            modifier = Modifier.size(48.dp),
            enabled = text.isNotBlank()
        ) {
            Surface(
                shape = CircleShape,
                color = if (text.isNotBlank()) PrimaryBlue else Color.LightGray,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation(
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSendMessage: ((String) -> Unit)? = null
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column {
            if (onSendMessage != null) {
                MessageInput(onSendMessage = onSendMessage)
                HorizontalDivider(color = SurfaceGray.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(R.string.nav_home),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    selected = true,
                    onClick = {},
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f),
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Outlined.History,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(R.string.nav_history),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    selected = false,
                    onClick = onHistoryClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Outlined.PersonOutline,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(R.string.nav_profile),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    selected = false,
                    onClick = onProfileClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(R.string.nav_help),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    selected = false,
                    onClick = onHelpClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
            }
        }
    }
}
