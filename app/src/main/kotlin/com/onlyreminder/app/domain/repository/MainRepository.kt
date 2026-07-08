package com.onlyreminder.app.domain.repository

import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.GroupDao
import com.onlyreminder.app.data.database.dao.MainDao
import com.onlyreminder.app.data.database.dao.TagDao
import com.onlyreminder.app.data.database.entities.BirthdayRunEntity
import com.onlyreminder.app.data.database.entities.BirthdayRunItemEntity
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.ContactTagCrossRefEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.MessageLogEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.database.entities.TemplateEntity
import com.onlyreminder.app.domain.model.ContactStatus
import com.onlyreminder.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val contactDao: ContactDao,
    private val groupDao: GroupDao,
    private val tagDao: TagDao,
    private val mainDao: MainDao,
) {
    // Contacts
    fun getAllContacts(): Flow<List<ContactEntity>> = contactDao.getAllContacts()

    fun searchContacts(
        query: String? = null,
        groupId: Long? = null,
        status: ContactStatus? = null
    ): Flow<List<ContactEntity>> =
        contactDao.searchContacts(query, groupId, status)

    fun getContactsByTag(tagName: String): Flow<List<ContactEntity>> =
        contactDao.getContactsByTag(tagName)

    suspend fun getContactById(id: Long): ContactEntity? = contactDao.getContactById(id)

    suspend fun getContactsWithBirthdayOn(month: Int, day: Int): List<ContactEntity> {
        val monthDay = String.format(Locale.US, "%02d-%02d", month, day)
        return contactDao.getContactsWithBirthdayOn(monthDay)
    }

    suspend fun saveContact(contact: ContactEntity): Long = contactDao.insertContact(contact)

    suspend fun updateContact(contact: ContactEntity) = contactDao.updateContact(contact)

    suspend fun archiveContact(id: Long) {
        contactDao.getContactById(id)?.let { contact ->
            contactDao.updateContact(contact.copy(status = ContactStatus.ARCHIVED))
        }
    }

    suspend fun restoreContact(id: Long) {
        contactDao.getContactById(id)?.let { contact ->
            contactDao.updateContact(contact.copy(status = ContactStatus.ACTIVE))
        }
    }

    suspend fun hardDeleteContact(contact: ContactEntity) {
        contactDao.deleteContact(contact)
    }

    // Groups
    fun getAllGroups(): Flow<List<GroupEntity>> = groupDao.getAllGroups()
    suspend fun getGroupById(id: Long): GroupEntity? = groupDao.getGroupById(id)
    suspend fun saveGroup(group: GroupEntity): Long = groupDao.insertGroup(group)
    suspend fun deleteGroup(group: GroupEntity) = groupDao.deleteGroup(group)

    // Tags
    fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()

    suspend fun addTagToContact(contactId: Long, tagName: String) {
        tagDao.insertTag(TagEntity(tagName))
        tagDao.insertContactTagCrossRef(ContactTagCrossRefEntity(contactId, tagName))
    }

    suspend fun removeTagFromContact(contactId: Long, tagName: String) {
        tagDao.deleteContactTagCrossRef(contactId, tagName)
    }

    suspend fun updateContactTags(contactId: Long, tagNames: List<String>) {
        tagDao.deleteAllTagsForContact(contactId)
        tagNames.forEach { tagName ->
            addTagToContact(contactId, tagName)
        }
    }

    fun getTagsForContact(contactId: Long): Flow<List<TagEntity>> =
        tagDao.getTagsForContact(contactId)

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
    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskEntity>> =
        mainDao.getTasksByStatus(status)

    fun getTasksForContact(contactId: Long): Flow<List<TaskEntity>> =
        mainDao.getTasksForContact(contactId)

    suspend fun getTaskById(id: Long): TaskEntity? = mainDao.getTaskById(id)
    suspend fun saveTask(task: TaskEntity): Long = mainDao.insertTask(task)
    suspend fun deleteTask(task: TaskEntity) = mainDao.deleteTask(task)
    suspend fun updateTaskStatus(taskId: Long, status: TaskStatus) {
        mainDao.getTaskById(taskId)?.let { task ->
            val completedAt = if (status == TaskStatus.COMPLETED) LocalDateTime.now() else null
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
