package com.onlyreminder.app.features.onboarding.ui

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
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
                                contentDescription = stringResource(id = R.string.back)
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
                3 -> PrivacyStep()
                else -> {
                    viewModel.completeOnboarding()
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentStep == 2 && !isPinSet) {
                    OutlinedButton(
                        onClick = { viewModel.nextStep() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.skip))
                    }
                }

                Button(
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (currentStep < 3) stringResource(R.string.next) else stringResource(R.string.finish))
                }
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
            label = { Text(stringResource(id = R.string.english)) }
        )
        FilterChip(
            selected = currentLanguage == "it",
            onClick = { onLanguageSelected("it") },
            label = { Text(stringResource(id = R.string.italian)) }
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
            label = { Text(stringResource(id = R.string.set_pin_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { if (pin.length == 4) onPinSet(pin) },
            enabled = pin.length == 4,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.save_pin))
        }
    } else {
        Text(
            stringResource(id = R.string.pin_is_set),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun PrivacyStep() {
    Text(stringResource(R.string.privacy_notice), style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        stringResource(R.string.privacy_text),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}
