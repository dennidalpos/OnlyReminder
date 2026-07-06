package com.onlyreminder.app.features.whatsapp.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhatsAppManualManager @Inject constructor() {

    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String): Boolean {
        return try {
            val packageManager = context.packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=" + URLEncoder.encode(
                message,
                "UTF-8"
            )

            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)

            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i)
                true
            } else {
                // Try WhatsApp Business
                i.setPackage("com.whatsapp.w4b")
                if (i.resolveActivity(packageManager) != null) {
                    context.startActivity(i)
                    true
                } else {
                    Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("OnlyReminder Message", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Message copied to clipboard.", Toast.LENGTH_SHORT).show()
    }
}
