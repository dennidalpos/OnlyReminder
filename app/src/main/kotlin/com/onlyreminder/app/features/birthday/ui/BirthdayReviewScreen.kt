package com.onlyreminder.app.features.birthday.ui

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.domain.model.BirthdayItemStatus
import com.onlyreminder.app.domain.model.SendMode
import com.onlyreminder.app.features.birthday.presentation.BirthdayReviewViewModel
import com.onlyreminder.app.features.birthday.presentation.BirthdayRunItemWithContact

@Composable
fun BirthdayReviewScreen(
    navController: NavController,
    viewModel: BirthdayReviewViewModel = hiltViewModel()
) {
    val latestRun by viewModel.latestRun.collectAsState()
    val items by viewModel.items.collectAsState()
    val sendMode by viewModel.sendMode.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.birthday_review_title),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (latestRun == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(id = R.string.no_birthday_runs))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                RunSummaryHeader(latestRun!!)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { itemWithContact ->
                        BirthdayReviewItem(
                            itemWithContact = itemWithContact,
                            sendMode = sendMode,
                            onSendIndividual = {
                                if (sendMode == SendMode.WA_API) {
                                    viewModel.sendItemViaApi(itemWithContact)
                                } else {
                                    latestRun?.let { run ->
                                        navController.navigate(Route.WhatsApp(run.id))
                                    }
                                }
                            },
                            onStatusChange = { status ->
                                viewModel.updateItemStatus(
                                    itemWithContact.item.id,
                                    status
                                )
                            },
                            onDeleteContact = {
                                itemWithContact.contact?.let {
                                    viewModel.deleteContact(
                                        it
                                    )
                                }
                            }
                        )
                    }
                }

                val pendingCount = items.count { it.item.status == BirthdayItemStatus.PENDING }

                BottomActionArea(
                    isRunEmpty = items.isEmpty(),
                    hasPending = pendingCount > 0,
                    onSendAll = {
                        latestRun?.let { run ->
                            navController.navigate(Route.WhatsApp(run.id))
                        }
                    },
                    onSkipAll = { viewModel.skipAll() },
                    onCompleteRun = {
                        viewModel.completeRun()
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

@Composable
fun RunSummaryHeader(run: com.onlyreminder.app.data.database.entities.BirthdayRunEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.scan_date, run.date),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.contacts_found, run.totalFound),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(id = R.string.status) + ": " + when (run.status) {
                    com.onlyreminder.app.domain.model.BirthdayRunStatus.PENDING -> stringResource(id = R.string.status_pending)
                    com.onlyreminder.app.domain.model.BirthdayRunStatus.COMPLETED -> stringResource(
                        id = R.string.status_completed
                    )

                    com.onlyreminder.app.domain.model.BirthdayRunStatus.NOT_REVIEWED -> stringResource(
                        id = R.string.status_not_reviewed
                    )
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun BirthdayReviewItem(
    itemWithContact: BirthdayRunItemWithContact,
    sendMode: SendMode,
    onSendIndividual: () -> Unit,
    onStatusChange: (BirthdayItemStatus) -> Unit,
    onDeleteContact: () -> Unit
) {
    val contact = itemWithContact.contact
    val item = itemWithContact.item

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = contact?.displayName ?: stringResource(id = R.string.none),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = contact?.phone ?: stringResource(id = R.string.not_set),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.delete_contact)) },
                            onClick = { onDeleteContact(); showMenu = false },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.generatedMessagePreview,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { onStatusChange(BirthdayItemStatus.SKIPPED) }) {
                    Text(stringResource(id = R.string.skip))
                }
                if (item.status == BirthdayItemStatus.PENDING) {
                    Button(onClick = onSendIndividual) {
                        Text(
                            if (sendMode == SendMode.WA_API) stringResource(R.string.send_now_api) else stringResource(
                                id = R.string.prepare_send
                            )
                        )
                    }
                } else {
                    Text(
                        text = when (item.status) {
                            BirthdayItemStatus.SENT, BirthdayItemStatus.SENT_MANUAL -> stringResource(
                                id = R.string.status_sent
                            )

                            BirthdayItemStatus.FAILED -> stringResource(id = R.string.status_failed)
                            BirthdayItemStatus.SKIPPED -> stringResource(id = R.string.status_skipped)
                            BirthdayItemStatus.PENDING -> item.status.name
                        },
                        color = if (item.status == BirthdayItemStatus.SENT || item.status == BirthdayItemStatus.SENT_MANUAL) Color(
                            0xFF4CAF50
                        ) else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun BottomActionArea(
    isRunEmpty: Boolean,
    hasPending: Boolean,
    onSendAll: () -> Unit,
    onSkipAll: () -> Unit,
    onCompleteRun: () -> Unit
) {
    Surface(tonalElevation = 4.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (hasPending) {
                OutlinedButton(onClick = onSkipAll, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.skip_all))
                }
                Button(onClick = onSendAll, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.send_selected))
                }
            } else if (!isRunEmpty) {
                Button(onClick = onCompleteRun, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.mark_as_completed))
                }
            }
        }
    }
}
