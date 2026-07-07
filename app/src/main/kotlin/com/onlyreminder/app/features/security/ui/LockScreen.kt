package com.onlyreminder.app.features.security.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.onlyreminder.app.R
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
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.app_locked),
            style = MaterialTheme.typography.headlineMedium
        )
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
            label = { Text(stringResource(id = R.string.enter_pin)) },
            isError = error,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation()
        )
        if (error) {
            Text(
                text = stringResource(id = R.string.incorrect_pin),
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
                Text(stringResource(id = R.string.use_biometrics))
            }
        }
    }
}
