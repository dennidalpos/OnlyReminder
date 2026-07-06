package com.onlyreminder.app.features.contacts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Screen
import com.onlyreminder.app.core.ui.components.EmptyState
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.data.database.entities.ContactEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.contacts_title),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.import_csv)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.FileUpload,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(Screen.Import.route)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.ContactEdit.createRoute()) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_contact)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            if (contacts.isEmpty()) {
                EmptyState(message = stringResource(id = R.string.no_contacts))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(contacts) { contact ->
                        ContactItem(
                            contact = contact,
                            onClick = {
                                navController.navigate(
                                    Screen.ContactDetail.createRoute(
                                        contact.id
                                    )
                                )
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
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

@Composable
fun ContactItem(
    contact: ContactEntity,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = contact.displayName, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(text = contact.phone.ifEmpty { contact.email }) },
        overlineContent = {
            if (contact.status == "ARCHIVED") {
                Text(
                    text = stringResource(id = R.string.archived).uppercase(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        leadingContent = {
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
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    groups: List<com.onlyreminder.app.data.database.entities.GroupEntity>,
    tags: List<String>,
    selectedGroupId: Long?,
    selectedStatus: String?,
    selectedTag: String?,
    onGroupSelected: (Long?) -> Unit,
    onStatusSelected: (String?) -> Unit,
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
                    selected = selectedStatus == "ACTIVE",
                    onClick = { onStatusSelected("ACTIVE") },
                    label = { Text(stringResource(id = R.string.active)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedStatus == "ARCHIVED",
                    onClick = { onStatusSelected("ARCHIVED") },
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
