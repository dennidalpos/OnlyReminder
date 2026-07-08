package com.onlyreminder.app.features.contacts.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.EmptyState
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.domain.model.ContactStatus
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val contactUiModels by viewModel.contactUiModels.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val selectedContactIds by viewModel.selectedContactIds.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAssignGroupDialog by remember { mutableStateOf(false) }
    var showAssignTaskDialog by remember { mutableStateOf(false) }

    val isSelectionMode = selectedContactIds.isNotEmpty()

    BackHandler(enabled = isSelectionMode) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                OnlyReminderTopBar(
                    title = stringResource(id = R.string.items_selected, selectedContactIds.size),
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAllContacts() }) {
                            Icon(
                                Icons.Default.SelectAll,
                                contentDescription = stringResource(id = R.string.select_all)
                            )
                        }
                        IconButton(onClick = { showAssignGroupDialog = true }) {
                            Icon(
                                Icons.Default.GroupAdd,
                                contentDescription = stringResource(id = R.string.assign_to_group)
                            )
                        }
                        IconButton(onClick = { showAssignTaskDialog = true }) {
                            Icon(
                                Icons.Default.Assignment,
                                contentDescription = stringResource(id = R.string.assign_task)
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
                    title = stringResource(id = R.string.contacts_title),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showFilterSheet = true }) {
                            val isFiltered =
                                selectedGroupId != null || selectedStatus != null || selectedTag != null
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(id = R.string.filters),
                                tint = if (isFiltered) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = R.string.import_contacts)) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.FileUpload,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        navController.navigate(Route.Import)
                                    }
                                )
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(onClick = { navController.navigate(Route.ContactEdit()) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_contact)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (!isSelectionMode) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            if (contactUiModels.isEmpty()) {
                EmptyState(message = stringResource(id = R.string.no_contacts))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(contactUiModels) { model ->
                        val contact = model.contact
                        val isSelected = selectedContactIds.contains(contact.id)
                        ContactItem(
                            contact = contact,
                            hasActiveTasks = model.hasActiveTasks,
                            isSelected = isSelected,
                            isSelectionMode = isSelectionMode,
                            onLongClick = { viewModel.toggleContactSelection(contact.id) },
                            onClick = {
                                if (isSelectionMode) {
                                    viewModel.toggleContactSelection(contact.id)
                                } else {
                                    navController.navigate(Route.ContactDetail(contact.id))
                                }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
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
                            selectedContactIds.size
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteSelectedContacts()
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

        if (showAssignGroupDialog) {
            AssignGroupDialog(
                groups = groups,
                onGroupSelected = { groupId: Long? ->
                    viewModel.assignSelectedToGroup(groupId)
                    showAssignGroupDialog = false
                },
                onDismiss = { showAssignGroupDialog = false }
            )
        }

        if (showAssignTaskDialog) {
            AssignTaskDialog(
                onAssign = { title: String, description: String ->
                    viewModel.assignTaskToSelected(
                        title,
                        description,
                        LocalDateTime.now().plusDays(1)
                    )
                    showAssignTaskDialog = false
                },
                onDismiss = { showAssignTaskDialog = false },
                count = selectedContactIds.size
            )
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                groups = groups,
                tags = tags.map { it.name },
                selectedGroupId = selectedGroupId,
                selectedStatus = selectedStatus,
                selectedTag = selectedTag,
                onGroupSelected = viewModel::onGroupFilterChanged,
                onStatusSelected = viewModel::onStatusFilterChanged,
                onTagSelected = viewModel::onTagFilterChanged,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@Composable
fun AssignTaskDialog(
    onAssign: (String, String) -> Unit,
    onDismiss: () -> Unit,
    count: Int
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.assign_task_to_contacts, count)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.task_title_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.task_description_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAssign(title, description) },
                enabled = title.isNotBlank()
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

@Composable
fun AssignGroupDialog(
    groups: List<com.onlyreminder.app.data.database.entities.GroupEntity>,
    onGroupSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.move_to_group_title)) },
        text = {
            LazyColumn {
                item {
                    ListItem(
                        headlineContent = { Text(stringResource(id = R.string.no_group)) },
                        modifier = Modifier.clickable { onGroupSelected(null) }
                    )
                }
                items(groups) { group ->
                    ListItem(
                        headlineContent = { Text(group.name) },
                        modifier = Modifier.clickable { onGroupSelected(group.id) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.cancel)
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactItem(
    contact: ContactEntity,
    hasActiveTasks: Boolean,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = contact.displayName, fontWeight = FontWeight.SemiBold)
                if (hasActiveTasks) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Has active tasks",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        supportingContent = { Text(text = contact.phone.ifEmpty { contact.email }) },
        overlineContent = {
            if (contact.status == ContactStatus.ARCHIVED) {
                Text(
                    text = stringResource(id = R.string.archived).uppercase(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        leadingContent = {
            if (isSelected) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = contact.displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        trailingContent = {
            if (isSelectionMode) {
                Checkbox(checked = isSelected, onCheckedChange = { onClick() })
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    groups: List<com.onlyreminder.app.data.database.entities.GroupEntity>,
    tags: List<String>,
    selectedGroupId: Long?,
    selectedStatus: ContactStatus?,
    selectedTag: String?,
    onGroupSelected: (Long?) -> Unit,
    onStatusSelected: (ContactStatus?) -> Unit,
    onTagSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.filters),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.group),
                style = MaterialTheme.typography.labelLarge
            )
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(
                    selected = selectedGroupId == null,
                    onClick = { onGroupSelected(null) },
                    label = { Text(stringResource(id = R.string.all)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                groups.forEach { group ->
                    FilterChip(
                        selected = selectedGroupId == group.id,
                        onClick = { onGroupSelected(group.id) },
                        label = { Text(group.name) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Text(
                text = stringResource(id = R.string.status),
                style = MaterialTheme.typography.labelLarge
            )
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { onStatusSelected(null) },
                    label = { Text(stringResource(id = R.string.all)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedStatus == ContactStatus.ACTIVE,
                    onClick = { onStatusSelected(ContactStatus.ACTIVE) },
                    label = { Text(stringResource(id = R.string.active)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedStatus == ContactStatus.ARCHIVED,
                    onClick = { onStatusSelected(ContactStatus.ARCHIVED) },
                    label = { Text(stringResource(id = R.string.archived)) }
                )
            }

            if (tags.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.tags),
                    style = MaterialTheme.typography.labelLarge
                )
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    FilterChip(
                        selected = selectedTag == null,
                        onClick = { onTagSelected(null) },
                        label = { Text(stringResource(id = R.string.all)) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    tags.forEach { tag ->
                        FilterChip(
                            selected = selectedTag == tag,
                            onClick = { onTagSelected(tag) },
                            label = { Text(tag) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
