package com.onlyreminder.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.OnlyReminderNavGraph
import com.onlyreminder.app.core.navigation.Route

sealed class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    val labelId: Int
) {
    data object Home : TopLevelDestination(Route.Home, Icons.Default.Home, R.string.home_nav)
    data object Notifications : TopLevelDestination(Route.BirthdayReview, Icons.Default.Notifications, R.string.notifications_nav)
    data object Settings : TopLevelDestination(Route.Settings, Icons.Default.Settings, R.string.settings_nav)
}

@Composable
fun MainScreen(
    startDestination: Route = Route.Home
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelDestinations = listOf(
        TopLevelDestination.Home,
        TopLevelDestination.Notifications,
        TopLevelDestination.Settings
    )

    val isTopLevelDestination = topLevelDestinations.any { destination ->
        currentDestination?.hasRoute(destination.route::class) == true
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isTopLevelDestination,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(destination.route::class)
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = stringResource(destination.labelId)
                                )
                            },
                            label = { Text(stringResource(destination.labelId)) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        OnlyReminderNavGraph(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
