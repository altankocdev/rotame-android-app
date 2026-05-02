package com.altankoc.rotame.feature.location.presentation.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.altankoc.rotame.core.ui.theme.*
import com.altankoc.rotame.core.util.CameraPermissionHandler
import com.altankoc.rotame.core.util.GalleryPermissionHandler
import com.altankoc.rotame.feature.location.presentation.state.AddEditLocationUiState
import com.altankoc.rotame.feature.location.presentation.state.LocationDetailUiState
import com.altankoc.rotame.feature.location.presentation.viewmodel.AddEditLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.io.File
import java.util.Locale

@Composable
fun AddEditLocationScreen(
    locationId: Long? = null,
    initialLat: Double? = null,
    initialLng: Double? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddEditLocationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationState by viewModel.locationState.collectAsStateWithLifecycle()
    val lat by viewModel.lat.collectAsStateWithLifecycle()
    val lng by viewModel.lng.collectAsStateWithLifecycle()
    val pendingImages by viewModel.pendingImages.collectAsStateWithLifecycle()
    val existingImages by viewModel.existingImages.collectAsStateWithLifecycle()
    val coverIndex by viewModel.coverIndex.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val isEditMode = locationId != null

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showCameraPermission by remember { mutableStateOf(false) }
    var showGalleryPermission by remember { mutableStateOf(false) }
    var cameraImageFile by remember { mutableStateOf<File?>(null) }

    val totalImages = existingImages.size + pendingImages.size

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val remaining = 4 - totalImages
        if (remaining > 0) {
            uris.take(remaining).forEach { uri ->
                uriToFile(context, uri)?.let { viewModel.addPendingImage(it) }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageFile?.let { viewModel.addPendingImage(it) }
        }
    }

    LaunchedEffect(Unit) {
        if (isEditMode) viewModel.getLocation(locationId!!)
        else viewModel.setInitialCoordinates(initialLat, initialLng)
    }

    LaunchedEffect(locationState) {
        if (locationState is LocationDetailUiState.Success) {
            val location = (locationState as LocationDetailUiState.Success).location
            name = location.name
            description = location.description ?: ""
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AddEditLocationUiState.Success) {
            viewModel.resetState()
            onNavigateBack()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(lat ?: 39.9334, lng ?: 32.8597), if (lat != null) 15f else 6f
        )
    }

    LaunchedEffect(lat, lng) {
        if (lat != null && lng != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lng!!), 15f)
            )
        }
    }

    if (showCameraPermission) {
        CameraPermissionHandler(
            onGranted = {
                showCameraPermission = false
                val file = createTempImageFile(context)
                cameraImageFile = file
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                cameraLauncher.launch(uri)
            },
            onDenied = { it() },
            onPermanentlyDenied = { showCameraPermission = false }
        )
    }

    if (showGalleryPermission) {
        GalleryPermissionHandler(
            onGranted = {
                showGalleryPermission = false
                galleryLauncher.launch("image/*")
            },
            onDenied = { it() },
            onPermanentlyDenied = { showGalleryPermission = false }
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
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Primary.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 64.dp, bottom = 32.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onNavigateBack,
                        shape = CircleShape,
                        color = OnPrimaryContainer.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Geri",
                                tint = OnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isEditMode) "Rotayı Güncelle" else "Yeni Keşif Ekle",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = OnPrimaryContainer,
                        letterSpacing = (-1).sp
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                color = Background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Primary.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = MapProperties(isMyLocationEnabled = true),
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = false,
                                    myLocationButtonEnabled = false
                                ),
                                onMapClick = { latLng ->
                                    viewModel.updateCoordinates(latLng.latitude, latLng.longitude)
                                }
                            ) {
                                if (lat != null && lng != null) {
                                    val markerState = rememberMarkerState(position = LatLng(lat!!, lng!!))
                                    LaunchedEffect(lat, lng) {
                                        markerState.position = LatLng(lat!!, lng!!)
                                    }
                                    Marker(state = markerState)
                                }
                            }

                            if (lat != null && lng != null) {
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.9f)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.4f, %.4f", lat, lng),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ModernInputField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Rota Başlığı",
                        placeholder = "Örn: Gizli Şelale",
                        icon = Icons.Rounded.Title
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ModernInputField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Hikayen",
                        placeholder = "Burayı özel kılan nedir?",
                        icon = Icons.AutoMirrored.Rounded.Notes,
                        singleLine = false,
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Fotoğraflar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Primary)
                        Text("$totalImages/4", fontSize = 12.sp, color = OnSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kapak fotoğrafı seçmek için fotoğrafa dokun",
                        fontSize = 11.sp,
                        color = OnSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(existingImages) { _, image ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (image.cover) 3.dp else 0.dp,
                                        color = if (image.cover) Primary else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(image.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            locationId?.let { viewModel.setExistingCover(it, image.id) }
                                        }
                                )

                                IconButton(
                                    onClick = {
                                        locationId?.let { viewModel.removeExistingImage(it, image.id) }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                ) {
                                    Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }

                                if (image.cover) {
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Primary
                                    ) {
                                        Text(
                                            "Kapak",
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        itemsIndexed(pendingImages) { index, file ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (existingImages.isEmpty() && index == coverIndex) 3.dp else 0.dp,
                                        color = if (existingImages.isEmpty() && index == coverIndex) Primary else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                AsyncImage(
                                    model = file,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.setCoverIndex(index) }
                                )

                                IconButton(
                                    onClick = { viewModel.removePendingImage(index) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                ) {
                                    Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }

                                if (existingImages.isEmpty() && index == coverIndex) {
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Primary
                                    ) {
                                        Text(
                                            "Kapak",
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (totalImages < 4) {
                            item {
                                AddPhotoButton(onClick = { showImageSourceDialog = true })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = {
                            if (isEditMode) viewModel.updateLocation(locationId!!, name, description.ifBlank { null })
                            else viewModel.createLocation(name, description.ifBlank { null })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Primary),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = name.isNotBlank() && uiState !is AddEditLocationUiState.Loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.horizontalGradient(listOf(Primary, Secondary))),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState is AddEditLocationUiState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = if (isEditMode) "DEĞİŞİKLİKLERİ KAYDET" else "ROTAYI OLUŞTUR",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            shape = RoundedCornerShape(28.dp),
            title = { Text("Fotoğraf Ekle", fontWeight = FontWeight.Bold) },
            text = { Text("Fotoğrafı nereden çekmek istersiniz?") },
            confirmButton = {
                Button(
                    onClick = { showCameraPermission = true; showImageSourceDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Kamera")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGalleryPermission = true; showImageSourceDialog = false }) {
                    Text("Galeri", color = Primary)
                }
            }
        )
    }
}

@Composable
private fun ModernInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = OnSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            leadingIcon = { Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Primary
            ),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

@Composable
private fun AddPhotoButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(2.dp, Brush.linearGradient(listOf(Primary.copy(alpha = 0.2f), Color.Transparent)))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Rounded.AddAPhoto, null, tint = Primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Ekle", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Bold)
        }
    }
}

private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("img_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { inputStream.copyTo(it) }
        tempFile
    } catch (e: Exception) { null }
}

private fun createTempImageFile(context: Context): File {
    return File.createTempFile("camera_", ".jpg", context.cacheDir)
}