package com.onlyreminder.app.features.groups.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.EmptyState
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.data.database.entities.GroupEntity

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupsViewModel = hiltViewModel()
) {
    val groups by viewModel.groups.collectAsState()
    val selectedGroupIds by viewModel.selectedGroupIds.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    val isSelectionMode = selectedGroupIds.isNotEmpty()

    BackHandler(enabled = isSelectionMode) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                OnlyReminderTopBar(
                    title = stringResource(id = R.string.items_selected, selectedGroupIds.size),
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAllGroups() }) {
                            Icon(
                                Icons.Default.SelectAll,
                                contentDescription = stringResource(id = R.string.select_all)
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.delete_selected)
                            )
                        }
                    }
                )
            } else {
                OnlyReminderTopBar(
                    title = stringResource(id = R.string.groups_title),
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
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(onClick = { navController.navigate(Route.GroupEdit()) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_group)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (groups.isEmpty()) {
            EmptyState(
                message = stringResource(id = R.string.no_groups),
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(groups) { group ->
                    val isSelected = selectedGroupIds.contains(group.id)
                    GroupItem(
                        group = group,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                viewModel.toggleGroupSelection(group.id)
                            }
                        },
                        onLongClick = { viewModel.toggleGroupSelection(group.id) },
                        onEdit = { navController.navigate(Route.GroupEdit(group.id)) },
                        onDelete = { viewModel.deleteGroup(group) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(id = R.string.delete_selected)) },
                text = {
                    Text(
                        stringResource(
                            id = R.string.delete_selected_confirm_msg,
                            selectedGroupIds.size
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteSelectedGroups()
                        showDeleteDialog = false
                    }) {
                        Text(stringResource(id = R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    group: GroupEntity,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = group.name) },
        supportingContent = {
            if (group.description.isNotEmpty()) {
                Text(text = group.description)
            }
        },
        leadingContent = {
            if (isSelectionMode) {
                Checkbox(checked = isSelected, onCheckedChange = { onClick() })
            }
        },
        trailingContent = {
            if (!isSelectionMode) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    )
}
