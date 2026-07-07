package com.onlyreminder.app.features.templates.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.components.ConfirmationDialog
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.templates.presentation.TemplateEditViewModel

@Composable
fun TemplateEditScreen(
    navController: NavController,
    viewModel: TemplateEditViewModel = hiltViewModel()
) {
    val template by viewModel.template.collectAsState()
    val previewText by viewModel.previewText.collectAsState()
    val isPromotional by viewModel.isPromotional.collectAsState()

    var showBackDialog by remember { mutableStateOf(false) }

    val onBack = {
        if (viewModel.hasChanges) {
            showBackDialog = true
        } else {
            navController.navigateUp()
        }
        Unit
    }

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = if (template?.id == 0L) {
                    stringResource(id = R.string.new_template)
                } else {
                    stringResource(id = R.string.edit_template)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveTemplate { navController.navigateUp() } }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(id = R.string.save)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        template?.let { temp ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = temp.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text(stringResource(id = R.string.template_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var channelExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { channelExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(id = R.string.channel_label, temp.channel))
                        }
                        DropdownMenu(
                            expanded = channelExpanded,
                            onDismissRequest = { channelExpanded = false }) {
                            listOf(
                                "WHATSAPP_MANUAL",
                                "WHATSAPP_BUSINESS_API",
                                "REMINDER"
                            ).forEach { channel ->
                                DropdownMenuItem(
                                    text = { Text(channel) },
                                    onClick = {
                                        viewModel.updateChannel(channel); channelExpanded = false
                                    })
                            }
                        }
                    }

                    var langExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(0.5f)) {
                        OutlinedButton(
                            onClick = { langExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(id = R.string.language_label, temp.language))
                        }
                        DropdownMenu(
                            expanded = langExpanded,
                            onDismissRequest = { langExpanded = false }) {
                            listOf("EN", "IT").forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang) },
                                    onClick = {
                                        viewModel.updateLanguage(lang); langExpanded = false
                                    })
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = temp.isDefault,
                        onCheckedChange = { viewModel.updateIsDefault(it) })
                    Text(stringResource(id = R.string.set_default_label))
                }

                OutlinedTextField(
                    value = temp.body,
                    onValueChange = { viewModel.updateBody(it) },
                    label = { Text(stringResource(id = R.string.message_body_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    supportingText = {
                        Text(stringResource(id = R.string.available_placeholders))
                    }
                )

                if (isPromotional) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(id = R.string.promotional_warning),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Text(
                    stringResource(id = R.string.preview_sample),
                    style = MaterialTheme.typography.titleSmall
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = previewText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (showBackDialog) {
            ConfirmationDialog(
                title = stringResource(id = R.string.unsaved_changes_title),
                message = stringResource(id = R.string.unsaved_changes_msg),
                onConfirm = {
                    showBackDialog = false
                    navController.navigateUp()
                },
                onDismiss = { showBackDialog = false }
            )
        }
    }
}
