package com.onlyreminder.app.features.whatsapp.presentation

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.core.security.SecurePrefs
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.features.whatsapp.data.WhatsAppApiService
import com.onlyreminder.app.features.whatsapp.data.WhatsAppLanguage
import com.onlyreminder.app.features.whatsapp.data.WhatsAppMessageRequest
import com.onlyreminder.app.features.whatsapp.data.WhatsAppTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltViewModel
class WhatsAppApiViewModel @Inject constructor(
    @SecurePrefs private val sharedPreferences: SharedPreferences,
    private val mainRepository: MainRepositoryImpl
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

        sharedPreferences.edit()
            .putString("wa_phone_id", phoneId)
            .putString("wa_token", token)
            .putString("wa_template", template)
            .apply()
    }

    private val apiService: WhatsAppApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        Retrofit.Builder()
            .baseUrl("https://graph.facebook.com/v17.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(WhatsAppApiService::class.java)
    }

    fun sendBatch(contacts: List<com.onlyreminder.app.data.database.entities.ContactEntity>) {
        viewModelScope.launch {
            _isSending.value = true
            for (contact in contacts) {
                if (!_isSending.value) break

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
                        // Success
                    } else {
                        // Error
                    }
                } catch (e: Exception) {
                    // Log error
                }

                delay(3000) // 3 seconds delay
            }
            _isSending.value = false
        }
    }

    fun stopSending() {
        _isSending.value = false
    }
}
