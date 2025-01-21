package com.samyak2403.emf_magneticfielddetector.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.samyak2403.emf_magneticfielddetector.screens.AboutScreen
import com.samyak2403.emf_magneticfielddetector.screens.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.About.route) {
            AboutScreen()
        }
    }
}
