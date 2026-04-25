package com.virtualszafa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.virtualszafa.presentation.additem.AddItemScreen
import com.virtualszafa.presentation.home.HomeScreen
import com.virtualszafa.presentation.settings.SettingsScreen
import com.virtualszafa.presentation.wardrobe.MyWardrobeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                VirtualSzafaApp()
            }
        }
    }
}

@Composable
fun VirtualSzafaApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }

        // Poprawione – przekazujemy navController do MyWardrobeScreen
        composable("my_wardrobe") { MyWardrobeScreen(navController) }

        composable("settings") { SettingsScreen(navController) }

        // Pełna integracja AddItemScreen z parametrami
        composable(
            route = "add_item?barcode={barcode}&aiId={aiId}",
            arguments = listOf(
                navArgument("barcode") { type = NavType.StringType; nullable = true },
                navArgument("aiId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode")
            val aiId = backStackEntry.arguments?.getString("aiId")
            AddItemScreen(
                navController = navController,
                barcode = barcode,
                aiId = aiId
            )
        }
    }
}