package com.example.pdfgen.navigation
// In a new file, e.g., com/yourcompany/yourproject/navigation/AppNavigation.kt


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pdfgen.ui.screens.HomeScreen
import com.example.pdfgen.ui.screens.PdfGenScreen
import com.example.pdfgen.ui.screens.ProfileScreen


@Composable
fun AppNavigation() {
    // Create a NavController to handle navigation.
    val navController = rememberNavController()
    // List of bottom navigation items.
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.PdfGen,
        NavigationItem.Profile,
    )

    Scaffold(
        bottomBar = {
            // Composable for the bottom navigation bar.
            NavigationBar {
                // Get the current back stack entry to determine the current route.
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    // Composable for each item in the bottom navigation bar.
                    NavigationBarItem(
                        // The icon for the navigation item.
                        icon = { Icon(screen.icon, contentDescription = null) },
                        // The label for the navigation item.
                        label = { Text(screen.title) },
                        // Whether the item is currently selected.
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        // The action to perform when the item is clicked.
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item.
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item.
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // The NavHost that will contain the different screens.
        NavHost(
            navController,
            startDestination = NavigationItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            // Define the composable for the Home screen.
            composable(NavigationItem.Home.route) { HomeScreen() }
            // Define the composable for the PDF Gen screen.
            composable(NavigationItem.PdfGen.route) { PdfGenScreen() }
            // Define the composable for the Profile screen.
            composable(NavigationItem.Profile.route) { ProfileScreen() }
        }
    }
}