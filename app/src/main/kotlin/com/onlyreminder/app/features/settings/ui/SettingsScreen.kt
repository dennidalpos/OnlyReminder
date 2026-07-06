package com.onlyreminder.app.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val language by viewModel.language.collectAsState()
    val sendMode by viewModel.sendMode.collectAsState()
    val countryCode by viewModel.defaultCountryCode.collectAsState()
    val notificationTime by viewModel.birthdayNotificationTime.collectAsState()
    val backupUri by viewModel.backupFolderUri.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("General", style = MaterialTheme.typography.titleMedium)

            ListItem(
                headlineContent = { Text("Language") },
                supportingContent = { Text(if (language == "en") "English" else "Italiano") },
                trailingContent = {
                    TextButton(onClick = { viewModel.setLanguage(if (language == "en") "it" else "en") }) {
                        Text("Change")
                    }
                }
            )

            ListItem(
                headlineContent = { Text("Default Country Code") },
                supportingContent = { Text(countryCode) },
                trailingContent = {
                    // Simplified: toggle between +39 and +1
                    TextButton(onClick = { viewModel.setDefaultCountryCode(if (countryCode == "+39") "+1" else "+39") }) {
                        Text("Toggle")
                    }
                }
            )

            Divider()
            Text("Sending Mode", style = MaterialTheme.typography.titleMedium)

            Column {
                listOf("REMINDER_ONLY", "MANUAL_WA", "WA_API").forEach { mode ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        RadioButton(
                            selected = sendMode == mode,
                            onClick = { viewModel.setSendMode(mode) })
                        Text(mode, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Divider()
            Text("Birthdays", style = MaterialTheme.typography.titleMedium)

            ListItem(
                headlineContent = { Text("Notification Time") },
                supportingContent = { Text(notificationTime) }
            )

            Divider()
            Text("Backup", style = MaterialTheme.typography.titleMedium)

            ListItem(
                headlineContent = { Text("Backup Location") },
                supportingContent = { Text(backupUri ?: "Not set") }
            )
        }
    }
}
