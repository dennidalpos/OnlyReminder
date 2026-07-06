package com.onlyreminder.app.features.contacts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditScreen(
    navController: NavController,
    viewModel: ContactEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val groups by viewModel.groups.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = if (uiState.displayName.isEmpty()) "New Contact" else "Edit Contact",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveContact { navController.navigateUp() } }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
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
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = viewModel::onDisplayNameChange,
                label = { Text("Display Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = viewModel::onFirstNameChange,
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = viewModel::onLastNameChange,
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.company,
                onValueChange = viewModel::onCompanyChange,
                label = { Text("Company") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.birthday,
                onValueChange = viewModel::onBirthdayChange,
                label = { Text("Birthday (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            GroupDropdown(
                selectedGroupId = uiState.groupId,
                groups = groups,
                onGroupSelected = viewModel::onGroupChange
            )

            OutlinedTextField(
                value = uiState.tags,
                onValueChange = viewModel::onTagsChange,
                label = { Text("Tags (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDropdown(
    selectedGroupId: Long?,
    groups: List<com.onlyreminder.app.data.database.entities.GroupEntity>,
    onGroupSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedGroup = groups.find { it.id == selectedGroupId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedGroup?.name ?: "No Group",
            onValueChange = {},
            readOnly = true,
            label = { Text("Group") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("No Group") },
                onClick = {
                    onGroupSelected(null)
                    expanded = false
                }
            )
            groups.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group.name) },
                    onClick = {
                        onGroupSelected(group.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
