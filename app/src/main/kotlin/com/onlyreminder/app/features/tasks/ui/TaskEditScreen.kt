package com.onlyreminder.app.features.tasks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.tasks.presentation.TaskEditViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    navController: NavController,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val task by viewModel.task.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val templates by viewModel.templates.collectAsState()

    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm") }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = if (task?.id == 0L) "New Task" else "Edit Task",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveTask { navController.navigateUp() } }) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        task?.let { t ->
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
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = t.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Contact Picker
                var contactExpanded by remember { mutableStateOf(false) }
                val selectedContact = contacts.find { it.id == t.contactId }
                ExposedDropdownMenuBox(
                    expanded = contactExpanded,
                    onExpandedChange = { contactExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedContact?.displayName ?: "No Contact",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Link Contact") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = contactExpanded) },
                        modifier = Modifier
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = contactExpanded,
                        onDismissRequest = { contactExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("No Contact") },
                            onClick = { viewModel.updateContact(null); contactExpanded = false })
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

                // Send Mode
                var modeExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { modeExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Mode: ${t.sendMode}")
                    }
                    DropdownMenu(
                        expanded = modeExpanded,
                        onDismissRequest = { modeExpanded = false }) {
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

                if (t.sendMode != "REMINDER_ONLY") {
                    // Template Picker
                    var templateExpanded by remember { mutableStateOf(false) }
                    val selectedTemplate = templates.find { it.id == t.templateId }
                    ExposedDropdownMenuBox(
                        expanded = templateExpanded,
                        onExpandedChange = { templateExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedTemplate?.name ?: "No Template",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Message Template") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = templateExpanded) },
                            modifier = Modifier
                                .menuAnchor(
                                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                )
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = templateExpanded,
                            onDismissRequest = { templateExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("No Template") },
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
                }

                // Due Date/Time (Simplified for now)
                Text("Due Date: ${t.dueDateTime.format(dateTimeFormatter)}")
                // In a real app, I'd add DatePickerDialog and TimePickerDialog here.
            }
        }
    }
}
