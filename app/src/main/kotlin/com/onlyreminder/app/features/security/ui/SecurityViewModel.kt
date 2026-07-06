package com.onlyreminder.app.features.security.ui

import androidx.lifecycle.ViewModel
import com.onlyreminder.app.domain.security.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val repository: SecurityRepository
) : ViewModel() {

    private val _isPinSet = MutableStateFlow(repository.isPinSet())
    val isPinSet: StateFlow<Boolean> = _isPinSet.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(repository.isBiometricEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _autoLockTimeout = MutableStateFlow(repository.getAutoLockTimeout())
    val autoLockTimeout: StateFlow<Int> = _autoLockTimeout.asStateFlow()

    fun setPin(pin: String) {
        repository.setPin(pin)
        _isPinSet.value = true
    }

    fun clearPin() {
        repository.clearPin()
        _isPinSet.value = false
    }

    fun setBiometricEnabled(enabled: Boolean) {
        repository.setBiometricEnabled(enabled)
        _isBiometricEnabled.value = enabled
    }

    fun setAutoLockTimeout(minutes: Int) {
        repository.setAutoLockTimeout(minutes)
        _autoLockTimeout.value = minutes
    }

    fun wipeData() {
        repository.wipeSecurityData()
        _isPinSet.value = false
        _isBiometricEnabled.value = false
        _autoLockTimeout.value = 5
    }
}
