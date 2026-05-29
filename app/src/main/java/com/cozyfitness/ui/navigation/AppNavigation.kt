package com.cozyfitness.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cozyfitness.ui.home.HomeScreen
import com.cozyfitness.ui.stats.StatsScreen
import com.cozyfitness.ui.workout.WorkoutScreen
import com.cozyfitness.ui.tracking.TrackingScreen
import com.cozyfitness.ui.profile.ProfileScreen
import com.cozyfitness.ui.theme.SageGreen
import com.cozyfitness.ui.theme.Silver
import com.cozyfitness.ui.theme.MintWhisper

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem("home", "首页", Icons.Filled.Home, Icons.Outlined.Home)
    data object Workout : BottomNavItem("workout", "训练", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter)
    data object Track : BottomNavItem("track", "追踪", Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle)
    data object Stats : BottomNavItem("stats", "统计", Icons.Filled.BarChart, Icons.Outlined.BarChart)
    data object Profile : BottomNavItem("profile", "我的", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun CozyFitnessNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workout,
        BottomNavItem.Track,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SageGreen,
                            selectedTextColor = SageGreen,
                            unselectedIconColor = Silver,
                            unselectedTextColor = Silver,
                            indicatorColor = MintWhisper
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Workout.route) { WorkoutScreen() }
            composable(BottomNavItem.Track.route) { TrackingScreen() }
            composable(BottomNavItem.Stats.route) { StatsScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}