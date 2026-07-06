package com.onlyreminder.app.core.security

object SecurityUtils {
    /**
     * Masks a sensitive string like a token or a password.
     * Example: "mysecrettoken" -> "mys********ken"
     */
    fun maskSecret(secret: String?): String {
        if (secret.isNullOrBlank()) return ""
        if (secret.length <= 6) return "******"
        return secret.take(3) + "*".repeat(secret.length - 6) + secret.takeLast(3)
    }

    /**
     * Formats a phone number for logs/display without showing full digits.
     * Example: "+391234567890" -> "+39*******890"
     */
    fun maskPhoneNumber(phoneNumber: String?): String {
        if (phoneNumber.isNullOrBlank()) return ""
        if (phoneNumber.length <= 6) return "****"
        return phoneNumber.take(3) + "*".repeat(phoneNumber.length - 6) + phoneNumber.takeLast(3)
    }
}
