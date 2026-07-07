package com.onlyreminder.app.features.contacts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.DestructiveConfirmationDialog
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.domain.model.ContactStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContactDetailScreen(
    navController: NavController,
    viewModel: ContactDetailViewModel = hiltViewModel(),
) {
    val contact by viewModel.contact.collectAsState()
    val group by viewModel.group.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.contact_details),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Route.ContactEdit(contact?.id))
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        contact?.let { c ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // Header with initials
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = c.displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = c.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                if (c.company.isNotEmpty()) {
                    Text(
                        text = c.company,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                DetailItem(
                    icon = Icons.Default.Phone,
                    label = stringResource(id = R.string.phone),
                    value = c.phone
                )
                if (c.normalizedPhone.isNotEmpty() && (c.normalizedPhone != c.phone)) {
                    DetailItem(
                        icon = Icons.Default.Dialpad,
                        label = stringResource(id = R.string.normalized),
                        value = c.normalizedPhone,
                    )
                }
                DetailItem(
                    icon = Icons.Default.Email,
                    label = stringResource(id = R.string.email),
                    value = c.email
                )
                DetailItem(
                    icon = Icons.Default.Cake,
                    label = stringResource(id = R.string.birthday),
                    value = c.birthday ?: stringResource(id = R.string.not_set),
                )
                DetailItem(
                    icon = Icons.Default.Group,
                    label = stringResource(id = R.string.group),
                    value = group?.name ?: stringResource(id = R.string.none)
                )

                if (tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.tags),
                        style = MaterialTheme.typography.labelLarge
                    )
                    FlowRow(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            SuggestionChip(onClick = { }, label = { Text(tag.name) })
                        }
                    }
                }

                DetailItem(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    label = stringResource(id = R.string.notes),
                    value = c.notes
                )
                DetailItem(
                    icon = Icons.Default.Info,
                    label = stringResource(id = R.string.status),
                    value = c.status.name
                )

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                DetailAction(
                    label = stringResource(id = R.string.create_task),
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onClick = {
                        navController.navigate(Route.TaskEdit(contactId = c.id))
                    }
                )
                DetailAction(
                    label = stringResource(id = R.string.send_wa),
                    icon = Icons.AutoMirrored.Filled.Send,
                    onClick = {
                        viewModel.openWhatsApp(context)
                    }
                )

                if (c.status != ContactStatus.ARCHIVED) {
                    Button(
                        onClick = { viewModel.archiveContact() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(stringResource(id = R.string.archive_contact))
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        if (showDeleteDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(id = R.string.delete_contact_confirm_title),
                message = stringResource(id = R.string.delete_contact_confirm_msg),
                onConfirm = {
                    viewModel.deleteContact {
                        navController.navigateUp()
                    }
                    showDeleteDialog = false
                },
            ) { showDeleteDialog = false }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    if (value.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(text = value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun DetailAction(label: String, icon: ImageVector, onClick: () -> Unit) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
