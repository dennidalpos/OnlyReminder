package com.onlyreminder.app.features.groups.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.data.database.entities.ContactEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupEditScreen(
    navController: NavController,
    viewModel: GroupEditViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val members by viewModel.members.collectAsState()
    val availableContacts by viewModel.availableContacts.collectAsState()

    var showAddMemberDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = if (name.isEmpty()) stringResource(id = R.string.new_group) else stringResource(id = R.string.edit_group),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveGroup { navController.navigateUp() } }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = stringResource(id = R.string.save))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(id = R.string.group_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(id = R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (name.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.members_title), style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { showAddMemberDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_member))
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(members) { contact ->
                        ListItem(
                            headlineContent = { Text(contact.displayName) },
                            supportingContent = { Text(contact.phone) },
                            trailingContent = {
                                IconButton(onClick = { viewModel.removeContactFromGroup(contact) }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.remove), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        if (showAddMemberDialog) {
            AlertDialog(
                onDismissRequest = { showAddMemberDialog = false },
                title = { Text(stringResource(id = R.string.add_members_title)) },
                text = {
                    if (availableContacts.isEmpty()) {
                        Text(stringResource(id = R.string.no_available_contacts))
                    } else {
                        LazyColumn {
                            items(availableContacts) { contact ->
                                ListItem(
                                    headlineContent = { Text(contact.displayName) },
                                    trailingContent = {
                                        IconButton(onClick = {
                                            viewModel.addContactToGroup(contact)
                                        }) {
                                            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_member))
                                        }
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddMemberDialog = false }) {
                        Text(stringResource(id = R.string.finish))
                    }
                }
            )
        }
    }
}
