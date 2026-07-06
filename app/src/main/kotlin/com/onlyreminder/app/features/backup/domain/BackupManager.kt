package com.onlyreminder.app.features.backup.domain

import android.content.Context
import android.net.Uri
import com.onlyreminder.app.data.database.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase,
) {

    suspend fun createBackup(password: String): File? =
        withContext(Dispatchers.IO) {
            try {
                // Ensure data is consistent - this is a bit hacky, maybe use checkpoint
                // db.mainDao().getAllTemplates().first() 

                val dbFile = context.getDatabasePath(AppDatabase.DB_NAME)
                val dbBytes = dbFile.readBytes()

                val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
                val iv = ByteArray(12).apply { SecureRandom().nextBytes(this) }

                val spec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val secretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

                val encryptedBytes = cipher.doFinal(dbBytes)

                val outputBytes = ByteArray(1 + 16 + 12 + encryptedBytes.size)
                outputBytes[0] = 1
                System.arraycopy(salt, 0, outputBytes, 1, 16)
                System.arraycopy(iv, 0, outputBytes, 17, 12)
                System.arraycopy(encryptedBytes, 0, outputBytes, 29, encryptedBytes.size)

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")
                val fileName =
                    "OnlyReminder_Backup_${LocalDateTime.now().format(formatter)}.orbackup"

                val backupFile = File(context.filesDir, "backups/$fileName")
                backupFile.parentFile?.mkdirs()
                backupFile.writeBytes(outputBytes)

                applyRetention()

                backupFile
            } catch (_: Exception) {
                null
            }
        }

    private fun applyRetention() {
        val directory = File(context.filesDir, "backups")
        val files = directory.listFiles()?.asSequence()
            ?.filter { it.extension == "orbackup" }
            ?.sortedBy { it.lastModified() }
            ?.toList()
        if (files != null && files.size > 10) {
            files.take(files.size - 10).forEach { it.delete() }
        }
    }

    suspend fun exportContactsToCsv(
        uri: Uri,
        contacts: List<com.onlyreminder.app.data.database.entities.ContactEntity>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = outputStream.bufferedWriter()
                writer.write("FirstName,LastName,DisplayName,Phone,Email,Company,Birthday,Notes\n")
                contacts.forEach { c ->
                    writer.write("${c.firstName},${c.lastName},${c.displayName},${c.phone},${c.email},${c.company},${c.birthday},${c.notes}\n")
                }
                writer.flush()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun restoreBackup(password: String, backupUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    context.contentResolver.openInputStream(backupUri) ?: return@withContext false
                val backupBytes = inputStream.readBytes()
                inputStream.close()

                if (backupBytes[0].toInt() != 1) return@withContext false // Version mismatch

                val salt = backupBytes.copyOfRange(1, 17)
                val iv = backupBytes.copyOfRange(17, 29)
                val encryptedBytes = backupBytes.copyOfRange(29, backupBytes.size)

                val spec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val secretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

                val decryptedBytes = cipher.doFinal(encryptedBytes)

                db.close()
                val dbFile = context.getDatabasePath(AppDatabase.DB_NAME)
                dbFile.writeBytes(decryptedBytes)

                true
            } catch (e: Exception) {
                false
            }
        }
}
