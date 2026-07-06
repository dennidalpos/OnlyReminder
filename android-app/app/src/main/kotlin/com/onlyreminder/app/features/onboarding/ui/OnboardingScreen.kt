package com.onlyreminder.app.features.onboarding.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.navigation.Screen
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
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
                0 -> LanguageStep(language, onLanguageSelected = { viewModel.setLanguage(it) })
                1 -> SecurityStep(isPinSet, onPinSet = { viewModel.setPin(it) })
                2 -> SendModeStep(sendMode, onModeSelected = { viewModel.setSendMode(it) })
                3 -> {
                    if (sendMode == "WA_API") {
                        WhatsAppApiStep()
                    } else {
                        // Skip if not API
                        LaunchedEffect(Unit) { viewModel.nextStep() }
                    }
                }

                4 -> BackupStep(onFolderSelected = { viewModel.setBackupFolder(it) })
                5 -> PrivacyStep()
                else -> {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.nextStep() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (currentStep < 5) stringResource(R.string.next) else stringResource(R.string.finish))
            }
        }
    }
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
fun WhatsAppApiStep() {
    Text("WhatsApp API Setup", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Enter your WhatsApp Business credentials. These will be stored securely.")
    // Placeholder fields for now
    TextField(value = "", onValueChange = {}, label = { Text("Business Account ID") })
    TextField(value = "", onValueChange = {}, label = { Text("Phone Number ID") })
    TextField(value = "", onValueChange = {}, label = { Text("Access Token") })
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
