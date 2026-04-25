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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
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
import edu.hust.medicalaichatbot.ui.components.CommonTopBar
import edu.hust.medicalaichatbot.ui.theme.BackgroundGray
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SurfaceGray
import edu.hust.medicalaichatbot.ui.theme.TextGray
import edu.hust.medicalaichatbot.ui.theme.SuccessGreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.hust.medicalaichatbot.domain.model.UserProfile
import edu.hust.medicalaichatbot.ui.viewmodel.ProfileViewModel

import edu.hust.medicalaichatbot.ui.viewmodel.AuthViewModel
import edu.hust.medicalaichatbot.ui.viewmodel.AuthState

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onLoginClick: () -> Unit = {}
) {
    val userProfiles by profileViewModel.userProfiles.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val isGuest = authState is AuthState.Guest
    
    var editingProfileId by remember { mutableStateOf<Int?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
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
        
        if (isGuest) {
            GuestAccountPlaceholder(onLoginClick = onLoginClick)
        } else {
            Text(
                text = "DANH SÁCH HỒ SƠ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            userProfiles.forEach { profile ->
                val isEditing = editingProfileId == profile.id || (profile.isInitial && profile.isPrimary && !isAddingNew)
                
                if (isEditing) {
                    EditProfileCard(
                        profile = profile,
                        isPrimary = profile.isPrimary,
                        onSave = { name, birthYear, gender, conditions ->
                            profileViewModel.updateProfile(profile.id, name, birthYear, gender, conditions)
                            editingProfileId = null
                        },
                        onCancel = { editingProfileId = null }
                    )
                } else {
                    MainProfileCard(
                        profile = profile,
                        isPrimary = profile.isPrimary,
                        onEditClick = { editingProfileId = profile.id },
                        onUpdateConditions = { newConditions ->
                            profileViewModel.updateProfile(
                                profile.id,
                                profile.name, 
                                profile.birthYear, 
                                profile.gender, 
                                newConditions
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (isAddingNew) {
                EditProfileCard(
                    profile = UserProfile(name = "", isInitial = false),
                    onSave = { name, birthYear, gender, conditions ->
                        profileViewModel.addProfile(name, birthYear, gender, conditions)
                        isAddingNew = false
                    },
                    onCancel = { isAddingNew = false }
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                // Add new profile button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { isAddingNew = true },
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
fun EditProfileCard(
    profile: UserProfile,
    isPrimary: Boolean = false,
    onSave: (String, String, String, List<String>) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var birthYear by remember { mutableStateOf(profile.birthYear) }
    var gender by remember { mutableStateOf(profile.gender) }
    var conditionsInput by remember { mutableStateOf(profile.conditions.joinToString(", ")) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (profile.isInitial && isPrimary) "Nhập thông tin của bạn" 
                       else if (isPrimary) "Chỉnh sửa hồ sơ của bạn"
                       else if (profile.name.isEmpty()) "Thêm hồ sơ mới" 
                       else "Chỉnh sửa hồ sơ người thân", 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!isPrimary) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            OutlinedTextField(
                value = birthYear,
                onValueChange = { birthYear = it },
                label = { Text("Năm sinh") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Giới tính") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = conditionsInput,
                onValueChange = { conditionsInput = it },
                label = { Text("Bệnh nền (cách nhau bởi dấu phẩy)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!profile.isInitial) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hủy")
                    }
                }
                Button(
                    onClick = {
                        val conditions = conditionsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        onSave(name, birthYear, gender, conditions)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Lưu hồ sơ")
                }
            }
        }
    }
}

@Composable
fun MainProfileCard(
    profile: UserProfile,
    isPrimary: Boolean = true,
    onEditClick: () -> Unit,
    onUpdateConditions: (List<String>) -> Unit
) {
    var isEditingConditions by remember { mutableStateOf(false) }
    var conditionsInput by remember { mutableStateOf(profile.conditions.joinToString(", ")) }

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
                    color = if (isPrimary) PrimaryBlue else Color(0xFF81C784)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(if (isPrimary) Icons.Default.Person else Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isPrimary) {
                            if (profile.name.isEmpty() || profile.name == "Tôi") "Chủ tài khoản" else profile.name
                        } else {
                            profile.name
                        },
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    if (!isPrimary) {
                        Text(text = "Người phụ thuộc", fontSize = 12.sp, color = TextGray)
                    }
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = TextGray, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoBox(label = "Năm sinh", value = profile.birthYear, subValue = "(${profile.age} tuổi)", modifier = Modifier.weight(1f))
                InfoBox(label = "Giới tính", value = profile.gender, modifier = Modifier.weight(1f))
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
                        Text(
                            text = if (isEditingConditions) "Lưu" else "Cập nhật",
                            fontSize = 12.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { 
                                if (isEditingConditions) {
                                    val conditions = conditionsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    onUpdateConditions(conditions)
                                    isEditingConditions = false
                                } else {
                                    isEditingConditions = true
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (isEditingConditions) {
                        OutlinedTextField(
                            value = conditionsInput,
                            onValueChange = { conditionsInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Nhập bệnh nền...", fontSize = 12.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    } else {
                        if (profile.conditions.isEmpty()) {
                            Text(text = "Chưa có thông tin bệnh nền", fontSize = 12.sp, color = TextGray)
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                profile.conditions.forEach { condition ->
                                    DiseaseTag(condition)
                                }
                            }
                        }
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

