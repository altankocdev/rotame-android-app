package com.altankoc.rotame.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Map : BottomNavItem(
        route = "map",
        title = "Harita",
        icon = Icons.Default.Map
    )

    data object List : BottomNavItem(
        route = "location_list",
        title = "Liste",
        icon = Icons.Default.List
    )

    data object Profile : BottomNavItem(
        route = "profile",
        title = "Profil",
        icon = Icons.Default.Person
    )
}