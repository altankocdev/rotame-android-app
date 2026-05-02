package com.altankoc.rotame.feature.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.rotame.core.ui.theme.*
import com.altankoc.rotame.feature.profile.domain.model.User
import com.altankoc.rotame.feature.profile.presentation.state.ProfileActionUiState
import com.altankoc.rotame.feature.profile.presentation.state.ProfileUiState
import com.altankoc.rotame.feature.profile.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToLocationList: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editFirstName by remember { mutableStateOf("") }
    var editLastName by remember { mutableStateOf("") }
    var editUsername by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            val user = (uiState as ProfileUiState.Success).user
            editFirstName = user.firstName
            editLastName = user.lastName
            editUsername = user.username
        }
    }

    LaunchedEffect(actionState) {
        if (actionState is ProfileActionUiState.Success) {
            viewModel.resetActionState()
            onLogout()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Çıkış Yap", fontWeight = FontWeight.Bold) },
            text = { Text("Hesabından çıkış yapmak istediğine emin misin?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Çıkış Yap", color = OnError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Vazgeç", color = OnSurfaceVariant)
                }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hesabı Sil", fontWeight = FontWeight.Bold, color = Error) },
            text = { Text("Bu işlem geri alınamaz. Tüm verilerin kalıcı olarak silinecektir.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteMe()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hesabımı Sil", color = OnError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal", color = OnSurfaceVariant)
                }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Surface,
            title = { Text("Profili Düzenle", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editFirstName,
                        onValueChange = { editFirstName = it },
                        label = { Text("Ad") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        )
                    )
                    OutlinedTextField(
                        value = editLastName,
                        onValueChange = { editLastName = it },
                        label = { Text("Soyad") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        )
                    )
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Kullanıcı adı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        )
                    )
                    if (actionState is ProfileActionUiState.Error) {
                        Text(
                            text = (actionState as ProfileActionUiState.Error).message,
                            color = Error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateMe(
                            firstName = editFirstName.ifBlank { null },
                            lastName = editLastName.ifBlank { null },
                            username = editUsername.ifBlank { null }
                        )
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = actionState !is ProfileActionUiState.Loading
                ) {
                    if (actionState is ProfileActionUiState.Loading) {
                        CircularProgressIndicator(color = OnPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Kaydet", color = OnPrimary)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("İptal", color = OnSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeaderSection(uiState)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionTitle("Yolculuğum")

                ProfileMenuCard(
                    icon = Icons.Rounded.Map,
                    title = "Kayıtlı Konumlarım",
                    subtitle = "Rotalarını ve noktalarını gör",
                    onClick = onNavigateToLocationList
                )

                ProfileMenuCard(
                    icon = Icons.Rounded.Favorite,
                    title = "Favorilerim",
                    subtitle = "Beğendiğin tüm keşifler",
                    iconContainerColor = ErrorContainer,
                    iconTint = Error,
                    onClick = onNavigateToFavorites
                )

                SectionTitle("Hesap Ayarları")

                ProfileMenuCard(
                    icon = Icons.Rounded.Edit,
                    title = "Profili Düzenle",
                    subtitle = "Bilgilerini güncel tut",
                    onClick = { showEditDialog = true }
                )

                ProfileMenuCard(
                    icon = Icons.Rounded.Logout,
                    title = "Çıkış Yap",
                    subtitle = "Oturumu güvenli şekilde kapat",
                    onClick = { showLogoutDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Error.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Hesabı Kalıcı Olarak Sil",
                        color = Error.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeaderSection(uiState: ProfileUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .background(
                    brush = Brush.verticalGradient(colors = listOf(Primary, Secondary)),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState) {
                is ProfileUiState.Success -> ProfileContent(user = uiState.user)
                is ProfileUiState.Loading -> CircularProgressIndicator(color = OnPrimary)
                is ProfileUiState.Error -> Text(uiState.message, color = OnPrimary)
                else -> Unit
            }
        }
    }
}

@Composable
private fun ProfileContent(user: User) {
    Surface(
        modifier = Modifier.size(100.dp),
        shape = CircleShape,
        color = Surface,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = user.firstName.take(1).uppercase() + user.lastName.take(1).uppercase(),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = user.fullName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

    Text(
        text = "@${user.username}",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White.copy(alpha = 0.8f)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Surface(
        color = Color.Black.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = user.email,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun ProfileMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconContainerColor: Color = PrimaryContainer.copy(alpha = 0.4f),
    iconTint: Color = Primary,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Surface,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconContainerColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                Text(text = subtitle, fontSize = 13.sp, color = OnSurfaceVariant)
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Outline,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}