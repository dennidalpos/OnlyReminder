package com.onlyreminder.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.onlyreminder.app.data.database.DatabaseSeeder
import com.onlyreminder.app.features.birthday.data.BirthdayWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class OnlyReminderApp : Application(), Configuration.Provider {

    companion object {
        init {
            System.loadLibrary("sqlcipher")
        }
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var seeder: DatabaseSeeder

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (resources.getBoolean(R.bool.is_demo)) {
            CoroutineScope(Dispatchers.IO).launch {
                seeder.seedDemoData()
            }
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
            birthdayRequest,
        )

        val backupRequest =
            PeriodicWorkRequestBuilder<com.onlyreminder.app.features.backup.data.BackupWorker>(
                1,
                TimeUnit.DAYS,
            )
                .addTag("daily_backup")
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_backup",
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest,
        )
    }
}
