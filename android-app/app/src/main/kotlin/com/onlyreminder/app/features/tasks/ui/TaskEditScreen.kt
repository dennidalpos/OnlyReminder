package com.onlyreminder.app.features.tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.tasks.presentation.TaskEditViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    navController: NavController,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val task by viewModel.task.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val templates by viewModel.templates.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = if (task?.id == 0L) "New Task" else "Edit Task",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
                    modifier = Modifier.fillMaxWidth().height(100.dp)
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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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
                            modifier = Modifier.menuAnchor().fillMaxWidth()
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
                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                Text("Due Date: ${dateFormat.format(Date(t.dueDateTime))}")
                // In a real app, I'd add DatePickerDialog and TimePickerDialog here.
            }
        }
    }
}
