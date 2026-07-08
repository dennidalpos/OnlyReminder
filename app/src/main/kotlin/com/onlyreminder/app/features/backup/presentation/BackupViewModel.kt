package com.onlyreminder.app.features.backup.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.UiText
import com.onlyreminder.app.features.backup.domain.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val settingsDataStore: com.onlyreminder.app.data.settings.SettingsDataStore
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _status = MutableStateFlow<UiText?>(null)
    val status: StateFlow<UiText?> = _status.asStateFlow()

    fun createBackup(password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val file = backupManager.createBackup(password)
            if (file != null) {
                settingsDataStore.updateLastBackupTime(java.time.LocalDateTime.now().toString())
                _status.value = UiText.StringResource(R.string.backup_create_success, file.name)
            } else {
                _status.value = UiText.StringResource(R.string.backup_create_failed)
            }
            _isLoading.value = false
        }
    }

    fun restoreBackup(password: String, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = backupManager.restoreBackup(password, uri)
            if (success) {
                _status.value = UiText.StringResource(R.string.backup_restore_success)
            } else {
                _status.value = UiText.StringResource(R.string.backup_restore_failed)
            }
            _isLoading.value = false
        }
    }

    fun clearStatus() {
        _status.value = null
    }
}
