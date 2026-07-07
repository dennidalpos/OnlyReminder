package com.onlyreminder.app.features.whatsapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
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
                title = stringResource(R.string.whatsapp_api_mode),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
            Text(
                stringResource(R.string.api_configuration),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = tempPhoneId,
                onValueChange = { tempPhoneId = it },
                label = { Text(stringResource(R.string.phone_number_id)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tempToken,
                onValueChange = { tempToken = it },
                label = { Text(stringResource(R.string.access_token)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = tempTemplate,
                onValueChange = { tempTemplate = it },
                label = { Text(stringResource(R.string.approved_template_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.updateConfig(tempPhoneId, tempToken, tempTemplate) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_configuration))
            }

            HorizontalDivider()

            Text(stringResource(R.string.operations), style = MaterialTheme.typography.titleMedium)

            if (isSending) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = { viewModel.stopSending() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.stop_sending))
                }
            } else {
                Button(
                    onClick = { /* In a real scenario, we'd pass contacts from current run */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.start_sending_queue))
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(
                    text = stringResource(R.string.wa_api_security_notice),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
