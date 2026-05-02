package com.altankoc.rotame.feature.map.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.rotame.core.ui.theme.*
import com.altankoc.rotame.core.util.LocationPermissionHandler
import com.altankoc.rotame.feature.map.presentation.state.MapUiState
import com.altankoc.rotame.feature.map.presentation.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddLocation: (Double?, Double?) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocationPermissionHandler(
        onGranted = {
            MapContent(
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToAddLocation = onNavigateToAddLocation
            )
        },
        onDenied = { requestPermission ->
            LaunchedEffect(Unit) { requestPermission() }
            MapContent(
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToAddLocation = onNavigateToAddLocation
            )
        },
        onPermanentlyDenied = {
            MapContent(
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToAddLocation = onNavigateToAddLocation
            )
        }
    )
}

@Composable
private fun MapContent(
    uiState: MapUiState,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddLocation: (Double?, Double?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(39.9334, 32.8597), 12f)
    }

    var showConfirmDialog by remember { mutableStateOf<LatLng?>(null) }
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLatLng = LatLng(it.latitude, it.longitude)
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 14f)
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapStyleOptions = null
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false
            ),
            onMapLongClick = { latLng ->
                showConfirmDialog = latLng
            }
        ) {
            if (uiState is MapUiState.Success) {
                uiState.locations.forEach { location ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(location.latitude, location.longitude)
                        ),
                        title = location.name,
                        onClick = {
                            onNavigateToDetail(location.id)
                            true
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = {
                    currentLatLng?.let {
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                        }
                    }
                },
                containerColor = Color.White,
                contentColor = Primary,
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MyLocation,
                    contentDescription = "Konumum",
                    modifier = Modifier.size(22.dp)
                )
            }

            FloatingActionButton(
                onClick = { showConfirmDialog = cameraPositionState.position.target },
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Konum Ekle",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (showConfirmDialog != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = null },
                shape = RoundedCornerShape(32.dp),
                containerColor = Background,
                title = {
                    Text("Yeni Rota Oluştur", fontWeight = FontWeight.ExtraBold, color = OnSurface)
                },
                text = {
                    Text("Seçili konumu rotalarına eklemek istiyor musun?", color = OnSurfaceVariant)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val latLng = showConfirmDialog!!
                            showConfirmDialog = null
                            onNavigateToAddLocation(latLng.latitude, latLng.longitude)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Evet, Ekle", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = null }) {
                        Text("Vazgeç", color = OnSurfaceVariant)
                    }
                }
            )
        }
    }
}
