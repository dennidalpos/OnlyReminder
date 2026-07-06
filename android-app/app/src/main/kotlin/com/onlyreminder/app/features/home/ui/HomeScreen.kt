package com.onlyreminder.app.features.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.onlyreminder.app.core.navigation.Screen
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@Composable
fun HomeScreen(navController: NavController) {
    val screens = listOf(
        Screen.Contacts,
        Screen.Tasks,
        Screen.BirthdayReview,
        Screen.Templates,
        Screen.WhatsApp,
        Screen.Groups,
        Screen.Import,
        Screen.Backup,
        Screen.Settings,
        Screen.Security,
        Screen.Onboarding
    )

    Scaffold(
        topBar = { OnlyReminderTopBar(title = Screen.Home.title) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "OnlyReminder Home - Placeholder Navigation")

            screens.forEach { screen ->
                Button(
                    onClick = { navController.navigate(screen.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = screen.title)
                }
            }
        }
    }
}
