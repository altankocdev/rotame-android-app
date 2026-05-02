package com.altankoc.rotame.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryPermissionHandler(
    onGranted: @Composable () -> Unit,
    onDenied: @Composable (requestPermission: () -> Unit) -> Unit,
    onPermanentlyDenied: @Composable () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = PermissionUtil.galleryPermissions.toList()
    )

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    when {
        permissionState.allPermissionsGranted -> onGranted()
        permissionState.shouldShowRationale -> onDenied { permissionState.launchMultiplePermissionRequest() }
        else -> onPermanentlyDenied()
    }
}