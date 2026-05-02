package com.altankoc.rotame.feature.auth.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.rotame.core.ui.theme.OnPrimary
import com.altankoc.rotame.core.ui.theme.Primary
import com.altankoc.rotame.core.ui.theme.PrimaryContainer
import com.altankoc.rotame.core.ui.theme.Secondary
import com.altankoc.rotame.feature.auth.presentation.state.LoginUiState
import com.altankoc.rotame.feature.auth.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var identifier by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "buttonScale")

    val uiError = uiState as? LoginUiState.Error
    val isIdentifierError = uiError?.message?.contains("kullanıcı adı") == true
    val isPasswordError = uiError?.message?.contains("Şifre") == true

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            viewModel.resetState()
            onLoginSuccess()
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
                    .offset(x = (-50).dp, y = (-50).dp)
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(OnPrimary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = OnPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RotaMe",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnPrimary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Maceralarına kaldığın yerden devam et",
                    fontSize = 14.sp,
                    color = OnPrimary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

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
                    TextField(
                        value = identifier,
                        onValueChange = { 
                            identifier = it 
                            if (uiState is LoginUiState.Error) viewModel.resetState()
                        },
                        isError = isIdentifierError,
                        placeholder = { 
                            Text(
                                "E-posta veya kullanıcı adı", 
                                color = if (isIdentifierError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp 
                            ) 
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = if (isIdentifierError) MaterialTheme.colorScheme.error else Color.White, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (identifier.isNotEmpty()) {
                                IconButton(onClick = { identifier = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Temizle", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
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
                        onValueChange = { 
                            password = it 
                            if (uiState is LoginUiState.Error) viewModel.resetState()
                        },
                        isError = isPasswordError,
                        placeholder = { 
                            Text(
                                "Şifre", 
                                color = if (isPasswordError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp 
                            ) 
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = if (isPasswordError) MaterialTheme.colorScheme.error else Color.White, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Şifreyi gizle" else "Şifreyi göster",
                                    tint = if (isPasswordError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
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

                    if (uiState is LoginUiState.Error) {
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            color = MaterialTheme.colorScheme.errorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.login(identifier, password) },
                        enabled = uiState !is LoginUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .scale(scale),
                        shape = RoundedCornerShape(16.dp),
                        interactionSource = interactionSource,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Primary
                        )
                    ) {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "Giriş Yap",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Henüz hesabın yok mu? Kayıt Ol",
                    color = OnPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
