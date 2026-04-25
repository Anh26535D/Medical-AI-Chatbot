package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.hust.medicalaichatbot.BuildConfig
import edu.hust.medicalaichatbot.ui.theme.BackgroundGray
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SurfaceGray
import edu.hust.medicalaichatbot.ui.theme.TextGray
import edu.hust.medicalaichatbot.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val isGuest by authViewModel.isGuest.collectAsState()
    val isLoggedIn = currentUser != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt tài khoản", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = PrimaryBlue,
                    navigationIconContentColor = PrimaryBlue
                )
            )
        },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (!isLoggedIn) {
                GuestAccountPlaceholder(onLoginClick)
            } else {
                // User Info Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            color = PrimaryBlue.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            val user = currentUser!!
                            Text(text = user.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = user.phoneNumber, fontSize = 14.sp, color = TextGray)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(text = "TÀI KHOẢN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
                
                SettingsGroup {
                    SettingsItem(icon = Icons.Default.Edit, title = "Chỉnh sửa thông tin", onClick = {})
                    SettingsItem(icon = Icons.Default.Lock, title = "Thay đổi mật khẩu", onClick = {})
                    SettingsItem(icon = Icons.Default.Notifications, title = "Thông báo", onClick = {})
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(text = "ỨNG DỤNG", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
                
                SettingsGroup {
                    SettingsItem(icon = Icons.Default.Language, title = "Ngôn ngữ", value = "Tiếng Việt", onClick = {})
                    SettingsItem(icon = Icons.Default.Description, title = "Điều khoản sử dụng", onClick = {})
                    SettingsItem(icon = Icons.Default.PrivacyTip, title = "Chính sách bảo mật", onClick = {})
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        authViewModel.logout()
                        onLogoutSuccess()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
            
            if (isLoggedIn || isGuest) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Phiên bản ${BuildConfig.VERSION_NAME}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
fun GuestAccountPlaceholder(onLoginClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Chế độ khách",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Đăng nhập để lưu lịch sử tư vấn, quản lý hồ sơ sức khỏe và sử dụng đầy đủ các tính năng của ứng dụng.",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Đăng nhập / Đăng ký", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(onClick = { /* FAQ or Help */ }) {
                Text(text = "Tìm hiểu thêm về lợi ích tài khoản", color = PrimaryBlue, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 15.sp, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(text = value, fontSize = 14.sp, color = TextGray)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}
