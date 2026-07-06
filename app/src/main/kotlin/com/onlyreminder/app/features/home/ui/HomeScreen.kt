package com.onlyreminder.app.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@Composable
fun HomeScreen(navController: NavController) {
    val items = listOf(
        "Contacts" to Route.Contacts,
        "Tasks" to Route.Tasks,
        "Birthday Review" to Route.BirthdayReview,
        "Templates" to Route.Templates,
        "Groups" to Route.Groups,
        "Import" to Route.Import,
        "Backup" to Route.Backup,
        "Settings" to Route.Settings,
        "Security" to Route.Security,
    )

    Scaffold(
        topBar = { OnlyReminderTopBar(title = "OnlyReminder") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { (title, route) ->
                Button(
                    onClick = { navController.navigate(route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = title)
                }
            }
        }
    }
}
