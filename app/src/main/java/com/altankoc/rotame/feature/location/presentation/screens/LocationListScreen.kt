package com.altankoc.rotame.feature.location.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.altankoc.rotame.core.ui.theme.*
import com.altankoc.rotame.core.ui.theme.Error as ThemeError
import com.altankoc.rotame.feature.location.domain.model.Location
import com.altankoc.rotame.feature.location.presentation.state.LocationListUiState
import com.altankoc.rotame.feature.location.presentation.viewmodel.LocationListViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddLocation: () -> Unit,
    initialOnlyFavorites: Boolean = false,
    viewModel: LocationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var onlyFavorites by remember { mutableStateOf(initialOnlyFavorites) }
    var locationToDelete by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getLocations(onlyFavorites = onlyFavorites)
    }

    if (locationToDelete != null) {
        AlertDialog(
            onDismissRequest = { locationToDelete = null },
            title = { Text("Konumu Sil", fontWeight = FontWeight.Bold) },
            text = { Text("\"${locationToDelete!!.name}\" konumunu silmek istediğine emin misin? Bu işlem geri alınamaz.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLocation(locationToDelete!!.id)
                        locationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ThemeError)
                ) {
                    Text("Sil", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { locationToDelete = null }) {
                    Text("İptal", color = Primary)
                }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Primary.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(
                title = "Keşiflerin",
                subtitle = "Kaydettiğin tüm özel rotalar burada",
                onlyFavorites = onlyFavorites,
                onToggleFavorites = {
                    onlyFavorites = !onlyFavorites
                    viewModel.getLocations(onlyFavorites = onlyFavorites)
                }
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                color = Background
            ) {
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                    },
                    label = "ListStateTransition"
                ) { state ->
                    when (state) {
                        is LocationListUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Primary)
                            }
                        }
                        is LocationListUiState.Success -> {
                            val locations = state.locations
                            if (locations.isEmpty()) {
                                EmptyState(onlyFavorites = onlyFavorites)
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(22.dp)
                                ) {
                                    items(
                                        items = locations,
                                        key = { it.id }
                                    ) { location ->
                                        SwipeToDismissBox(
                                            state = rememberSwipeToDismissBoxState(
                                                confirmValueChange = { value ->
                                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                                        locationToDelete = location
                                                    }
                                                    false
                                                }
                                            ),
                                            modifier = Modifier.animateItem(),
                                            enableDismissFromStartToEnd = false,
                                            backgroundContent = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(28.dp))
                                                        .background(
                                                            brush = Brush.horizontalGradient(
                                                                colors = listOf(
                                                                    Primary.copy(alpha = 0.15f),
                                                                    Secondary.copy(alpha = 0.45f),
                                                                    Primary.copy(alpha = 0.75f)
                                                                )
                                                            )
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            brush = Brush.linearGradient(
                                                                colors = listOf(
                                                                    Color.White.copy(alpha = 0.5f),
                                                                    OutlineVariant.copy(alpha = 0.3f)
                                                                )
                                                            ),
                                                            shape = RoundedCornerShape(28.dp)
                                                        ),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(end = 22.dp)
                                                            .shadow(8.dp, RoundedCornerShape(20.dp))
                                                            .clip(RoundedCornerShape(20.dp))
                                                            .background(
                                                                brush = Brush.linearGradient(
                                                                    colors = listOf(
                                                                        Color.White.copy(alpha = 0.35f),
                                                                        Color.White.copy(alpha = 0.15f)
                                                                    )
                                                                )
                                                            )
                                                            .border(
                                                                width = 1.dp,
                                                                brush = Brush.linearGradient(
                                                                    colors = listOf(
                                                                        Color.White.copy(alpha = 0.7f),
                                                                        Color.White.copy(alpha = 0.2f)
                                                                    )
                                                                ),
                                                                shape = RoundedCornerShape(20.dp)
                                                            )
                                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                                    ) {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            Icon(
                                                                Icons.Rounded.Delete,
                                                                contentDescription = "Sil",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(26.dp)
                                                            )
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                            Text(
                                                                text = "Sil",
                                                                color = Color.White,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                letterSpacing = 0.5.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        ) {
                                            ModernLocationCard(
                                                location = location,
                                                onNavigateToDetail = onNavigateToDetail,
                                                onToggleFavorite = { viewModel.toggleFavorite(location.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        is LocationListUiState.Error -> {
                            ErrorState(message = state.message, onRetry = { viewModel.getLocations() })
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    title: String,
    subtitle: String,
    onlyFavorites: Boolean,
    onToggleFavorites: () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnPrimaryContainer,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Primary.copy(alpha = 0.8f)
                )
            }

            ModernAnimatedToggle(
                selected = onlyFavorites,
                onSelectedChange = onToggleFavorites
            )
        }
    }
}

@Composable
private fun ModernAnimatedToggle(
    selected: Boolean,
    onSelectedChange: () -> Unit
) {
    val transition = updateTransition(selected, label = "Toggle")

    val thumbOffset by transition.animateDp(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "Offset"
    ) { if (it) 36.dp else 4.dp }

    val containerColor by transition.animateColor(label = "Color") {
        if (it) ThemeError.copy(alpha = 0.12f) else Primary.copy(alpha = 0.08f)
    }

    Box(
        modifier = Modifier
            .width(76.dp)
            .height(40.dp)
            .clip(CircleShape)
            .background(containerColor)
            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            .clickable(onClick = onSelectedChange)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Explore, null, tint = Primary.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
            Icon(Icons.Rounded.Favorite, null, tint = ThemeError.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.toPx().roundToInt(), 0) }
                .size(32.dp)
                .shadow(6.dp, CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                },
                label = "Icon"
            ) { isFav ->
                Icon(
                    imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.Explore,
                    contentDescription = null,
                    tint = if (isFav) ThemeError else Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ModernLocationCard(
    modifier: Modifier = Modifier,
    location: Location,
    onNavigateToDetail: (Long) -> Unit,
    onToggleFavorite: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(location.favorite) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(location.favorite) {
        isFavorite = location.favorite
    }

    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "PopEffect"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Primary.copy(alpha = 0.25f),
                spotColor = Primary.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Surface,
                        SurfaceVariant.copy(alpha = 0.6f),
                        Surface.copy(alpha = 0.9f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        OutlineVariant.copy(alpha = 0.4f),
                        Primary.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onNavigateToDetail(location.id) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PrimaryContainer.copy(alpha = 0.4f),
                                Primary.copy(alpha = 0.1f)
                            )
                        )
                    )
            ) {
                val coverImage = location.coverImage
                if (coverImage != null) {
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(context)
                            .data(coverImage.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryContainer.copy(alpha = 0.6f),
                                        Primary.copy(alpha = 0.15f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Landscape,
                            null,
                            tint = Primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Place,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rota",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f))
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.7f),
                                    Color.White.copy(alpha = 0.2f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            isFavorite = !isFavorite
                            onToggleFavorite()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) ThemeError else Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .scale(favoriteScale)
                    )
                }

                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.6).sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 14.dp, top = 4.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (!location.description.isNullOrBlank()) {
                        Text(
                            text = location.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = OnSurfaceVariant.copy(alpha = 0.85f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = 0.18f),
                                        Secondary.copy(alpha = 0.12f)
                                    )
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.Explore,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Hemen Keşfet",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Primary,
                                    Primary.copy(alpha = 0.75f)
                                )
                            )
                        )
                        .clickable { onNavigateToDetail(location.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(onlyFavorites: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val transition = rememberInfiniteTransition(label = "Pulse")
        val scale by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
            label = "Scale"
        )

        Surface(
            modifier = Modifier
                .size(160.dp)
                .scale(scale),
            shape = CircleShape,
            color = SurfaceVariant.copy(alpha = 0.4f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (onlyFavorites) Icons.Rounded.FavoriteBorder else Icons.Rounded.Explore,
                    contentDescription = null,
                    tint = Primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = if (onlyFavorites) "Henüz favori yok" else "Keşif zamanı!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (onlyFavorites) "Kalp butonuna basarak ilk rotanı buraya ekleyebilirsin." else "Haritayı aç ve dünyayı keşfetmeye başla.",
            fontSize = 16.sp,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.NearbyError, null, tint = ThemeError, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = ThemeError, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("TEKRAR DENE", fontWeight = FontWeight.Black)
        }
    }
}