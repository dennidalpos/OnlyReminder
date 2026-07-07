package com.onlyreminder.app.features.birthday.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.onlyreminder.app.core.notifications.NotificationHelper
import com.onlyreminder.app.data.database.entities.BirthdayRunEntity
import com.onlyreminder.app.data.database.entities.BirthdayRunItemEntity
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.domain.model.BirthdayItemStatus
import com.onlyreminder.app.domain.model.BirthdayRunStatus
import com.onlyreminder.app.features.birthday.domain.BirthdayScanner
import com.onlyreminder.app.features.templates.domain.TemplateEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class BirthdayWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val birthdayScanner: BirthdayScanner,
    private val mainRepository: MainRepositoryImpl,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        val dateStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Mark previous runs as NOT_REVIEWED if they are still PENDING
        val allRuns = mainRepository.getAllBirthdayRuns().first()
        allRuns.forEach { run ->
            if ((run.date != dateStr) && (run.status == BirthdayRunStatus.PENDING)) {
                mainRepository.createBirthdayRun(run.copy(status = BirthdayRunStatus.NOT_REVIEWED))
            }
        }

        // Check if run already exists for today
        if (allRuns.any { it.date == dateStr }) {
            return Result.success()
        }

        // Requirement: Until a task is defined with users in it, the app does nothing automatically.
        val activeBirthdayTasks = mainRepository.getTasksByStatus(com.onlyreminder.app.domain.model.TaskStatus.PENDING).first()
            .filter { it.type == "BIRTHDAY" }

        if (activeBirthdayTasks.isEmpty()) {
            return Result.success()
        }

        val allContactsToday = birthdayScanner.findBirthdaysForDate(today)
        if (allContactsToday.isEmpty()) {
            return Result.success()
        }

        // Filter contacts that are actually covered by an active birthday task
        val contacts = allContactsToday.filter { contact ->
            activeBirthdayTasks.any { task ->
                task.contactId == contact.id || (task.groupId != null && contact.groupId == task.groupId)
            }
        }

        if (contacts.isEmpty()) {
            return Result.success()
        }

        // Get default template for birthday
        val templates = mainRepository.getAllTemplates().first()
        val defaultTemplate =
            templates.find { it.isDefault && it.name.contains("Birthday", ignoreCase = true) }
                ?: templates.find { it.name.contains("Birthday", ignoreCase = true) }

        val templateEngine = TemplateEngine()

        val runId = mainRepository.createBirthdayRun(
            BirthdayRunEntity(
                date = dateStr,
                status = BirthdayRunStatus.PENDING,
                totalFound = contacts.size,
                totalSelected = contacts.size,
                totalSkipped = 0,
                totalSent = 0,
                totalFailed = 0
            )
        )

        contacts.forEach { contact ->
            val message = if (defaultTemplate != null) {
                templateEngine.render(defaultTemplate.body, contact)
            } else {
                "Happy Birthday ${contact.firstName}!"
            }

            mainRepository.addRunItem(
                BirthdayRunItemEntity(
                    birthdayRunId = runId,
                    contactId = contact.id,
                    status = BirthdayItemStatus.PENDING,
                    generatedMessagePreview = message,
                    errorMessage = null
                )
            )
        }

        // Notification
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showTaskNotification(
            taskId = 999999 + runId, // Use a special range for birthday runs
            title = "Birthday Review Required",
            message = "Today there are ${contacts.size} birthday contacts to review."
        )

        return Result.success()
    }
}
