package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import edu.hust.medicalaichatbot.ui.theme.SuccessGreen

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Scaffold(
        topBar = { ProfileTopBar() },
        bottomBar = { ProfileBottomNavigation(onHomeClick, onHistoryClick, onHelpClick) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Hồ sơ sức khỏe",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Quản lý và cập nhật thông tin y tế gia đình.",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "DANH SÁCH HỒ SƠ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Main profile card
            MainProfileCard()
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Dependent profiles
            DependentProfileItem(name = "Bé", role = "Người phụ thuộc", color = Color(0xFF81C784), icon = Icons.Default.Face)
            DependentProfileItem(name = "Mẹ", role = "Người phụ thuộc", color = Color(0xFF64B5F6), icon = Icons.Default.Woman)
            DependentProfileItem(name = "Bố", role = "Người phụ thuộc", color = Color(0xFF90A4AE), icon = Icons.Default.Man)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Add new profile button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F7FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Thêm hồ sơ mới", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Info card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Quản lý gia đình", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bạn có thể thêm hồ sơ cho con cái hoặc người thân lớn tuổi để nhận được tư vấn sức khỏe phù hợp hơn cho cả nhà.",
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainProfileCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = PrimaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Tôi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Chủ tài khoản", fontSize = 12.sp, color = TextGray)
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextGray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoBox(label = "Tuổi", value = "46", subValue = "(1980)", modifier = Modifier.weight(1f))
                InfoBox(label = "Giới tính", value = "Nữ", modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Bệnh nền", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "Cập nhật", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DiseaseTag("Huyết áp cao")
                        DiseaseTag("Tiểu đường Type 2")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBox(label: String, value: String, subValue: String? = null, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF5F7FA)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 12.sp, color = TextGray)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (subValue != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = subValue, fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
        }
    }
}

@Composable
fun DiseaseTag(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SuccessGreen.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 11.sp,
            color = SuccessGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DependentProfileItem(name: String, role: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F7FA)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = role, fontSize = 12.sp, color = TextGray)
            }
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextGray)
        }
    }
}

@Composable
fun ProfileTopBar() {
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
fun ProfileBottomNavigation(onHomeClick: () -> Unit, onHistoryClick: () -> Unit, onHelpClick: () -> Unit) {
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
                icon = { Icon(Icons.Filled.Person, null, modifier = Modifier.size(24.dp)) },
                label = { Text(stringResource(R.string.nav_profile), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                selected = true,
                onClick = {},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Outlined.HelpOutline, null, modifier = Modifier.size(24.dp)) },
                label = { Text(stringResource(R.string.nav_help), fontSize = 12.sp) },
                selected = false,
                onClick = onHelpClick,
                colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
            )
        }
    }
}
