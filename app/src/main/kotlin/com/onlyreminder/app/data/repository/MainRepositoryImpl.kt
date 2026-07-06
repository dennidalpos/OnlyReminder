package com.onlyreminder.app.data.repository

import com.onlyreminder.app.data.database.dao.MainDao
import com.onlyreminder.app.data.database.entities.BirthdayRunEntity
import com.onlyreminder.app.data.database.entities.BirthdayRunItemEntity
import com.onlyreminder.app.data.database.entities.MessageLogEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.database.entities.TemplateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val mainDao: MainDao,
) {
    // Templates
    fun getAllTemplates(): Flow<List<TemplateEntity>> = mainDao.getAllTemplates()
    suspend fun getTemplateById(id: Long): TemplateEntity? = mainDao.getTemplateById(id)
    suspend fun saveTemplate(template: TemplateEntity) {
        if (template.isDefault) {
            mainDao.clearDefaultsForChannel(template.channel)
        }
        mainDao.insertTemplate(template)
    }

    suspend fun deleteTemplate(template: TemplateEntity) = mainDao.deleteTemplate(template)
    suspend fun duplicateTemplate(template: TemplateEntity) {
        val newTemplate = template.copy(
            id = 0,
            name = "${template.name} (Copy)",
            isDefault = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        mainDao.insertTemplate(newTemplate)
    }

    // Tasks
    fun getAllTasks(): Flow<List<TaskEntity>> = mainDao.getAllTasks()
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>> = mainDao.getTasksByStatus(status)
    fun getTasksForContact(contactId: Long): Flow<List<TaskEntity>> =
        mainDao.getTasksForContact(contactId)

    suspend fun getTaskById(id: Long): TaskEntity? = mainDao.getTaskById(id)
    suspend fun saveTask(task: TaskEntity): Long = mainDao.insertTask(task)
    suspend fun deleteTask(task: TaskEntity) = mainDao.deleteTask(task)
    suspend fun updateTaskStatus(taskId: Long, status: String) {
        mainDao.getTaskById(taskId)?.let { task ->
            val completedAt = if (status == "COMPLETED") LocalDateTime.now() else null
            mainDao.insertTask(task.copy(status = status, completedAt = completedAt))
        }
    }

    // Birthday Runs
    fun getAllBirthdayRuns(): Flow<List<BirthdayRunEntity>> = mainDao.getAllBirthdayRuns()
    suspend fun createBirthdayRun(run: BirthdayRunEntity): Long = mainDao.insertBirthdayRun(run)
    suspend fun addRunItem(item: BirthdayRunItemEntity) = mainDao.insertBirthdayRunItem(item)
    fun getItemsForRun(runId: Long): Flow<List<BirthdayRunItemEntity>> =
        mainDao.getItemsForRun(runId)

    // Logs
    fun getRecentLogs(): Flow<List<MessageLogEntity>> = mainDao.getRecentLogs()
    suspend fun addLog(log: MessageLogEntity) = mainDao.insertLog(log)

    suspend fun initializeDefaultTemplates() {
        val existing = mainDao.getAllTemplates().first()
        if (existing.isEmpty()) {
            saveTemplate(
                TemplateEntity(
                    name = "Birthday (English)",
                    language = "EN",
                    channel = "WHATSAPP_MANUAL",
                    body = "Happy birthday {first_name}! Wishing you a wonderful day.",
                    variables = "first_name",
                    isDefault = true,
                    whatsappApprovedTemplateName = null
                )
            )
            saveTemplate(
                TemplateEntity(
                    name = "Birthday (Italiano)",
                    language = "IT",
                    channel = "WHATSAPP_MANUAL",
                    body = "Tanti auguri {first_name}! Ti auguro una splendida giornata.",
                    variables = "first_name",
                    isDefault = false,
                    whatsappApprovedTemplateName = null
                )
            )
        }
    }
}
