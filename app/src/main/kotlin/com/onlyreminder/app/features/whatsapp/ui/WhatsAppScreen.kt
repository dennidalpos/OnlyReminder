package com.onlyreminder.app.features.whatsapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.whatsapp.domain.WhatsAppManualManager
import com.onlyreminder.app.features.whatsapp.domain.WhatsAppResult
import com.onlyreminder.app.features.whatsapp.presentation.WhatsAppViewModel

@Composable
fun WhatsAppScreen(
    navController: NavController,
    viewModel: WhatsAppViewModel = hiltViewModel(),
) {
    val currentItem by viewModel.currentItem.collectAsState()
    val queue by viewModel.queue.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val sendMode by viewModel.sendMode.collectAsState()
    val context = LocalContext.current

    val whatsappManager = remember { WhatsAppManualManager() }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = if (sendMode == "WA_API") stringResource(R.string.whatsapp_api_mode) else stringResource(
                    id = R.string.whatsapp_manual_send
                ),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (queue.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_pending_messages),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (currentIndex >= queue.size) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.all_messages_processed),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val rId = viewModel.runId
                        if (rId != null) {
                            navController.navigate(Route.Report(rId)) {
                                popUpTo(Route.WhatsApp(rId)) { inclusive = true }
                            }
                        } else {
                            navController.navigateUp()
                        }
                    }) {
                        Text(stringResource(id = R.string.back_to_review))
                    }
                }
            } else {
                val item = currentItem
                if (item != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / queue.size },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = stringResource(
                                id = R.string.sending_status,
                                currentIndex + 1,
                                queue.size
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = item.contact?.displayName
                                        ?: stringResource(id = R.string.none),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = item.contact?.phone
                                        ?: stringResource(id = R.string.not_set),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        OutlinedTextField(
                            value = item.item.generatedMessagePreview,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.message_preview)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.skipCurrent() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(id = R.string.skip))
                            }

                            if (sendMode == "WA_API") {
                                Button(
                                    onClick = { viewModel.sendCurrentViaApi() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.send_now_api))
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val success = whatsappManager.openWhatsAppChat(
                                            context,
                                            item.contact?.phone ?: "",
                                            item.item.generatedMessagePreview
                                        )
                                        if (success is WhatsAppResult.Success) {
                                            viewModel.markCurrentAsManualOpened()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(id = R.string.open_whatsapp))
                                }
                            }

                        }

                        TextButton(
                            onClick = {
                                whatsappManager.copyToClipboard(
                                    context,
                                    item.item.generatedMessagePreview
                                )
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(stringResource(id = R.string.copy_to_clipboard))
                        }
                    }
                }
            }
        }
    }
}
