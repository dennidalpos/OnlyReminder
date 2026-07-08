package com.onlyreminder.app.features.tasks.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.components.ConfirmationDialog
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.tasks.presentation.TaskEditViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    navController: NavController,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val task by viewModel.task.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val templates by viewModel.templates.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    val onBack = {
        if (viewModel.hasChanges) {
            showBackDialog = true
        } else {
            navController.navigateUp()
        }
        Unit
    }

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = if (task?.id == 0L) stringResource(id = R.string.new_task) else stringResource(
                    id = R.string.edit_task
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveTask { navController.navigateUp() } }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(id = R.string.save)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        task?.let { t ->
            val isBirthday = t.type == "BIRTHDAY"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = t.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text(stringResource(id = R.string.task_title)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = t.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text(stringResource(id = R.string.task_description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Event Type Selector
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = t.type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.event_type)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        listOf("REMINDER", "BIRTHDAY").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { viewModel.updateType(type); typeExpanded = false }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    // Contact Picker
                    var contactExpanded by remember { mutableStateOf(false) }
                    val selectedContact = contacts.find { it.id == t.contactId }
                    ExposedDropdownMenuBox(
                        expanded = contactExpanded,
                        onExpandedChange = { contactExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedContact?.displayName
                                ?: stringResource(id = R.string.no_contact),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.link_contact)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = contactExpanded
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = contactExpanded,
                            onDismissRequest = { contactExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.no_contact)) },
                                onClick = {
                                    viewModel.updateContact(null); contactExpanded = false
                                })
                            contacts.forEach { contact ->
                                DropdownMenuItem(
                                    text = { Text(contact.displayName) },
                                    onClick = {
                                        viewModel.updateContact(contact.id); contactExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Group Picker
                    var groupExpanded by remember { mutableStateOf(false) }
                    val selectedGroup = groups.find { it.id == t.groupId }
                    ExposedDropdownMenuBox(
                        expanded = groupExpanded,
                        onExpandedChange = { groupExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedGroup?.name ?: stringResource(id = R.string.no_group),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.link_group)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = groupExpanded
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = groupExpanded,
                            onDismissRequest = { groupExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.no_group)) },
                                onClick = { viewModel.updateGroup(null); groupExpanded = false })
                            groups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group.name) },
                                    onClick = {
                                        viewModel.updateGroup(group.id); groupExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Send Mode
                var modeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = modeExpanded,
                    onExpandedChange = { modeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = t.sendMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.send_mode_selection)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = modeExpanded,
                        onDismissRequest = { modeExpanded = false }
                    ) {
                        listOf(
                            "REMINDER_ONLY",
                            "MANUAL_WHATSAPP",
                            "WHATSAPP_BUSINESS_API"
                        ).forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode) },
                                onClick = { viewModel.updateSendMode(mode); modeExpanded = false })
                        }
                    }
                }

                // Template Picker
                var templateExpanded by remember { mutableStateOf(false) }
                val selectedTemplate = templates.find { it.id == t.templateId }
                ExposedDropdownMenuBox(
                    expanded = templateExpanded,
                    onExpandedChange = { templateExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTemplate?.name ?: stringResource(id = R.string.no_template),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.message_template)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = templateExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = templateExpanded,
                        onDismissRequest = { templateExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.no_template)) },
                            onClick = {
                                viewModel.updateTemplate(null); templateExpanded = false
                            })
                        templates.forEach { template ->
                            DropdownMenuItem(
                                text = { Text(template.name) },
                                onClick = {
                                    viewModel.updateTemplate(template.id); templateExpanded =
                                    false
                                }
                            )
                        }
                    }
                }

                // Due Date/Time
                Text(
                    text = stringResource(id = R.string.schedule_event),
                    style = MaterialTheme.typography.titleSmall
                )

                if (isBirthday) {
                    Text(
                        text = stringResource(id = R.string.birthday_check_time_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isBirthday) {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                stringResource(
                                    id = R.string.date_label,
                                    t.dueDateTime.toLocalDate()
                                )
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(if (isBirthday) 1f else 1f)
                    ) {
                        Text(
                            stringResource(
                                id = R.string.time_label,
                                t.dueDateTime.toLocalTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                            )
                        )
                    }
                }

                if (showDatePicker && !isBirthday) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = t.dueDateTime.atZone(ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                    )
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    val selectedDate = Instant.ofEpochMilli(it)
                                        .atZone(ZoneId.of("UTC"))
                                        .toLocalDate()
                                    val newDateTime = t.dueDateTime
                                        .withYear(selectedDate.year)
                                        .withMonth(selectedDate.monthValue)
                                        .withDayOfMonth(selectedDate.dayOfMonth)
                                    viewModel.updateDueDateTime(
                                        newDateTime.atZone(ZoneId.systemDefault()).toInstant()
                                            .toEpochMilli()
                                    )
                                }
                                showDatePicker = false
                            }) {
                                Text(stringResource(id = R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (showTimePicker) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = t.dueDateTime.hour,
                        initialMinute = t.dueDateTime.minute
                    )
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val newDateTime = t.dueDateTime
                                    .withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute)
                                viewModel.updateDueDateTime(
                                    newDateTime.atZone(ZoneId.systemDefault()).toInstant()
                                        .toEpochMilli()
                                )
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
            }
        }

        if (showBackDialog) {
            ConfirmationDialog(
                title = stringResource(id = R.string.unsaved_changes_title),
                message = stringResource(id = R.string.unsaved_changes_msg),
                onConfirm = {
                    showBackDialog = false
                    navController.navigateUp()
                },
                onDismiss = { showBackDialog = false }
            )
        }
    }
}
