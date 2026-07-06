package com.onlyreminder.app.features.security.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.onlyreminder.app.core.security.BiometricHelper
import com.onlyreminder.app.domain.security.SecurityRepository

@Composable
fun LockScreen(
    repository: SecurityRepository,
    onUnlocked: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (repository.isBiometricEnabled()) {
            val activity = BiometricHelper.findFragmentActivity(context)
            activity?.let {
                BiometricHelper.showBiometricPrompt(
                    activity = it,
                    onSuccess = onUnlocked,
                    onError = { /* Log or show message */ }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "App Locked", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = pinInput,
            onValueChange = {
                if (it.all { char -> char.isDigit() } && it.length <= 4) {
                    pinInput = it
                    error = false
                    if (it.length == 4) {
                        if (repository.verifyPin(it)) {
                            onUnlocked()
                        } else {
                            error = true
                            pinInput = ""
                        }
                    }
                }
            },
            label = { Text("Enter PIN") },
            isError = error,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation()
        )
        if (error) {
            Text(
                text = "Incorrect PIN",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (repository.isBiometricEnabled()) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = {
                val activity = BiometricHelper.findFragmentActivity(context)
                activity?.let {
                    BiometricHelper.showBiometricPrompt(
                        activity = it,
                        onSuccess = onUnlocked,
                        onError = { /* Log or show message */ }
                    )
                }
            }) {
                Text("Use Biometrics")
            }
        }
    }
}
