package com.onlyreminder.app.features.whatsapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.features.whatsapp.data.WhatsAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhatsAppApiViewModel @Inject constructor(
    private val repository: WhatsAppRepository
) : ViewModel() {

    private val _phoneNumberId = MutableStateFlow(repository.getPhoneId())
    val phoneNumberId: StateFlow<String> = _phoneNumberId.asStateFlow()

    private val _accessToken = MutableStateFlow(repository.getToken())
    val accessToken: StateFlow<String> = _accessToken.asStateFlow()

    private val _templateName = MutableStateFlow(repository.getTemplateName())
    val templateName: StateFlow<String> = _templateName.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun updateConfig(phoneId: String, token: String, template: String) {
        _phoneNumberId.value = phoneId
        _accessToken.value = token
        _templateName.value = template
        repository.updateConfig(phoneId, token, template)
    }

    fun sendMessage(contact: ContactEntity) {
        viewModelScope.launch {
            _isSending.value = true
            repository.sendMessage(contact)
            _isSending.value = false
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _isSending.value = true
            repository.testConnection(_phoneNumberId.value, _accessToken.value)
            _isSending.value = false
        }
    }

    fun stopSending() {
        _isSending.value = false
    }
}
