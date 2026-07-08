package com.onlyreminder.app.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
    val normalizePhone by viewModel.normalizePhone.collectAsState()
    val templates by viewModel.templates.collectAsState()
    val birthdayTemplateId by viewModel.birthdayTemplateId.collectAsState()
    val showBackupBanner by viewModel.showBackupBanner.collectAsState()

    var showTimePicker by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }
    var showSendModePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.settings_title)
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
                headlineContent = { Text(stringResource(id = R.string.normalize_phone_label)) },
                supportingContent = { Text(stringResource(id = R.string.normalize_phone_desc)) },
                trailingContent = {
                    Switch(
                        checked = normalizePhone,
                        onCheckedChange = { viewModel.setNormalizePhone(it) })
                }
            )

            if (normalizePhone) {
                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.default_country_code)) },
                    supportingContent = { Text(countryCode) },
                    trailingContent = {
                        TextButton(onClick = { showCountryPicker = true }) {
                            Text(stringResource(id = R.string.change))
                        }
                    }
                )
            }

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.send_mode_selection)) },
                supportingContent = {
                    Text(
                        when (sendMode) {
                            com.onlyreminder.app.domain.model.SendMode.REMINDER_ONLY -> stringResource(id = R.string.send_mode_reminder_only)
                            com.onlyreminder.app.domain.model.SendMode.MANUAL_WHATSAPP -> stringResource(id = R.string.send_mode_manual_wa)
                            com.onlyreminder.app.domain.model.SendMode.WA_API -> stringResource(id = R.string.send_mode_wa_api)
                        }
                    )
                },
                trailingContent = {
                    TextButton(onClick = { showSendModePicker = true }) {
                        Text(stringResource(id = R.string.change))
                    }
                }
            )

            HorizontalDivider()
            Text(
                stringResource(id = R.string.security_title),
                style = MaterialTheme.typography.titleMedium
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.change_pin)) },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                },
                modifier = Modifier.clickable { 
                    navController.navigate(Route.Security)
                }
            )

            HorizontalDivider()
            Text(
                stringResource(id = R.string.birthdays_header),
                style = MaterialTheme.typography.titleMedium
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.notification_time)) },
                supportingContent = { Text(notificationTime) },
                trailingContent = {
                    TextButton(onClick = { showTimePicker = true }) {
                        Text(stringResource(id = R.string.change))
                    }
                }
            )

            val selectedTemplate = templates.find { it.id == birthdayTemplateId }
            ListItem(
                headlineContent = { Text(stringResource(id = R.string.birthday_template)) },
                supportingContent = { 
                    Text(selectedTemplate?.name ?: stringResource(id = R.string.not_set)) 
                },
                trailingContent = {
                    TextButton(onClick = { showTemplatePicker = true }) {
                        Text(stringResource(id = R.string.change))
                    }
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.templates_title)) },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                },
                modifier = Modifier.clickable { navController.navigate(Route.Templates) }
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

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.show_backup_banner)) },
                supportingContent = { Text(stringResource(id = R.string.show_backup_banner_desc)) },
                trailingContent = {
                    Switch(
                        checked = showBackupBanner,
                        onCheckedChange = { viewModel.setShowBackupBanner(it) })
                }
            )
        }
    }

    if (showSendModePicker) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSendModePicker = false },
            title = { Text(stringResource(id = R.string.send_mode_selection)) },
            text = {
                Column {
                    com.onlyreminder.app.domain.model.SendMode.entries.forEach { mode ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    when (mode) {
                                        com.onlyreminder.app.domain.model.SendMode.REMINDER_ONLY -> stringResource(id = R.string.send_mode_reminder_only)
                                        com.onlyreminder.app.domain.model.SendMode.MANUAL_WHATSAPP -> stringResource(id = R.string.send_mode_manual_wa)
                                        com.onlyreminder.app.domain.model.SendMode.WA_API -> stringResource(id = R.string.send_mode_wa_api)
                                    }
                                )
                            },
                            modifier = Modifier.clickable {
                                viewModel.setSendMode(mode)
                                showSendModePicker = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSendModePicker = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showTimePicker) {
        val currentTime = try { LocalTime.parse(notificationTime) } catch(_: Exception) { LocalTime.of(9, 0) }
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute
        )

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                    viewModel.setBirthdayNotificationTime(newTime)
                    showTimePicker = false
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    if (showCountryPicker) {
        val countries = com.onlyreminder.app.core.security.SecurityUtils.countryCodes
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCountryPicker = false },
            title = { Text(stringResource(id = R.string.select_country_code)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    countries.forEach { country ->
                        ListItem(
                            headlineContent = { Text("${country.code} (${country.name})") },
                            modifier = Modifier.clickable {
                                viewModel.setDefaultCountryCode(country.code)
                                showCountryPicker = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryPicker = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showTemplatePicker) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTemplatePicker = false },
            title = { Text(stringResource(id = R.string.select_template)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    ListItem(
                        headlineContent = { Text(stringResource(id = R.string.none)) },
                        modifier = Modifier.clickable {
                            viewModel.setBirthdayTemplateId(null)
                            showTemplatePicker = false
                        }
                    )
                    templates.forEach { template ->
                        ListItem(
                            headlineContent = { Text(template.name) },
                            supportingContent = { Text(template.language) },
                            modifier = Modifier.clickable {
                                viewModel.setBirthdayTemplateId(template.id)
                                showTemplatePicker = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTemplatePicker = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}
