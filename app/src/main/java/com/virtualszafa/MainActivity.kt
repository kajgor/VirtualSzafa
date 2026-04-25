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
        composable("my_wardrobe") { MyWardrobeScreen(navController) }
        composable("settings") { SettingsScreen(navController) }

        composable(
            route = "add_item?name={name}&brand={brand}&size={size}&color={color}&barcode={barcode}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; nullable = true },
                navArgument("brand") { type = NavType.StringType; nullable = true },
                navArgument("size") { type = NavType.StringType; nullable = true },
                navArgument("color") { type = NavType.StringType; nullable = true },
                navArgument("barcode") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val brand = backStackEntry.arguments?.getString("brand")
            val size = backStackEntry.arguments?.getString("size")
            val color = backStackEntry.arguments?.getString("color")
            val barcode = backStackEntry.arguments?.getString("barcode")

            AddItemScreen(
                navController = navController,
                prefilledName = name,
                prefilledBrand = brand,
                prefilledSize = size,
                prefilledColor = color,
                barcode = barcode
            )
        }
    }
}