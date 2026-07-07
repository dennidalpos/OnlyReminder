package com.onlyreminder.app.features.whatsapp.presentation

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.core.security.SecurePrefs
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.features.whatsapp.data.WhatsAppApiService
import com.onlyreminder.app.features.whatsapp.data.WhatsAppLanguage
import com.onlyreminder.app.features.whatsapp.data.WhatsAppMessageRequest
import com.onlyreminder.app.features.whatsapp.data.WhatsAppTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhatsAppApiViewModel @Inject constructor(
    @param:SecurePrefs private val sharedPreferences: SharedPreferences,
    private val mainRepository: MainRepositoryImpl,
    private val apiService: WhatsAppApiService
) : ViewModel() {

    private val _phoneNumberId =
        MutableStateFlow(sharedPreferences.getString("wa_phone_id", "") ?: "")
    val phoneNumberId: StateFlow<String> = _phoneNumberId.asStateFlow()

    private val _accessToken = MutableStateFlow(sharedPreferences.getString("wa_token", "") ?: "")
    val accessToken: StateFlow<String> = _accessToken.asStateFlow()

    private val _templateName = MutableStateFlow(
        sharedPreferences.getString("wa_template", "birthday_template") ?: "birthday_template"
    )
    val templateName: StateFlow<String> = _templateName.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun updateConfig(phoneId: String, token: String, template: String) {
        _phoneNumberId.value = phoneId
        _accessToken.value = token
        _templateName.value = template

        sharedPreferences.edit {
            putString("wa_phone_id", phoneId)
            putString("wa_token", token)
            putString("wa_template", template)
        }
    }

    fun sendMessage(contact: com.onlyreminder.app.data.database.entities.ContactEntity) {
        viewModelScope.launch {
            _isSending.value = true
            try {
                val request = WhatsAppMessageRequest(
                    to = contact.phone,
                    template = WhatsAppTemplate(
                        name = _templateName.value,
                        language = WhatsAppLanguage(code = "it")
                    )
                )

                val response = apiService.sendMessage(
                    _phoneNumberId.value,
                    "Bearer ${_accessToken.value}",
                    request
                )

                if (response.isSuccessful) {
                    // Success logic
                }
            } catch (e: Exception) {
                // Error logic
            } finally {
                _isSending.value = false
            }
        }
    }

    fun stopSending() {
        _isSending.value = false
    }
}
