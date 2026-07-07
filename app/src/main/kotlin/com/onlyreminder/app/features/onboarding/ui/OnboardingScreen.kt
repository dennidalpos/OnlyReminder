package com.onlyreminder.app.features.onboarding.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val language by viewModel.language.collectAsState()
    val sendMode by viewModel.sendMode.collectAsState()
    val isPinSet by viewModel.isPinSet.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(R.string.onboarding_title),
                navigationIcon = {
                    if (currentStep > 0) {
                        IconButton(onClick = { viewModel.prevStep() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentStep) {
                0 -> WelcomeStep()
                1 -> LanguageStep(language, onLanguageSelected = { viewModel.setLanguage(it) })
                2 -> SecurityStep(isPinSet, onPinSet = { viewModel.setPin(it) })
                3 -> SendModeStep(sendMode, onModeSelected = { viewModel.setSendMode(it) })
                4 -> {
                    if (sendMode == "WA_API") {
                        WhatsAppApiStep(onConfigSave = { phoneId, token, template ->
                            viewModel.updateWhatsAppConfig(phoneId, token, template)
                        })
                    } else {
                        // Skip if not API
                        LaunchedEffect(Unit) { viewModel.nextStep() }
                    }
                }

                5 -> BackupStep(onFolderSelected = { viewModel.setBackupFolder(it) })
                6 -> PrivacyStep()
                else -> {
                    viewModel.completeOnboarding()
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.nextStep() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (currentStep < 6) stringResource(R.string.next) else stringResource(R.string.finish))
            }
        }
    }
}

@Composable
fun WelcomeStep() {
    Icon(
        imageVector = Icons.Default.Cake,
        contentDescription = null,
        modifier = Modifier.size(100.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        stringResource(R.string.welcome_message),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        stringResource(R.string.welcome_description),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
fun LanguageStep(currentLanguage: String, onLanguageSelected: (String) -> Unit) {
    Text(
        stringResource(R.string.language_selection),
        style = MaterialTheme.typography.headlineSmall
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = currentLanguage == "en",
            onClick = { onLanguageSelected("en") },
            label = { Text("English") }
        )
        FilterChip(
            selected = currentLanguage == "it",
            onClick = { onLanguageSelected("it") },
            label = { Text("Italiano") }
        )
    }
}

@Composable
fun SecurityStep(isPinSet: Boolean, onPinSet: (String) -> Unit) {
    Text(stringResource(R.string.security_setup), style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    if (!isPinSet) {
        var pin by remember { mutableStateOf("") }
        TextField(
            value = pin,
            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 4) pin = it },
            label = { Text("Set 4-digit PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = { if (pin.length == 4) onPinSet(pin) }) {
            Text("Save PIN")
        }
    } else {
        Text("PIN is set!")
    }
}

@Composable
fun SendModeStep(currentMode: String, onModeSelected: (String) -> Unit) {
    Text(
        stringResource(R.string.send_mode_selection),
        style = MaterialTheme.typography.headlineSmall
    )
    Spacer(modifier = Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RadioButtonOption(
            label = stringResource(R.string.send_mode_reminder_only),
            selected = currentMode == "REMINDER_ONLY",
            onClick = { onModeSelected("REMINDER_ONLY") }
        )
        RadioButtonOption(
            label = stringResource(R.string.send_mode_manual_wa),
            selected = currentMode == "MANUAL_WA",
            onClick = { onModeSelected("MANUAL_WA") }
        )
        RadioButtonOption(
            label = stringResource(R.string.send_mode_wa_api),
            selected = currentMode == "WA_API",
            onClick = { onModeSelected("WA_API") }
        )
    }
}

@Composable
fun WhatsAppApiStep(onConfigSave: (String, String, String) -> Unit) {
    var phoneId by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var template by remember { mutableStateOf("birthday_template") }

    Text(
        stringResource(R.string.whatsapp_api_setup),
        style = MaterialTheme.typography.headlineSmall
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(stringResource(R.string.whatsapp_api_setup_desc))

    Spacer(modifier = Modifier.height(16.dp))

    TextField(
        value = phoneId,
        onValueChange = { phoneId = it },
        label = { Text(stringResource(R.string.phone_number_id)) },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = token,
        onValueChange = { token = it },
        label = { Text(stringResource(R.string.access_token)) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = template,
        onValueChange = { template = it },
        label = { Text(stringResource(R.string.approved_template_name)) },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = { onConfigSave(phoneId, token, template) }) {
        Text(stringResource(R.string.save_configuration))
    }
}

@Composable
fun BackupStep(onFolderSelected: (String) -> Unit) {
    Text(stringResource(R.string.backup_setup), style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    var folderName by remember { mutableStateOf("No folder selected") }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let {
                folderName = it.path ?: it.toString()
                onFolderSelected(it.toString())
            }
        }
    Text(folderName)
    Button(onClick = { launcher.launch(null) }) {
        Text("Select Backup Folder")
    }
}

@Composable
fun PrivacyStep() {
    Text(stringResource(R.string.privacy_notice), style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    Text(stringResource(R.string.privacy_text), style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun RadioButtonOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}
