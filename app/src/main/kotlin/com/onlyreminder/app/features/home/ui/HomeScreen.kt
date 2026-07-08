package com.onlyreminder.app.features.home.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.features.home.presentation.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.app_name)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Welcome Section
            item {
                Column {
                    Text(
                        text = stringResource(R.string.hello),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (uiState.contactCount == 0) stringResource(R.string.start_by_adding_contacts)
                        else stringResource(R.string.saved_contacts_count_new, uiState.contactCount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Urgent Actions
            if ((uiState.birthdaysTodayCount > 0) || (uiState.birthdaysTomorrowCount > 0)) {
                item {
                    ActionCard(
                        title = stringResource(R.string.birthday_review_title),
                        description = buildString {
                            if (uiState.birthdaysTodayCount > 0) {
                                append(stringResource(R.string.birthdays_today_count, uiState.birthdaysTodayCount))
                            }
                            if (uiState.birthdaysTomorrowCount > 0) {
                                if (isNotEmpty()) append(" • ")
                                append(stringResource(R.string.birthdays_tomorrow_count, uiState.birthdaysTomorrowCount))
                            }
                        },
                        icon = Icons.Default.Cake,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = { navController.navigate(Route.BirthdayReview) }
                    )
                }
            }

            if (uiState.pendingTasks.isNotEmpty()) {
                item {
                    ActionCard(
                        title = stringResource(R.string.tasks_title),
                        description = stringResource(R.string.pending_tasks_summary, uiState.pendingTasks.size),
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { navController.navigate(Route.Tasks) }
                    )
                }
            } else {
                item {
                    ActionCard(
                        title = stringResource(R.string.tasks_title),
                        description = stringResource(R.string.no_pending_tasks),
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { navController.navigate(Route.Tasks) }
                    )
                }
            }

            item {
                ActionCard(
                    title = stringResource(R.string.contacts_title),
                    description = stringResource(R.string.manage_contacts_desc),
                    icon = Icons.Default.Contacts,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { navController.navigate(Route.Contacts) }
                )
            }

            if (uiState.showBackupWarning) {
                item {
                    ActionCard(
                        title = stringResource(R.string.backup_required_title),
                        description = stringResource(R.string.backup_required_desc),
                        icon = Icons.Default.Backup,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { navController.navigate(Route.Backup) }
                    )
                }
            }

            // 3. Upcoming Birthdays Horizontal List
            if (uiState.upcomingBirthdays.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = stringResource(R.string.upcoming_birthdays_count, uiState.upcomingBirthdays.size),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 16.dp)
                        ) {
                            items(uiState.upcomingBirthdays) { contact ->
                                BirthdayAvatar(contact = contact) {
                                    navController.navigate(Route.ContactDetail(contact.id))
                                }
                            }
                        }
                    }
                }
            }

            // 4. Tools Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.tools),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ToolItem(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.groups_title),
                            icon = Icons.Default.Group,
                            onClick = { navController.navigate(Route.Groups) }
                        )
                        ToolItem(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.templates_title),
                            icon = Icons.Default.Description,
                            onClick = { navController.navigate(Route.Templates) }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ToolItem(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.import_contacts),
                            icon = Icons.Default.FileUpload,
                            onClick = { navController.navigate(Route.Import) }
                        )
                        ToolItem(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.backup_title),
                            icon = Icons.Default.Backup,
                            onClick = { navController.navigate(Route.Backup) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun BirthdayAvatar(contact: ContactEntity, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(72.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = contact.displayName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = contact.displayName.split(" ").first(),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = contact.birthday?.takeLast(5) ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ToolItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(80.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}
