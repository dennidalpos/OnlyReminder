package com.onlyreminder.app.features.groups.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = "Groups",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Route.GroupEdit()) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Group")
            }
        }
    ) { paddingValues ->
        if (groups.isEmpty()) {
            EmptyState(
                message = "No groups created yet.",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(groups) { group ->
                    GroupItem(
                        group = group,
                        onEdit = { navController.navigate(Route.GroupEdit(group.id)) },
                        onDelete = { viewModel.deleteGroup(group) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun GroupItem(
    group: GroupEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = group.name) },
        supportingContent = { if (group.description.isNotEmpty()) Text(text = group.description) else null },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}
