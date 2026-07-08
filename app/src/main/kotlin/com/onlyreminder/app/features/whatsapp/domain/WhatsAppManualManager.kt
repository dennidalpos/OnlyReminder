package com.onlyreminder.app.features.whatsapp.domain

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhatsAppManualManager @Inject constructor() {

    /**
     * Prepares the intent to open WhatsApp. 
     * The actual execution is delegated to the caller to keep domain clean of Context if possible,
     * but here we return a boolean status after trying to start activity.
     */
    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String): WhatsAppResult {
        return try {
            val packageManager = context.packageManager
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=$encodedMessage"
            val uri = url.toUri()

            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp")
            }

            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
                WhatsAppResult.Success
            } else {
                // Try WhatsApp Business
                intent.setPackage("com.whatsapp.w4b")
                if (intent.resolveActivity(packageManager) != null) {
                    context.startActivity(intent)
                    WhatsAppResult.Success
                } else {
                    WhatsAppResult.ErrorWhatsAppNotInstalled
                }
            }
        } catch (e: Exception) {
            WhatsAppResult.Failure(e.message ?: "Unknown error")
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("WhatsApp Message", text)
        clipboard.setPrimaryClip(clip)
    }
}

sealed class WhatsAppResult {
    data object Success : WhatsAppResult()
    data object ErrorWhatsAppNotInstalled : WhatsAppResult()
    data class Failure(val message: String) : WhatsAppResult()
}
