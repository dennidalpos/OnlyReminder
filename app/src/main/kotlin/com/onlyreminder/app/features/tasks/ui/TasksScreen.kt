package com.onlyreminder.app.features.tasks.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.domain.model.TaskStatus
import com.onlyreminder.app.features.tasks.presentation.TasksViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val selectedTaskIds by viewModel.selectedTaskIds.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    val isSelectionMode = selectedTaskIds.isNotEmpty()

    BackHandler(enabled = isSelectionMode) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                OnlyReminderTopBar(
                    title = stringResource(id = R.string.items_selected, selectedTaskIds.size),
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAllTasks() }) {
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
                    title = stringResource(id = R.string.tasks_title),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = stringResource(id = R.string.filters)
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.all)) },
                                onClick = { viewModel.setFilterStatus(null); expanded = false })
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.status_pending)) },
                                onClick = {
                                    viewModel.setFilterStatus(TaskStatus.PENDING); expanded = false
                                })
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.status_completed)) },
                                onClick = {
                                    viewModel.setFilterStatus(TaskStatus.COMPLETED); expanded =
                                    false
                                })
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.status_cancelled)) },
                                onClick = {
                                    viewModel.setFilterStatus(TaskStatus.CANCELLED); expanded =
                                    false
                                })
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(onClick = { navController.navigate(Route.TaskEdit()) }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_contact)
                    ) // Reusing add_contact or could use a new string
                }
            }
        }
    ) { paddingValues ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(id = R.string.none))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    val isSelected = selectedTaskIds.contains(task.id)
                    TaskItem(
                        task = task,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                viewModel.toggleTaskSelection(task.id)
                            } else {
                                navController.navigate(Route.TaskEdit(task.id))
                            }
                        },
                        onLongClick = { viewModel.toggleTaskSelection(task.id) },
                        onComplete = { viewModel.updateTaskStatus(task.id, TaskStatus.COMPLETED) },
                        onSkip = { viewModel.updateTaskStatus(task.id, TaskStatus.CANCELLED) }
                    )
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
                            selectedTaskIds.size
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteSelectedTasks()
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
fun TaskItem(
    task: TaskEntity,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm") }
    val isPast =
        (task.dueDateTime.isBefore(LocalDateTime.now()) && task.status == TaskStatus.PENDING)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        else if (isPast) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                alpha = 0.1f
            )
        ) else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelectionMode) {
                    Checkbox(checked = isSelected, onCheckedChange = { onClick() })
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = task.dueDateTime.format(dateTimeFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPast) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                }

                StatusBadge(status = task.status)
            }

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }

            if (task.status == TaskStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onSkip) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Button(onClick = onComplete) {
                        Text(stringResource(id = R.string.complete))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: TaskStatus) {
    val color = when (status) {
        TaskStatus.COMPLETED -> Color(0xFF4CAF50)
        TaskStatus.CANCELLED -> Color.Gray
        TaskStatus.PENDING -> MaterialTheme.colorScheme.primary
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = when (status) {
                TaskStatus.PENDING -> stringResource(id = R.string.status_pending)
                TaskStatus.COMPLETED -> stringResource(id = R.string.status_completed)
                TaskStatus.CANCELLED -> stringResource(id = R.string.status_cancelled)
            },
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
