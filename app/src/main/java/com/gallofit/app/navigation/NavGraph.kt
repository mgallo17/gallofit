package com.gallofit.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gallofit.feature.dashboard.DashboardScreen
import com.gallofit.feature.foodlog.FoodLogScreen
import com.gallofit.feature.settings.SettingsScreen
import com.gallofit.feature.workout.WorkoutScreen

sealed class Screen(val route: String, val label: String) {
    object Dashboard : Screen("dashboard", "Início")
    object FoodLog : Screen("food_log", "Refeições")
    object Workout : Screen("workout", "Treino")
    object Settings : Screen("settings", "Definições")
}

@Composable
fun GalloFitNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        Triple(Screen.Dashboard, Icons.Default.Home, "Início"),
        Triple(Screen.FoodLog, Icons.Default.Restaurant, "Refeições"),
        Triple(Screen.Workout, Icons.Default.FitnessCenter, "Treino"),
        Triple(Screen.Settings, Icons.Default.Settings, "Definições"),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { (screen, icon, label) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(navController) }
            composable(Screen.FoodLog.route) { FoodLogScreen(navController) }
            composable(Screen.Workout.route) { WorkoutScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
