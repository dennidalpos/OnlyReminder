package com.onlyreminder.app.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val language by viewModel.language.collectAsState()
    val sendMode by viewModel.sendMode.collectAsState()
    val countryCode by viewModel.defaultCountryCode.collectAsState()
    val notificationTime by viewModel.birthdayNotificationTime.collectAsState()
    val backupUri by viewModel.backupFolderUri.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.settings_title),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
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
            Text(
                stringResource(id = R.string.general_header),
                style = MaterialTheme.typography.titleMedium
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.language_label_settings)) },
                supportingContent = {
                    Text(
                        if (language == "en") stringResource(id = R.string.english) else stringResource(
                            id = R.string.italian
                        )
                    )
                },
                trailingContent = {
                    TextButton(onClick = { viewModel.setLanguage(if (language == "en") "it" else "en") }) {
                        Text(stringResource(id = R.string.change))
                    }
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.default_country_code)) },
                supportingContent = { Text(countryCode) },
                trailingContent = {
                    // Simplified: toggle between +39 and +1
                    TextButton(onClick = { viewModel.setDefaultCountryCode(if (countryCode == "+39") "+1" else "+39") }) {
                        Text(stringResource(id = R.string.toggle))
                    }
                }
            )

            HorizontalDivider()
            Text(
                stringResource(id = R.string.sending_mode_header),
                style = MaterialTheme.typography.titleMedium
            )

            Column {
                val modes = listOf(
                    "REMINDER_ONLY" to stringResource(R.string.send_mode_reminder_only),
                    "MANUAL_WA" to stringResource(R.string.send_mode_manual_wa),
                    "WA_API" to stringResource(R.string.send_mode_wa_api)
                )
                modes.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sendMode == mode,
                            onClick = { viewModel.setSendMode(mode) })
                        Text(label, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            if (sendMode == "WA_API") {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.config_wa_api)) },
                    supportingContent = { Text(stringResource(R.string.config_wa_api_desc)) },
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    },
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable { navController.navigate(Route.WhatsAppApi) }
                )
            }

            HorizontalDivider()
            Text(
                stringResource(id = R.string.birthdays_header),
                style = MaterialTheme.typography.titleMedium
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.notification_time)) },
                supportingContent = { Text(notificationTime) }
            )

            HorizontalDivider()
            Text(
                stringResource(id = R.string.backup_header),
                style = MaterialTheme.typography.titleMedium
            )

            val backupLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree()
            ) { uri ->
                uri?.let { viewModel.setBackupFolder(it.toString()) }
            }

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.backup_location)) },
                supportingContent = { Text(backupUri ?: stringResource(id = R.string.not_set)) },
                trailingContent = {
                    TextButton(onClick = { backupLauncher.launch(null) }) {
                        Text(stringResource(id = R.string.select))
                    }
                }
            )
        }
    }
}
