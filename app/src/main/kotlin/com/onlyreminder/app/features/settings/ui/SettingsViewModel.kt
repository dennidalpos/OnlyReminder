package com.onlyreminder.app.features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.settings.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val language =
        settingsDataStore.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "en")
    val sendMode = settingsDataStore.sendMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        "REMINDER_ONLY"
    )
    val defaultCountryCode = settingsDataStore.defaultCountryCode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        "+39"
    )
    val birthdayNotificationTime = settingsDataStore.birthdayNotificationTime.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        "09:00"
    )
    val backupFolderUri = settingsDataStore.backupFolderUri.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        null
    )

    fun setLanguage(lang: String) {
        viewModelScope.launch { settingsDataStore.updateLanguage(lang) }
    }

    fun setSendMode(mode: String) {
        viewModelScope.launch { settingsDataStore.setSendMode(mode) }
    }

    fun setDefaultCountryCode(code: String) {
        viewModelScope.launch { settingsDataStore.updateDefaultCountryCode(code) }
    }

    fun setBirthdayNotificationTime(time: String) {
        viewModelScope.launch { settingsDataStore.updateBirthdayNotificationTime(time) }
    }

    fun setBackupFolder(uri: String) {
        viewModelScope.launch { settingsDataStore.setBackupFolderUri(uri) }
    }
}
