package com.onlyreminder.app.features.backup.data

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.onlyreminder.app.core.security.SecurePrefs
import com.onlyreminder.app.features.backup.domain.BackupManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupManager: BackupManager,
    @param:SecurePrefs private val sharedPreferences: SharedPreferences,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val password = sharedPreferences.getString("backup_password", null)
            ?: return Result.failure()

        val file = backupManager.createBackup(password)

        return if (file != null) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
