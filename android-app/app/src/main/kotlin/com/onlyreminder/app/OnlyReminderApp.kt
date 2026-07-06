package com.onlyreminder.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.onlyreminder.app.features.birthday.data.BirthdayWorker
import dagger.hilt.android.HiltAndroidApp
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class OnlyReminderApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        try {
            SQLiteDatabase.loadLibs(this)
        } catch (e: Exception) {
            // Log error or handle
        }
        scheduleBirthdayWorker()
    }

    private fun scheduleBirthdayWorker() {
        val birthdayRequest = PeriodicWorkRequestBuilder<BirthdayWorker>(1, TimeUnit.DAYS)
            .addTag("birthday_scan")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "birthday_scan",
            ExistingPeriodicWorkPolicy.KEEP,
            birthdayRequest
        )

        val backupRequest =
            PeriodicWorkRequestBuilder<com.onlyreminder.app.features.backup.data.BackupWorker>(
                1,
                TimeUnit.DAYS
            )
                .addTag("daily_backup")
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_backup",
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )
    }
}
