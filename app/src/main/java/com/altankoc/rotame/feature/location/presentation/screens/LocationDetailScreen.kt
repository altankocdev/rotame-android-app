package com.altankoc.rotame.feature.location.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.altankoc.rotame.core.ui.theme.*
import com.altankoc.rotame.core.ui.theme.Error as ThemeError
import com.altankoc.rotame.feature.location.domain.model.Location
import com.altankoc.rotame.feature.location.domain.model.LocationImage
import com.altankoc.rotame.feature.location.presentation.state.LocationDetailUiState
import com.altankoc.rotame.feature.location.presentation.viewmodel.LocationDetailViewModel
import java.util.Locale

@Composable
fun LocationDetailScreen(
    locationId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: LocationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(locationId) {
        viewModel.getLocation(locationId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        when (uiState) {
            is LocationDetailUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary
                )
            }
            is LocationDetailUiState.Success -> {
                val location = (uiState as LocationDetailUiState.Success).location
                DetailContent(
                    location = location,
                    scrollState = scrollState,
                    onNavigateBack = onNavigateBack,
                    onNavigateToEdit = onNavigateToEdit,
                    onToggleFavorite = { viewModel.toggleFavorite(location.id) },
                    onDelete = {
                        viewModel.deleteLocation(location.id)
                        onNavigateBack()
                    }
                )
            }
            is LocationDetailUiState.Error -> {
                ErrorState(
                    message = (uiState as LocationDetailUiState.Error).message,
                    onRetry = { viewModel.getLocation(locationId) },
                    onBack = onNavigateBack
                )
            }
            else -> Unit
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FullScreenImageViewer(
    images: List<LocationImage>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { images.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(images[page].imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Kapat",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    images.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) Color.White
                                    else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp),
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${images.size}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    location: Location,
    scrollState: ScrollState,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showImageViewer by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konumu Sil", fontWeight = FontWeight.Bold) },
            text = {
                Text("\"${location.name}\" konumunu silmek istediğine emin misin? Bu işlem geri alınamaz.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ThemeError)
                ) {
                    Text("Sil", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal", color = Primary)
                }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showImageViewer && location.images.isNotEmpty()) {
        FullScreenImageViewer(
            images = location.images,
            initialIndex = selectedImageIndex,
            onDismiss = { showImageViewer = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            val coverImage = location.coverImage
            if (coverImage != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(coverImage.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = location.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            val index = location.images.indexOf(coverImage)
                            selectedImageIndex = if (index >= 0) index else 0
                            showImageViewer = true
                        }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(Primary, Secondary))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Image, null, tint = Color.White, modifier = Modifier.size(80.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Surface(
                onClick = onNavigateBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(44.dp),
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.3f),
                contentColor = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = location.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.LocationOn, null, tint = Secondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.4f, %.4f", location.latitude, location.longitude),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Background
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionButton(
                        icon = if (location.favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        text = if (location.favorite) "Favori" else "Favori",
                        containerColor = if (location.favorite) ThemeError.copy(alpha = 0.1f) else Surface,
                        contentColor = if (location.favorite) ThemeError else Primary,
                        onClick = onToggleFavorite,
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        icon = Icons.Rounded.Navigation,
                        text = "Yol Tarifi",
                        containerColor = Primary.copy(alpha = 0.1f),
                        contentColor = Primary,
                        onClick = {
                            val uri = Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")
                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                val browserUri = Uri.parse(
                                    "https://www.google.com/maps/dir/?api=1&destination=${location.latitude},${location.longitude}"
                                )
                                context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        icon = Icons.Rounded.Edit,
                        text = "",
                        containerColor = Primary.copy(alpha = 0.05f),
                        contentColor = Primary,
                        onClick = { onNavigateToEdit(location.id) },
                        modifier = Modifier.width(56.dp)
                    )
                    ActionButton(
                        icon = Icons.Rounded.DeleteOutline,
                        text = "",
                        containerColor = ThemeError.copy(alpha = 0.05f),
                        contentColor = ThemeError,
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.width(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Açıklama", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = location.description ?: "Henüz bir açıklama eklenmemiş.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = OnSurfaceVariant
                )

                if (location.images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "Fotoğraflar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 24.dp)
                    ) {
                        items(location.images.withIndex().toList()) { (index, image) ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(image.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(120.dp, 160.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Surface)
                                    .clickable {
                                        selectedImageIndex = index
                                        showImageViewer = true
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(20.dp))
            if (text.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.ErrorOutline, null, tint = ThemeError, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = ThemeError, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
            Text("Tekrar Dene")
        }
        TextButton(onClick = onBack) {
            Text("Geri Dön", color = Primary)
        }
    }
}