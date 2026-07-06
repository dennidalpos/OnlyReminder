package com.onlyreminder.app.domain.security

import android.content.SharedPreferences
import android.util.Base64
import com.onlyreminder.app.core.security.SecurePrefs
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepository @Inject constructor(
    @SecurePrefs private val encryptedPrefs: SharedPreferences
) {
    fun setPin(pin: String) {
        val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
        val saltString = Base64.encodeToString(salt, Base64.DEFAULT)
        val hashedPin = hashPin(pin, salt)

        encryptedPrefs.edit()
            .putString(KEY_PIN_HASH, hashedPin)
            .putString(KEY_PIN_SALT, saltString)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = encryptedPrefs.getString(KEY_PIN_HASH, null) ?: return false
        val saltString = encryptedPrefs.getString(KEY_PIN_SALT, null) ?: return false
        val salt = Base64.decode(saltString, Base64.DEFAULT)

        val hashedInput = hashPin(pin, salt)
        return storedHash == hashedInput
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val digest = md.digest(pin.toByteArray())
        return Base64.encodeToString(digest, Base64.DEFAULT)
    }

    fun isPinSet(): Boolean {
        return encryptedPrefs.contains(KEY_PIN_HASH)
    }

    fun clearPin() {
        encryptedPrefs.edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setAutoLockTimeout(minutes: Int) {
        encryptedPrefs.edit().putInt(KEY_AUTO_LOCK_TIMEOUT, minutes).apply()
    }

    fun getAutoLockTimeout(): Int {
        return encryptedPrefs.getInt(KEY_AUTO_LOCK_TIMEOUT, 5) // Default 5 mins
    }

    fun setWhatsAppAccessToken(token: String) {
        encryptedPrefs.edit().putString(KEY_WA_ACCESS_TOKEN, token).apply()
    }

    fun getWhatsAppAccessToken(): String? {
        return encryptedPrefs.getString(KEY_WA_ACCESS_TOKEN, null)
    }

    /**
     * Wipes all security related data and settings.
     */
    fun wipeSecurityData() {
        encryptedPrefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_PIN_HASH = "security_pin_hash"
        private const val KEY_PIN_SALT = "security_pin_salt"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout"
        private const val KEY_WA_ACCESS_TOKEN = "wa_access_token"
    }
}
