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
    private val settingsDataStore: com.onlyreminder.app.data.settings.SettingsDataStore,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val dateStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Mark previous runs as NOT_REVIEWED if they are still PENDING
        val allRuns = mainRepository.getAllBirthdayRuns().first()
        allRuns.forEach { run ->
            if ((run.date != dateStr) && (run.status == BirthdayRunStatus.PENDING)) {
                mainRepository.createBirthdayRun(run.copy(status = BirthdayRunStatus.NOT_REVIEWED))
            }
        }

        val contactsToday = birthdayScanner.findBirthdaysForDate(today)
        val contactsTomorrow = birthdayScanner.findBirthdaysForDate(tomorrow)
        
        val allRelevantContacts = (contactsToday + contactsTomorrow).distinctBy { it.id }

        if (allRelevantContacts.isEmpty()) {
            return Result.success()
        }

        // Get template for birthday
        val templates = mainRepository.getAllTemplates().first()
        val templateId = settingsDataStore.birthdayTemplateId.first()
        val appLanguage = settingsDataStore.language.first()

        val selectedTemplate = if (templateId != null) {
            templates.find { it.id == templateId }
        } else {
            // Fallback: Default birthday template matching app language, or any default birthday template
            templates.find { it.isDefault && it.language.equals(appLanguage, ignoreCase = true) && it.name.contains("Birthday", ignoreCase = true) }
                ?: templates.find { it.isDefault && it.name.contains("Birthday", ignoreCase = true) }
                ?: templates.find { it.language.equals(appLanguage, ignoreCase = true) && it.name.contains("Birthday", ignoreCase = true) }
                ?: templates.find { it.name.contains("Birthday", ignoreCase = true) }
        }

        val templateEngine = TemplateEngine()

        // Check if run already exists for today, if so, we might update it or skip
        val existingRun = allRuns.find { it.date == dateStr }
        val runId = existingRun?.id ?: mainRepository.createBirthdayRun(
            BirthdayRunEntity(
                date = dateStr,
                status = BirthdayRunStatus.PENDING,
                totalFound = allRelevantContacts.size,
                totalSelected = allRelevantContacts.size,
                totalSkipped = 0,
                totalSent = 0,
                totalFailed = 0
            )
        )

        val existingItems = mainRepository.getItemsForRun(runId).first()
        val existingContactIds = existingItems.asSequence().map { it.contactId }.toSet()

        allRelevantContacts.forEach { contact ->
            if (!existingContactIds.contains(contact.id)) {
                val message = if (selectedTemplate != null) {
                    templateEngine.render(selectedTemplate.body, contact)
                } else {
                    applicationContext.getString(com.onlyreminder.app.R.string.birthday_fallback_message, contact.firstName)
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
        }

        // Notification
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showTaskNotification(
            taskId = 999999 + runId,
            title = applicationContext.getString(com.onlyreminder.app.R.string.birthday_check_notification_title),
            message = applicationContext.getString(com.onlyreminder.app.R.string.birthday_check_notification_message, allRelevantContacts.size)
        )

        return Result.success()
    }
}
