package com.onlyreminder.app.features.whatsapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.whatsapp.presentation.WhatsAppApiViewModel

@Composable
fun WhatsAppApiScreen(
    navController: NavController,
    viewModel: WhatsAppApiViewModel = hiltViewModel()
) {
    val phoneId by viewModel.phoneNumberId.collectAsState()
    val token by viewModel.accessToken.collectAsState()
    val template by viewModel.templateName.collectAsState()
    val isSending by viewModel.isSending.collectAsState()

    var tempPhoneId by remember { mutableStateOf(phoneId) }
    var tempToken by remember { mutableStateOf(token) }
    var tempTemplate by remember { mutableStateOf(template) }

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = "WhatsApp API Mode",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("API Configuration", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = tempPhoneId,
                onValueChange = { tempPhoneId = it },
                label = { Text("Phone Number ID") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tempToken,
                onValueChange = { tempToken = it },
                label = { Text("Access Token") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = tempTemplate,
                onValueChange = { tempTemplate = it },
                label = { Text("Approved Template Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.updateConfig(tempPhoneId, tempToken, tempTemplate) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Configuration")
            }

            HorizontalDivider()

            Text("Operations", style = MaterialTheme.typography.titleMedium)

            if (isSending) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = { viewModel.stopSending() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Stop Sending")
                }
            } else {
                Button(
                    onClick = { /* In a real scenario, we'd pass contacts from current run */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Sending Queue")
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(
                    text = "Your WhatsApp API credentials are stored only on this device and encrypted locally. You are responsible for their security and for the lawful use of the WhatsApp Business Platform.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
