package com.altankoc.rotame.core.util

import android.os.Build

object PermissionUtil {

    val locationPermissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val cameraPermissions = arrayOf(
        android.Manifest.permission.CAMERA
    )

    val galleryPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}