package com.onlyreminder.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val dataStore = context.dataStore

    object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val DEFAULT_COUNTRY_CODE = stringPreferencesKey("default_country_code")
        val BIRTHDAY_NOTIFICATION_TIME = stringPreferencesKey("birthday_notification_time")
        val BIRTHDAY_API_BATCH_DELAY = intPreferencesKey("birthday_api_batch_delay")
        val BACKUP_RETENTION_COUNT = intPreferencesKey("backup_retention_count")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val SEND_MODE = stringPreferencesKey("send_mode")
        val BACKUP_FOLDER_URI = stringPreferencesKey("backup_folder_uri")
        val NORMALIZE_PHONE = booleanPreferencesKey("normalize_phone")
        val LAST_BACKUP_TIME = stringPreferencesKey("last_backup_time")
        val BIRTHDAY_TEMPLATE_ID = intPreferencesKey("birthday_template_id")

        // WhatsApp API Settings (Encrypted by EncryptedPrefs usually, but for UI state we might use DataStore for non-sensitive ones)
        val WA_BUSINESS_ACCOUNT_ID = stringPreferencesKey("wa_business_account_id")
        val WA_PHONE_NUMBER_ID = stringPreferencesKey("wa_phone_number_id")
        // Access Token should ONLY be in EncryptedPrefs. I'll use repository for that.
    }

    val language: Flow<String> = dataStore.data.map { it[Keys.LANGUAGE] ?: "en" }
    val defaultCountryCode: Flow<String> =
        dataStore.data.map { it[Keys.DEFAULT_COUNTRY_CODE] ?: "+39" }
    val birthdayNotificationTime: Flow<String> =
        dataStore.data.map { it[Keys.BIRTHDAY_NOTIFICATION_TIME] ?: "09:00" }
    val birthdayApiBatchDelay: Flow<Int> =
        dataStore.data.map { it[Keys.BIRTHDAY_API_BATCH_DELAY] ?: 3 }
    val backupRetentionCount: Flow<Int> =
        dataStore.data.map { it[Keys.BACKUP_RETENTION_COUNT] ?: 10 }
    val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map { it[Keys.ONBOARDING_COMPLETED] ?: false }
    val sendMode: Flow<String> = dataStore.data.map { it[Keys.SEND_MODE] ?: "REMINDER_ONLY" }
    val backupFolderUri: Flow<String?> = dataStore.data.map { it[Keys.BACKUP_FOLDER_URI] }
    val normalizePhone: Flow<Boolean> = dataStore.data.map { it[Keys.NORMALIZE_PHONE] ?: true }
    val lastBackupTime: Flow<String?> = dataStore.data.map { it[Keys.LAST_BACKUP_TIME] }
    val birthdayTemplateId: Flow<Long?> = dataStore.data.map { it[Keys.BIRTHDAY_TEMPLATE_ID]?.toLong() }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setSendMode(mode: String) {
        dataStore.edit { it[Keys.SEND_MODE] = mode }
    }

    suspend fun setBackupFolderUri(uri: String) {
        dataStore.edit { it[Keys.BACKUP_FOLDER_URI] = uri }
    }

    suspend fun setNormalizePhone(normalize: Boolean) {
        dataStore.edit { it[Keys.NORMALIZE_PHONE] = normalize }
    }

    suspend fun updateLanguage(language: String) {
        dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun updateDefaultCountryCode(code: String) {
        dataStore.edit { it[Keys.DEFAULT_COUNTRY_CODE] = code }
    }

    suspend fun updateBirthdayNotificationTime(time: String) {
        dataStore.edit { it[Keys.BIRTHDAY_NOTIFICATION_TIME] = time }
    }

    suspend fun updateBirthdayApiBatchDelay(seconds: Int) {
        dataStore.edit { it[Keys.BIRTHDAY_API_BATCH_DELAY] = seconds }
    }

    suspend fun updateBackupRetentionCount(count: Int) {
        dataStore.edit { it[Keys.BACKUP_RETENTION_COUNT] = count }
    }

    suspend fun updateLastBackupTime(time: String) {
        dataStore.edit { it[Keys.LAST_BACKUP_TIME] = time }
    }

    suspend fun setBirthdayTemplateId(id: Long?) {
        dataStore.edit {
            if (id == null) it.remove(Keys.BIRTHDAY_TEMPLATE_ID)
            else it[Keys.BIRTHDAY_TEMPLATE_ID] = id.toInt()
        }
    }
}
