package com.onlyreminder.app.features.whatsapp.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WhatsAppApiService {
    @POST("{phoneNumberId}/messages")
    suspend fun sendMessage(
        @Path("phoneNumberId") phoneNumberId: String,
        @Header("Authorization") authorization: String,
        @Body request: WhatsAppMessageRequest
    ): Response<WhatsAppMessageResponse>
}

data class WhatsAppMessageRequest(
    val messaging_product: String = "whatsapp",
    val to: String,
    val type: String = "template",
    val template: WhatsAppTemplate
)

data class WhatsAppTemplate(
    val name: String,
    val language: WhatsAppLanguage,
    val components: List<WhatsAppTemplateComponent>? = null
)

data class WhatsAppLanguage(
    val code: String
)

data class WhatsAppTemplateComponent(
    val type: String,
    val parameters: List<WhatsAppTemplateParameter>
)

data class WhatsAppTemplateParameter(
    val type: String = "text",
    val text: String
)

data class WhatsAppMessageResponse(
    val messaging_product: String,
    val contacts: List<Map<String, String>>,
    val messages: List<Map<String, String>>
)
