package com.altankoc.rotame.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Map : Screen("map")
    data object LocationList : Screen("location_list")
    data object LocationDetail : Screen("location_detail/{locationId}") {
        fun createRoute(locationId: Long) = "location_detail/$locationId"
    }
    data object AddEditLocation : Screen("add_edit_location?locationId={locationId}&lat={lat}&lng={lng}") {
        fun createRoute(locationId: Long? = null, lat: Double? = null, lng: Double? = null) =
            "add_edit_location?locationId=${locationId ?: -1L}&lat=${lat ?: 0.0}&lng=${lng ?: 0.0}"
    }
    data object Profile : Screen("profile")
    data object MyLocations : Screen("my_locations")
    data object Favorites : Screen("favorites")
}