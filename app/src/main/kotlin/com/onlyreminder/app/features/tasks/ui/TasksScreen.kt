package com.onlyreminder.app.features.tasks.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
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
    val filterStatus by viewModel.filterStatus.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = "Tasks",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = { viewModel.setFilterStatus(null); expanded = false })
                        DropdownMenuItem(
                            text = { Text("Pending") },
                            onClick = {
                                viewModel.setFilterStatus(TaskStatus.PENDING); expanded = false
                            })
                        DropdownMenuItem(
                            text = { Text("Completed") },
                            onClick = {
                                viewModel.setFilterStatus(TaskStatus.COMPLETED); expanded = false
                            })
                        DropdownMenuItem(
                            text = { Text("Cancelled") },
                            onClick = {
                                viewModel.setFilterStatus(TaskStatus.CANCELLED); expanded = false
                            })
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Route.TaskEdit()) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
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
                Text("No tasks found.")
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
                    TaskItem(
                        task = task,
                        onEdit = { navController.navigate(Route.TaskEdit(task.id)) },
                        onComplete = { viewModel.updateTaskStatus(task.id, TaskStatus.COMPLETED) },
                        onSkip = { viewModel.updateTaskStatus(task.id, TaskStatus.CANCELLED) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm") }
    val isPast = task.dueDateTime.isBefore(LocalDateTime.now()) && task.status == TaskStatus.PENDING

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit,
        colors = if (isPast) CardDefaults.cardColors(
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
                        Text("Cancel")
                    }
                    Button(onClick = onComplete) {
                        Text("Complete")
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
            text = status.name,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
