package com.onlyreminder.app.features.contacts.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
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
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Route.ContactEdit()) }) {
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
                                navController.navigate(Route.ContactDetail(contact.id))
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
            if (contact.status == ContactStatus.ARCHIVED) {
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
