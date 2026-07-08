package com.onlyreminder.app.features.whatsapp.data

import android.content.SharedPreferences
import com.onlyreminder.app.core.security.SecurePrefs
import com.onlyreminder.app.data.database.entities.ContactEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhatsAppRepository @Inject constructor(
    private val apiService: WhatsAppApiService,
    @param:SecurePrefs private val sharedPreferences: SharedPreferences,
) {
    fun getPhoneId() = sharedPreferences.getString("wa_phone_id", "") ?: ""
    fun getToken() = sharedPreferences.getString("wa_token", "") ?: ""
    fun getTemplateName() =
        sharedPreferences.getString("wa_template", "birthday_template") ?: "birthday_template"

    suspend fun sendMessage(contact: ContactEntity, overrideTemplateName: String? = null): Boolean {
        val phoneId = getPhoneId()
        val token = getToken()
        val templateName = overrideTemplateName ?: getTemplateName()

        if (phoneId.isBlank() || token.isBlank()) return false

        val request = WhatsAppMessageRequest(
            to = contact.phone,
            template = WhatsAppTemplate(
                name = templateName,
                language = WhatsAppLanguage(code = "it"),
                components = listOf(
                    WhatsAppTemplateComponent(
                        type = "body",
                        parameters = listOf(
                            WhatsAppTemplateParameter(text = contact.firstName)
                        )
                    )
                )
            )
        )

        return try {
            val response = apiService.sendMessage(phoneId, "Bearer $token", request)
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun testConnection(phoneId: String, token: String): Boolean {
        val request = WhatsAppMessageRequest(
            to = "5511999999999",
            template = WhatsAppTemplate(
                name = "hello_world",
                language = WhatsAppLanguage(code = "en_US")
            )
        )
        return try {
            val response = apiService.sendMessage(phoneId, "Bearer $token", request)
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    fun updateConfig(phoneId: String, token: String, template: String) {
        sharedPreferences.edit().apply {
            putString("wa_phone_id", phoneId)
            putString("wa_token", token)
            putString("wa_template", template)
            apply()
        }
    }
}
