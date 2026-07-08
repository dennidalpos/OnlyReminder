package com.onlyreminder.app.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.domain.model.TaskStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: MainRepositoryImpl

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("taskId", -1L)
        if (taskId == -1L) return

        val notificationHelper = NotificationHelper(context)

        CoroutineScope(Dispatchers.IO).launch {
            val task = repository.getTaskById(taskId)
            if ((task != null) && (task.status == TaskStatus.PENDING)) {
                notificationHelper.showTaskNotification(
                    taskId = task.id,
                    title = context.getString(com.onlyreminder.app.R.string.reminder_notification_title, task.title),
                    message = task.description.ifBlank { context.getString(com.onlyreminder.app.R.string.reminder_notification_default_message) },
                )
            }
        }
    }
}
