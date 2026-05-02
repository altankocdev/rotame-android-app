package com.altankoc.rotame.feature.auth.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.rotame.core.ui.theme.OnPrimary
import com.altankoc.rotame.core.ui.theme.Primary
import com.altankoc.rotame.core.ui.theme.PrimaryContainer
import com.altankoc.rotame.core.ui.theme.Secondary
import com.altankoc.rotame.feature.auth.presentation.state.RegisterUiState
import com.altankoc.rotame.feature.auth.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "buttonScale")

    val uiError = uiState as? RegisterUiState.Error
    val isFirstNameError = uiError?.message?.contains("Ad ") == true
    val isLastNameError = uiError?.message?.contains("Soyad") == true
    val isUsernameError = uiError?.message?.contains("Kullanıcı adı") == true
    val isEmailError = uiError?.message?.contains("E-posta") == true
    val isPasswordError = uiError?.message?.contains("Şifre") == true || uiError?.message?.contains("uyuşmuyor") == true

    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.SuccessWithMessage -> {
                successMessage = (uiState as RegisterUiState.SuccessWithMessage).message
                showSuccessDialog = true
            }
            is RegisterUiState.Success -> {
                onRegisterSuccess()
            }
            else -> {}
        }
    }

    if (showSuccessDialog) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Harika!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = successMessage.ifEmpty { "Kaydın başarıyla oluşturuldu. Şimdi giriş yapabilirsin." },
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.resetState()
                            onNavigateToLogin()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Giriş Yap", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Primary, Secondary, Primary.copy(alpha = 0.9f)),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f)
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = (-80).dp, y = (-50).dp)
                    .background(PrimaryContainer, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 60.dp, y = 60.dp)
                    .background(Secondary, CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(OnPrimary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = OnPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Kayıt Ol",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnPrimary,
                letterSpacing = 2.sp
            )
            Text(
                text = "Yeni maceralar için aramıza katıl",
                fontSize = 14.sp,
                color = OnPrimary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp)),
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = firstName,
                            onValueChange = { firstName = it; if (uiError != null) viewModel.resetState() },
                            placeholder = { Text("Ad", color = if (isFirstNameError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                            isError = isFirstNameError,
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(54.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = lastName,
                            onValueChange = { lastName = it; if (uiError != null) viewModel.resetState() },
                            placeholder = { Text("Soyad", color = if (isLastNameError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                            isError = isLastNameError,
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(54.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = username,
                        onValueChange = { username = it; if (uiError != null) viewModel.resetState() },
                        placeholder = { Text("Kullanıcı adı", color = if (isUsernameError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                        isError = isUsernameError,
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = if (isUsernameError) MaterialTheme.colorScheme.error else Color.White, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                            errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it; if (uiError != null) viewModel.resetState() },
                        placeholder = { Text("E-posta", color = if (isEmailError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                        isError = isEmailError,
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = if (isEmailError) MaterialTheme.colorScheme.error else Color.White, modifier = Modifier.size(20.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                            errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it; if (uiError != null) viewModel.resetState() },
                        placeholder = { Text("Şifre", color = if (isPasswordError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                        isError = isPasswordError,
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = if (isPasswordError) MaterialTheme.colorScheme.error else Color.White, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = if (isPasswordError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                            errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; if (uiError != null) viewModel.resetState() },
                        placeholder = { Text("Şifre Tekrar", color = if (isPasswordError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                        isError = isPasswordError,
                        leadingIcon = { Icon(Icons.Default.LockClock, null, tint = if (isPasswordError) MaterialTheme.colorScheme.error else Color.White, modifier = Modifier.size(20.dp)) },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                            errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                    )

                    if (uiError != null) {
                        Text(
                            text = uiError.message,
                            color = MaterialTheme.colorScheme.errorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.register(firstName, lastName, username, email, password, confirmPassword)
                        },
                        enabled = uiState !is RegisterUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .scale(scale),
                        shape = RoundedCornerShape(16.dp),
                        interactionSource = interactionSource,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Primary)
                    ) {
                        if (uiState is RegisterUiState.Loading) {
                            CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Kayıt Ol", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Zaten hesabın var mı? Giriş yap",
                    color = OnPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
