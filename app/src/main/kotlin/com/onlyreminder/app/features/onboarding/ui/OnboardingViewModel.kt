package com.onlyreminder.app.features.onboarding.ui

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.core.security.SecurePrefs
import com.onlyreminder.app.data.settings.SettingsDataStore
import com.onlyreminder.app.domain.security.SecurityRepository
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
    @param:SecurePrefs private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    val language =
        settingsDataStore.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "en")
    val sendMode = settingsDataStore.sendMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        "REMINDER_ONLY"
    )
    val isPinSet = MutableStateFlow(securityRepository.isPinSet())

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

    fun setSendMode(mode: String) {
        viewModelScope.launch {
            settingsDataStore.setSendMode(mode)
        }
    }

    fun setPin(pin: String) {
        securityRepository.setPin(pin)
        isPinSet.value = true
    }

    fun setBackupFolder(uri: String) {
        viewModelScope.launch {
            settingsDataStore.setBackupFolderUri(uri)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsDataStore.setOnboardingCompleted(true)
        }
    }

    fun updateWhatsAppConfig(phoneId: String, token: String, template: String) {
        sharedPreferences.edit {
            putString("wa_phone_id", phoneId)
            putString("wa_token", token)
            putString("wa_template", template)
        }
    }
}
