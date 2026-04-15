package com.gallofit.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gallofit.core.data.FoodViewModel
import com.gallofit.feature.addfood.AddFoodScreen
import com.gallofit.feature.dashboard.DashboardScreen
import com.gallofit.feature.foodlog.FoodLogScreen
import com.gallofit.feature.gym.GymScreen
import com.gallofit.feature.healthconnect.HealthConnectScreen
import com.gallofit.feature.profile.ProfileScreen
import com.gallofit.feature.settings.SettingsScreen
import com.gallofit.feature.workout.WorkoutScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object FoodLog : Screen("food_log")
    object Workout : Screen("workout")
    object Settings : Screen("settings")
    object AddFood : Screen("add_food/{slot}")
    object Gym : Screen("gym")
    object Profile : Screen("profile")
    object HealthConnect : Screen("health_connect")
}

private val bottomNavScreens = listOf(
    Triple(Screen.Dashboard, Icons.Default.Home, "Início"),
    Triple(Screen.FoodLog, Icons.Default.Restaurant, "Refeições"),
    Triple(Screen.Workout, Icons.Default.FitnessCenter, "Treino"),
    Triple(Screen.Settings, Icons.Default.Settings, "Definições"),
)

private val fullscreenRoutes = setOf("add_food", "gym", "profile", "health_connect")

@Composable
fun GalloFitNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val foodViewModel: FoodViewModel = viewModel()

    val showBottomBar = fullscreenRoutes.none { currentRoute.startsWith(it) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    bottomNavScreens.forEach { (screen, icon, label) ->
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(navController, foodViewModel) }
            composable(Screen.FoodLog.route) { FoodLogScreen(navController, foodViewModel) }
            composable(Screen.Workout.route) { WorkoutScreen(navController, foodViewModel) }
            composable(Screen.Settings.route) { SettingsScreen(navController, foodViewModel) }
            composable(Screen.Gym.route) { GymScreen(navController, foodViewModel) }
            composable(Screen.Profile.route) { ProfileScreen(foodViewModel) }
            composable(Screen.HealthConnect.route) { HealthConnectScreen(navController, foodViewModel) }
            composable(
                route = "add_food/{slot}",
                arguments = listOf(navArgument("slot") { type = NavType.StringType })
            ) { backStackEntry ->
                val slot = backStackEntry.arguments?.getString("slot") ?: "LUNCH"
                AddFoodScreen(navController = navController, initialSlot = slot, foodViewModel = foodViewModel)
            }
        }
    }
}
