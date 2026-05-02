package com.altankoc.rotame.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.altankoc.rotame.core.ui.components.RotaMeBottomBar
import com.altankoc.rotame.core.ui.screens.SplashScreen
import com.altankoc.rotame.feature.auth.presentation.screens.LoginScreen
import com.altankoc.rotame.feature.auth.presentation.screens.RegisterScreen
import com.altankoc.rotame.feature.location.presentation.screens.AddEditLocationScreen
import com.altankoc.rotame.feature.location.presentation.screens.LocationDetailScreen
import com.altankoc.rotame.feature.location.presentation.screens.LocationListScreen
import com.altankoc.rotame.feature.map.presentation.screens.MapScreen
import com.altankoc.rotame.feature.profile.presentation.screens.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    val bottomNavItems = listOf(
        BottomNavItem.Map,
        BottomNavItem.List,
        BottomNavItem.Profile
    )

    val bottomNavRoutes = listOf(
        Screen.Map.route,
        Screen.LocationList.route,
        Screen.Profile.route
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = bottomNavRoutes.any { route ->
        currentDestination?.hierarchy?.any { it.route == route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                RotaMeBottomBar(
                    items = bottomNavItems,
                    currentDestination = currentDestination,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Map.route) {
                MapScreen(
                    onNavigateToDetail = { locationId ->
                        navController.navigate(Screen.LocationDetail.createRoute(locationId))
                    },
                    onNavigateToAddLocation = { lat, lng ->
                        navController.navigate(Screen.AddEditLocation.createRoute(lat = lat, lng = lng))
                    }
                )
            }

            composable(Screen.LocationList.route) {
                LocationListScreen(
                    onNavigateToDetail = { locationId ->
                        navController.navigate(Screen.LocationDetail.createRoute(locationId))
                    },
                    onNavigateToAddLocation = {
                        navController.navigate(Screen.AddEditLocation.createRoute())
                    }
                )
            }

            composable(
                route = Screen.LocationDetail.route,
                arguments = listOf(navArgument("locationId") { type = NavType.LongType })
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getLong("locationId") ?: return@composable
                LocationDetailScreen(
                    locationId = locationId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.AddEditLocation.createRoute(locationId = id))
                    }
                )
            }

            composable(
                route = Screen.AddEditLocation.route,
                arguments = listOf(
                    navArgument("locationId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("lat") { type = NavType.FloatType; defaultValue = 0f },
                    navArgument("lng") { type = NavType.FloatType; defaultValue = 0f }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getLong("locationId")?.takeIf { it != -1L }
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble()?.takeIf { it != 0.0 }
                val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble()?.takeIf { it != 0.0 }
                AddEditLocationScreen(
                    locationId = locationId,
                    initialLat = lat,
                    initialLng = lng,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToLocationList = { navController.navigate(Screen.LocationList.route) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Favorites.route) {
                LocationListScreen(
                    onNavigateToDetail = { locationId ->
                        navController.navigate(Screen.LocationDetail.createRoute(locationId))
                    },
                    onNavigateToAddLocation = {
                        navController.navigate(Screen.AddEditLocation.createRoute())
                    },
                    initialOnlyFavorites = true
                )
            }
        }
    }
}