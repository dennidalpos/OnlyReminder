package com.onlyreminder.app.features.birthday.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.domain.model.BirthdayItemStatus
import com.onlyreminder.app.features.birthday.presentation.BirthdayReviewViewModel

@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: BirthdayReviewViewModel = hiltViewModel()
) {
    val latestRun by viewModel.latestRun.collectAsState()
    val items by viewModel.items.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.report_finale)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            latestRun?.let { run ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.summary),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ReportRow(
                            stringResource(id = R.string.total_found),
                            run.totalFound.toString()
                        )
                        ReportRow(
                            stringResource(id = R.string.status_sent),
                            items.count { it.item.status == BirthdayItemStatus.SENT || it.item.status == BirthdayItemStatus.SENT_MANUAL }
                                .toString(),
                            Color(0xFF4CAF50)
                        )
                        ReportRow(
                            stringResource(id = R.string.status_failed),
                            items.count { it.item.status == BirthdayItemStatus.FAILED }.toString(),
                            MaterialTheme.colorScheme.error
                        )
                        ReportRow(
                            stringResource(id = R.string.status_skipped),
                            items.count { it.item.status == BirthdayItemStatus.SKIPPED }.toString(),
                            Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.details),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { itemWithContact ->
                    ReportItemRow(itemWithContact)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.completeRun()
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.finish))
            }
        }
    }
}

@Composable
fun ReportRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ReportItemRow(itemWithContact: com.onlyreminder.app.features.birthday.presentation.BirthdayRunItemWithContact) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (icon, color) = when (itemWithContact.item.status) {
            BirthdayItemStatus.SENT, BirthdayItemStatus.SENT_MANUAL -> Icons.Default.CheckCircle to Color(
                0xFF4CAF50
            )

            BirthdayItemStatus.FAILED -> Icons.Default.Error to MaterialTheme.colorScheme.error
            else -> Icons.Default.Info to Color.Gray
        }
        Icon(imageVector = icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = itemWithContact.contact?.displayName ?: stringResource(id = R.string.none),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = itemWithContact.item.status.name,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}
