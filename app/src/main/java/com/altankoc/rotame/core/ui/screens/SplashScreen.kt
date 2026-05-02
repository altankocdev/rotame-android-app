package com.altankoc.rotame.core.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.rotame.core.ui.theme.OnPrimary
import com.altankoc.rotame.core.ui.theme.Primary
import com.altankoc.rotame.core.ui.theme.PrimaryContainer
import com.altankoc.rotame.core.ui.theme.Secondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val iconScale = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(20f) }

    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )

    LaunchedEffect(Unit) {
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = LinearOutSlowInEasing)
        )
        iconAlpha.animateTo(1f, tween(600))

        delay(200)
        textAlpha.animateTo(1f, tween(800))
        textOffset.animateTo(0f, tween(800, easing = LinearOutSlowInEasing))
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashUiState.NavigateToLogin -> onNavigateToLogin()
            is SplashUiState.NavigateToMap -> onNavigateToMap()
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Primary,
                        Secondary,
                        Primary.copy(alpha = 0.9f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = (-100).dp, y = (-50).dp)
                    .background(PrimaryContainer, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 80.dp, y = 80.dp)
                    .background(Secondary, CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(iconScale.value)
                    .alpha(iconAlpha.value)
                    .offset(y = floatingOffset.dp)
                    .background(
                        color = OnPrimary.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = OnPrimary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = OnPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .offset(y = textOffset.value.dp)
            ) {
                Text(
                    text = "RotaMe",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnPrimary,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Konumlarını kaydet, anılarını sakla",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnPrimary.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}