package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.hust.medicalaichatbot.ui.theme.LightBlue
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.TextGray

@Composable
fun HomeScreen() {
    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomNavigation() },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ChatSection(modifier = Modifier.weight(1f))
            QuickActionsSection()
            MessageInput()
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
                text = "Sức Khỏe Việt AI",
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
            // Profile image placeholder
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun ChatSection(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Hôm nay, 24 Tháng 5",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier
                        .background(Color(0xFFE9ECEF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        item {
            AiMessage(
                text = "Chào Chị, tôi là trợ lý sức khỏe AI của bạn. Hôm nay bạn cảm thấy thế nào ạ? Bạn có cần tôi giúp kiểm tra triệu chứng gì không?"
            )
        }

        item {
            UserSelectionSection()
        }

        item {
            UserMessage(
                text = "Chào bạn, sáng nay tôi thấy hơi đau đầu và nhức mỏi vai gáy. Không biết có phải do thời tiết thay đổi không?"
            )
        }

        item {
            AiMessage(
                text = "Tôi ghi nhận tình trạng đau đầu và mỏi vai gáy của bạn. Để phân tích kỹ hơn, bạn cho tôi hỏi:\n• Cơn đau có lan xuống cánh tay không ạ?\n• Bạn có thấy chóng mặt hay buồn nôn không?"
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFFE9ECEF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI đang phân tích...",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier
                        .background(Color(0xFFE9ECEF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AiMessage(text: String) {
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
                text = "08:15 AM",
                fontSize = 10.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun UserMessage(text: String) {
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
                text = "08:16 AM",
                fontSize = 10.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = Color(0xFF90EE90) // Light green
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
        Text(text = "Bạn đang hỏi cho ai?", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SelectionAvatar("Tôi", true)
            SelectionAvatar("Bố", false)
            SelectionAvatar("Mẹ", false)
            SelectionAvatar("Bé", false)
        }
        Text(
            text = "08:15 AM",
            fontSize = 10.sp,
            color = TextGray,
            modifier = Modifier.padding(top = 8.dp)
        )
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
            color = if (isSelected) Color.White else Color(0xFFE9ECEF)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (label == "Tôi") Icons.Default.Person else Icons.Default.PersonOutline,
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
            text = "Hành động nhanh",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard("Thêm triệu chứng", Icons.Default.AddCircleOutline, Color(0xFFE3F2FD), PrimaryBlue, Modifier.weight(1f))
            QuickActionCard("Cấp cứu", Icons.Default.Emergency, Color(0xFFFFEBEE), Color.Red, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard("Hỏi dược sĩ", Icons.Default.MedicalServices, Color(0xFFF5F5F5), TextGray, Modifier.weight(1f))
            QuickActionCard("Gặp bác sĩ", Icons.Default.MedicalInformation, Color(0xFFE8F5E9), Color(0xFF4CAF50), Modifier.weight(1f))
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
fun MessageInput() {
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
            Text(
                text = "Mô tả triệu chứng của bạn...",
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.Mic, contentDescription = null, tint = TextGray)
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = PrimaryBlue
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Trang chủ") },
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
            label = { Text("Lịch sử") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
            label = { Text("Hồ sơ") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
            label = { Text("Trợ giúp") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
    }
}
