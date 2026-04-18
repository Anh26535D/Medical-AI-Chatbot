package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.hust.medicalaichatbot.R
import edu.hust.medicalaichatbot.ui.theme.BackgroundGray
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SurfaceGray
import edu.hust.medicalaichatbot.ui.theme.TextGray

@Composable
fun HelpScreen(
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = { HelpTopBar() },
        bottomBar = { HelpBottomNavigation(onHomeClick, onHistoryClick, onProfileClick) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SearchBarPlaceholder()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Instruction card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = PrimaryBlue.copy(alpha = 0.05f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryBlue.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Hướng dẫn sử dụng", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Câu hỏi thường gặp", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Xem tất cả", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FaqItem("Làm thế nào để đặt lịch hẹn với bác sĩ chuyên khoa?")
            FaqItem("Kết quả khám bệnh sẽ được bảo mật như thế nào?")
            FaqItem("Ứng dụng có hỗ trợ tư vấn sức khỏe từ xa không?")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Liên hệ hỗ trợ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Hotline button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = PrimaryBlue
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Gọi tổng đài (24/7)", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "1900 6868",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Phiên bản ứng dụng 2.4.0 (2026)",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 12.sp,
                color = TextGray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SearchBarPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Bạn cần hỗ trợ điều gì?", color = TextGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun FaqItem(question: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = question, modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color.Black)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextGray)
        }
    }
}

@Composable
fun HelpTopBar() {
    Surface(color = Color.Transparent) {
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
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.app_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun HelpBottomNavigation(onHomeClick: () -> Unit, onHistoryClick: () -> Unit, onProfileClick: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(80.dp)
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Outlined.Home, null, modifier = Modifier.size(24.dp)) },
                label = { Text(stringResource(R.string.nav_home), fontSize = 12.sp) },
                selected = false,
                onClick = onHomeClick,
                colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
            )
            NavigationBarItem(
                icon = { Icon(Icons.Outlined.History, null, modifier = Modifier.size(24.dp)) },
                label = { Text(stringResource(R.string.nav_history), fontSize = 12.sp) },
                selected = false,
                onClick = onHistoryClick,
                colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
            )
            NavigationBarItem(
                icon = { Icon(Icons.Outlined.PersonOutline, null, modifier = Modifier.size(24.dp)) },
                label = { Text(stringResource(R.string.nav_profile), fontSize = 12.sp) },
                selected = false,
                onClick = onProfileClick,
                colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
            )
            NavigationBarItem(
                icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, null, modifier = Modifier.size(24.dp)) },
                label = { Text(stringResource(R.string.nav_help), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                selected = true,
                onClick = {},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}
