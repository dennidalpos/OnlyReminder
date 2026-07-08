package com.onlyreminder.app.features.security.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.components.DestructiveConfirmationDialog
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@Composable
fun SecurityScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val isPinSet by viewModel.isPinSet.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val autoLockTimeout by viewModel.autoLockTimeout.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var showWipeDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.security_title),
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ListItem(
                headlineContent = { Text(stringResource(id = R.string.app_lock_enable)) },
                supportingContent = { Text(if (isPinSet) stringResource(id = R.string.enabled) else stringResource(id = R.string.disabled)) },
                trailingContent = {
                    Switch(
                        checked = isPinSet,
                        onCheckedChange = {
                            if (it) showPinDialog = true else viewModel.clearPin()
                        }
                    )
                }
            )

            if (isPinSet) {
                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.biometric_enable)) },
                    trailingContent = {
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { viewModel.setBiometricEnabled(it) }
                        )
                    }
                )

                Text(text = stringResource(id = R.string.auto_lock_timeout), style = MaterialTheme.typography.labelLarge)
                val timeouts = listOf(0, 1, 5, 15)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeouts.forEach { minutes ->
                        FilterChip(
                            selected = autoLockTimeout == minutes,
                            onClick = { viewModel.setAutoLockTimeout(minutes) },
                            label = { Text(if (minutes == 0) stringResource(id = R.string.immediate) else stringResource(id = R.string.minutes_format, minutes)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1.0f))

            Button(
                onClick = { showWipeDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.wipe_security_data))
            }
        }

        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text(stringResource(id = R.string.set_pin)) },
                text = {
                    TextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 4) pinInput = it
                        },
                        label = { Text(stringResource(id = R.string.enter_pin_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (pinInput.length == 4) {
                                viewModel.setPin(pinInput)
                                showPinDialog = false
                                pinInput = ""
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }

        if (showWipeDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(id = R.string.wipe_security_data_confirm_title),
                message = stringResource(id = R.string.wipe_security_data_confirm_msg),
                confirmText = stringResource(id = R.string.wipe),
                onConfirm = {
                    viewModel.wipeData()
                    showWipeDialog = false
                },
                onDismiss = { showWipeDialog = false }
            )
        }
    }
}
