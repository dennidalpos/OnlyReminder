package com.onlyreminder.app.features.backup.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.backup.presentation.BackupViewModel

@Composable
fun BackupScreen(
    navController: NavController,
    viewModel: BackupViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val status by viewModel.status.collectAsState()

    var password by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(value = false) }
    var isRestoreMode by remember { mutableStateOf(value = false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            isRestoreMode = true
            showPasswordDialog = true
        }
    }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = "Backup & Restore",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Backup,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                "Keep your contacts safe by creating encrypted backups. You will need a password to restore them.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Button(
                onClick = {
                    isRestoreMode = false
                    showPasswordDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Backup, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Backup Now")
            }

            OutlinedButton(
                onClick = { restoreLauncher.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Restore, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore from Backup")
            }

            if (isLoading) {
                CircularProgressIndicator()
            }

            status?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Button(onClick = { viewModel.clearStatus() }) {
                    Text("OK")
                }
            }
        }
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text(if (isRestoreMode) "Enter Password to Restore" else "Set Backup Password") },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    singleLine = true,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isRestoreMode) {
                            selectedUri?.let { viewModel.restoreBackup(password, it) }
                        } else {
                            viewModel.createBackup(password)
                        }
                        showPasswordDialog = false
                        password = ""
                    },
                ) {
                    Text("Proceed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}
