package com.onlyreminder.app.features.backup.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.features.backup.domain.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    fun createBackup(password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val file = backupManager.createBackup(password)
            if (file != null) {
                _status.value = "Backup created successfully: ${file.name}"
            } else {
                _status.value = "Failed to create backup."
            }
            _isLoading.value = false
        }
    }

    fun restoreBackup(password: String, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = backupManager.restoreBackup(password, uri)
            if (success) {
                _status.value = "Restore successful. Please restart the app."
            } else {
                _status.value = "Failed to restore backup. Check password or file."
            }
            _isLoading.value = false
        }
    }

    fun clearStatus() {
        _status.value = null
    }
}
