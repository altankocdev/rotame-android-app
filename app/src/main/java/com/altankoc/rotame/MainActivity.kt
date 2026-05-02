package com.altankoc.rotame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.altankoc.rotame.core.network.AuthEvent
import com.altankoc.rotame.core.network.AuthEventBus
import com.altankoc.rotame.core.ui.theme.RotaMeTheme
import com.altankoc.rotame.navigation.NavGraph
import com.altankoc.rotame.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authEventBus: AuthEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            RotaMeTheme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    authEventBus.events.collect { event ->
                        when (event) {
                            is AuthEvent.Unauthorized -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                NavGraph(navController = navController)
            }
        }
    }
}