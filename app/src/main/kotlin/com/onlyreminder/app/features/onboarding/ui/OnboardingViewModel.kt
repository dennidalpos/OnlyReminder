package com.onlyreminder.app.features.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.settings.SettingsDataStore
import com.onlyreminder.app.domain.security.SecurityRepository
import com.onlyreminder.app.domain.model.SendMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val securityRepository: SecurityRepository,
) : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    val language =
        settingsDataStore.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "en")

    val isPinSet = MutableStateFlow(securityRepository.isPinSet())

    val sendMode = settingsDataStore.sendMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SendMode.REMINDER_ONLY)
    val backupFolderUri = settingsDataStore.backupFolderUri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun nextStep() {
        _currentStep.value++
    }

    fun prevStep() {
        if (_currentStep.value > 0) _currentStep.value--
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsDataStore.updateLanguage(lang)
        }
    }

    fun setPin(pin: String) {
        securityRepository.setPin(pin)
        isPinSet.value = true
    }

    fun setSendMode(mode: SendMode) {
        viewModelScope.launch {
            settingsDataStore.setSendMode(mode)
        }
    }

    fun setBackupFolder(uri: String) {
        viewModelScope.launch {
            settingsDataStore.setBackupFolderUri(uri)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsDataStore.setOnboardingCompleted(completed = true)
        }
    }
}
