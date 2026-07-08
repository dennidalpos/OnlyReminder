package com.onlyreminder.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.onlyreminder.app.core.notifications.TaskScheduler
import com.onlyreminder.app.data.database.DatabaseSeeder
import com.onlyreminder.app.data.settings.SettingsDataStore
import com.onlyreminder.app.R
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class OnlyReminderApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var seeder: DatabaseSeeder

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var taskScheduler: TaskScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            if (resources.getBoolean(R.bool.is_demo)) {
                seeder.seedDemoData()
            }
            
            val notificationTime = settingsDataStore.birthdayNotificationTime.first()
            taskScheduler.rescheduleBirthdayWorker(notificationTime, ExistingPeriodicWorkPolicy.KEEP)
            
            scheduleBackupWorker()
        }
    }

    private fun scheduleBackupWorker() {
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
