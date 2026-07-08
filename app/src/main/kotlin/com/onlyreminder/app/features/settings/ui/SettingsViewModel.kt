package com.onlyreminder.app.features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.settings.SettingsDataStore
import com.onlyreminder.app.domain.model.SendMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val taskScheduler: com.onlyreminder.app.core.notifications.TaskScheduler,
    mainRepository: com.onlyreminder.app.data.repository.MainRepositoryImpl,
) : ViewModel() {

    val templates = mainRepository.getAllTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val language =
        settingsDataStore.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "en")
    val sendMode = settingsDataStore.sendMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        SendMode.REMINDER_ONLY
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
    val normalizePhone = settingsDataStore.normalizePhone.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = true
    )
    val birthdayTemplateId = settingsDataStore.birthdayTemplateId.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        null
    )
    val showBackupBanner = settingsDataStore.showBackupBanner.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        true
    )

    fun setLanguage(lang: String) {
        viewModelScope.launch { settingsDataStore.updateLanguage(lang) }
    }

    fun setSendMode(mode: SendMode) {
        viewModelScope.launch { settingsDataStore.setSendMode(mode) }
    }

    fun setDefaultCountryCode(code: String) {
        viewModelScope.launch { settingsDataStore.updateDefaultCountryCode(code) }
    }

    fun setBirthdayNotificationTime(time: String) {
        viewModelScope.launch { 
            settingsDataStore.updateBirthdayNotificationTime(time)
            taskScheduler.rescheduleBirthdayWorker(time)
        }
    }

    fun setBackupFolder(uri: String) {
        viewModelScope.launch { settingsDataStore.setBackupFolderUri(uri) }
    }

    fun setNormalizePhone(normalize: Boolean) {
        viewModelScope.launch { settingsDataStore.setNormalizePhone(normalize) }
    }

    fun setBirthdayTemplateId(id: Long?) {
        viewModelScope.launch { settingsDataStore.setBirthdayTemplateId(id) }
    }

    fun setShowBackupBanner(show: Boolean) {
        viewModelScope.launch { settingsDataStore.setShowBackupBanner(show) }
    }
}
