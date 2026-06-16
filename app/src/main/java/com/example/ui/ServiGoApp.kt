package com.example.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.BookingsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ServiceSelectionScreen
import com.example.ui.screens.BookingProcessScreen

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Bookings : Screen("bookings", "Bookings", Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Outlined.List)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun ServiGoApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.Home, Screen.Bookings, Screen.Profile)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar(
                    windowInsets = WindowInsets.navigationBars
                ) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == screen.route) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
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
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "landing",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("landing") {
                com.example.ui.screens.LandingScreen(
                    onNavigateToLogin = { navController.navigate("login") },
                    onNavigateToSignUp = { navController.navigate("signup") }
                )
            }
            composable("login") {
                com.example.ui.screens.AuthInputScreen(
                    isSignUp = false,
                    onBack = { navController.popBackStack() },
                    onSendOtp = { contact, isProvider -> navController.navigate("otp/$contact/$isProvider") }
                )
            }
            composable("signup") {
                com.example.ui.screens.AuthInputScreen(
                    isSignUp = true,
                    onBack = { navController.popBackStack() },
                    onSendOtp = { contact, isProvider -> navController.navigate("otp/$contact/$isProvider") }
                )
            }
            composable("otp/{contact}/{isProvider}") { backStackEntry ->
                val contact = backStackEntry.arguments?.getString("contact") ?: ""
                val isProvider = backStackEntry.arguments?.getString("isProvider")?.toBoolean() ?: false
                com.example.ui.screens.OtpVerificationScreen(
                    contact = contact,
                    onBack = { navController.popBackStack() },
                    onVerify = { navController.navigate("welcome/$isProvider") }
                )
            }
            composable("welcome/{isProvider}") { backStackEntry ->
                val isProvider = backStackEntry.arguments?.getString("isProvider")?.toBoolean() ?: false
                com.example.ui.screens.WelcomeScreen(
                    isProvider = isProvider,
                    onGetStarted = { 
                        val route = if (isProvider) "provider_dashboard" else Screen.Home.route
                        navController.navigate(route) {
                            popUpTo("landing") { inclusive = true }
                        }
                    }
                )
            }
            composable("provider_dashboard") {
                com.example.ui.screens.ProviderDashboardScreen()
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCategory = { category ->
                        navController.navigate("services/$category")
                    }
                )
            }
            composable("services/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "All"
                ServiceSelectionScreen(
                    category = category,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onServiceSelected = { serviceId ->
                        navController.navigate("book/$serviceId")
                    }
                )
            }
            composable("book/{serviceId}") { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                BookingProcessScreen(
                    serviceId = serviceId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onBookingConfirm = {
                        navController.navigate(Screen.Bookings.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
            composable(Screen.Bookings.route) {
                BookingsScreen(viewModel = viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
