package com.onlyreminder.app.features.contacts.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.components.ConfirmationDialog
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditScreen(
    navController: NavController,
    viewModel: ContactEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val groups by viewModel.groups.collectAsState()
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
                title = if (uiState.displayName.isEmpty()) {
                    stringResource(id = R.string.new_contact)
                } else {
                    stringResource(id = R.string.edit_contact)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveContact { navController.navigateUp() } }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.save)
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
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = viewModel::onDisplayNameChange,
                label = { Text(stringResource(id = R.string.display_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = viewModel::onFirstNameChange,
                    label = { Text(stringResource(id = R.string.first_name)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = viewModel::onLastNameChange,
                    label = { Text(stringResource(id = R.string.last_name)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text(stringResource(id = R.string.phone)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(id = R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.company,
                onValueChange = viewModel::onCompanyChange,
                label = { Text(stringResource(id = R.string.company)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.birthday,
                onValueChange = viewModel::onBirthdayChange,
                label = { Text(stringResource(id = R.string.birthday_format)) },
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
                label = { Text(stringResource(id = R.string.tags_csv)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text(stringResource(id = R.string.notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))
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
            value = selectedGroup?.name ?: stringResource(id = R.string.no_group),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.group)) },
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
                text = { Text(stringResource(id = R.string.no_group)) },
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
