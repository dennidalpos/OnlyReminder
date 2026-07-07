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
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
        topBar = { OnlyReminderTopBar(title = "OnlyReminder") },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Route.ContactEdit(null)) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard Summary
            SummarySection(uiState.contactCount, uiState.upcomingBirthdays.size)

            // Action Required Banner
            if (uiState.birthdayReviewRequired || uiState.pendingTasks.isNotEmpty()) {
                ActionRequiredBanner(
                    birthdayReviewRequired = uiState.birthdayReviewRequired,
                    pendingTasksCount = uiState.pendingTasks.size,
                    onActionClick = {
                        if (uiState.birthdayReviewRequired) {
                            navController.navigate(Route.BirthdayReview)
                        } else {
                            navController.navigate(Route.Tasks)
                        }
                    }
                )
            }

            Text(
                text = stringResource(R.string.what_do_you_want_to_do),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Primary Actions Grid
            val mainActions = listOf(
                DashboardAction(
                    stringResource(R.string.contacts_title),
                    Icons.Default.Person,
                    Route.Contacts
                ),
                DashboardAction(
                    stringResource(R.string.groups_title),
                    Icons.Default.Group,
                    Route.Groups
                ),
                DashboardAction(
                    stringResource(R.string.tasks_title),
                    Icons.Default.Task,
                    Route.Tasks
                ),
                DashboardAction(
                    stringResource(R.string.templates_title),
                    Icons.Default.Description,
                    Route.Templates
                ),
            )

            DashboardGrid(mainActions) { route ->
                navController.navigate(route)
            }

            Text(
                text = stringResource(R.string.tools),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Tools Section
            val tools = mutableListOf(
                DashboardAction(
                    stringResource(R.string.import_contacts),
                    Icons.Default.ImportExport,
                    Route.Import
                ),
                DashboardAction(
                    stringResource(R.string.birthday_review_title),
                    Icons.Default.Cake,
                    Route.BirthdayReview
                ),
                DashboardAction(
                    stringResource(R.string.backup_title),
                    Icons.Default.Backup,
                    Route.Backup
                ),
            )

            if (uiState.sendMode == "WA_API") {
                tools.add(
                    DashboardAction(
                        stringResource(R.string.config_wa_api),
                        Icons.Default.Settings,
                        Route.WhatsAppApi
                    )
                )
            }

            tools.forEach { tool ->
                ToolRow(tool) {
                    navController.navigate(tool.route)
                }
            }

            Text(
                text = stringResource(R.string.app),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val appSettings = listOf(
                DashboardAction(
                    stringResource(R.string.settings_title),
                    Icons.Default.Settings,
                    Route.Settings
                ),
                DashboardAction(
                    stringResource(R.string.security_title),
                    Icons.Default.Lock,
                    Route.Security
                ),
            )

            appSettings.forEach { setting ->
                ToolRow(setting) {
                    navController.navigate(setting.route)
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}

@Composable
fun ActionRequiredBanner(
    birthdayReviewRequired: Boolean,
    pendingTasksCount: Int,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onActionClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Action Required",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
                val message = when {
                    birthdayReviewRequired && pendingTasksCount > 0 ->
                        "You have birthdays to review and $pendingTasksCount pending tasks."

                    birthdayReviewRequired ->
                        "You have birthdays to review."

                    else ->
                        "You have $pendingTasksCount pending tasks."
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun SummarySection(contactCount: Int, upcomingCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(R.string.saved_contacts_count, contactCount),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (upcomingCount > 0) {
                Text(
                    text = stringResource(R.string.upcoming_birthdays_count, upcomingCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DashboardGrid(actions: List<DashboardAction>, onActionClick: (Route) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val chunks = actions.chunked(2)
        chunks.forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowActions.forEach { action ->
                    ActionCard(
                        action = action,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionClick(action.route) }
                    )
                }
                if (rowActions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ActionCard(action: DashboardAction, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun ToolRow(action: DashboardAction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = action.title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

data class DashboardAction(
    val title: String,
    val icon: ImageVector,
    val route: Route
)
