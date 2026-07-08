package com.onlyreminder.app.features.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.onlyreminder.app.features.home.presentation.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.app_name),
                actions = {
                    IconButton(onClick = { navController.navigate(Route.Settings) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings_title)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Route.ContactEdit(null)) }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_contact)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Welcome & Status Summary
            SummarySection(uiState.contactCount, uiState.upcomingBirthdays.size)

            // 2. Urgent Action (Review)
            if (uiState.birthdaysTodayCount > 0 || uiState.birthdaysTomorrowCount > 0) {
                UrgentActionCard(
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
                    icon = Icons.Default.NotificationsActive,
                    onClick = { navController.navigate(Route.BirthdayReview) }
                )
            }

            if (uiState.pendingTasks.isNotEmpty()) {
                UrgentActionCard(
                    title = stringResource(R.string.tasks_title),
                    description = stringResource(
                        id = R.string.pending_tasks_summary,
                        uiState.pendingTasks.size
                    ),
                    icon = Icons.Default.Task,
                    onClick = { navController.navigate(Route.Tasks) }
                )
            }

            if (uiState.showBackupWarning) {
                UrgentActionCard(
                    title = stringResource(R.string.backup_required_title),
                    description = stringResource(R.string.backup_required_desc),
                    icon = Icons.Default.Backup,
                    onClick = { navController.navigate(Route.Backup) }
                )
            }

            Text(
                text = stringResource(R.string.management),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // 3. Clean Management Grid
            ManagementSection(navController)

            Spacer(modifier = Modifier.height(80.dp)) // FAB Space
        }
    }
}

@Composable
fun SummarySection(contactCount: Int, upcomingCount: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.hello),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (contactCount == 0) stringResource(R.string.start_by_adding_contacts) else stringResource(
                R.string.saved_contacts_count_new,
                contactCount
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (upcomingCount > 0) {
            Text(
                text = stringResource(R.string.upcoming_birthdays_count, upcomingCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UrgentActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ManagementSection(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ManagementRow(
            title = stringResource(R.string.contacts_title),
            icon = Icons.Default.Person,
            onClick = { navController.navigate(Route.Contacts) }
        )
        ManagementRow(
            title = stringResource(R.string.groups_title),
            icon = Icons.Default.Group,
            onClick = { navController.navigate(Route.Groups) }
        )
        ManagementRow(
            title = stringResource(R.string.tasks_title),
            icon = Icons.Default.Task,
            onClick = { navController.navigate(Route.Tasks) }
        )
        ManagementRow(
            title = stringResource(R.string.templates_title),
            icon = Icons.Default.Description,
            onClick = { navController.navigate(Route.Templates) }
        )
        ManagementRow(
            title = stringResource(R.string.import_contacts),
            icon = Icons.Default.Add, // Changed icon for clarity
            onClick = { navController.navigate(Route.Import) }
        )
    }
}

@Composable
fun ManagementRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
