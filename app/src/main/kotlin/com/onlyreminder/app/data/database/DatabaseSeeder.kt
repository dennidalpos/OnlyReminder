package com.onlyreminder.app.data.database

import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.GroupDao
import com.onlyreminder.app.data.database.dao.MainDao
import com.onlyreminder.app.data.database.dao.TagDao
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.ContactTagCrossRefEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.MessageLogEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.database.entities.TemplateEntity
import com.onlyreminder.app.domain.model.ContactStatus
import com.onlyreminder.app.domain.model.MessageStatus
import com.onlyreminder.app.domain.model.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class DatabaseSeeder @Inject constructor(
    private val contactDao: ContactDao,
    private val groupDao: GroupDao,
    private val mainDao: MainDao,
    private val tagDao: TagDao,
) {
    suspend fun seedDemoData() {
        // Check if already seeded
        if (contactDao.getContactById(1) != null) return
        
        // 1. Seed Groups
        val familyGroupId = groupDao.insertGroup(
            GroupEntity(
                name = "Famiglia",
                description = "Parenti stretti",
                color = 0xFFE91E63.toInt(),
            ),
        )
        val friendsGroupId = groupDao.insertGroup(
            GroupEntity(
                name = "Amici",
                description = "Amici di vecchia data",
                color = 0xFF2196F3.toInt(),
            ),
        )
        val workGroupId = groupDao.insertGroup(
            GroupEntity(
                name = "Lavoro",
                description = "Colleghi e clienti",
                color = 0xFF4CAF50.toInt(),
            ),
        )

        // 2. Seed Templates
        val bdayFriendsTemplate = mainDao.insertTemplate(
            TemplateEntity(
                name = "Compleanno Amici",
                language = "it",
                channel = "WHATSAPP_MANUAL",
                body = "Ehi {first_name}! Tanti auguri di buon compleanno! 🎂 Ci vediamo presto!",
                variables = "first_name",
                isDefault = true,
                whatsappApprovedTemplateName = null,
            ),
        )
        
        mainDao.insertTemplate(
            TemplateEntity(
                name = "Compleanno Formale",
                language = "it",
                channel = "WHATSAPP_MANUAL",
                body = "Gentile {first_name}, Le auguro un felice compleanno. Cordiali saluti.",
                variables = "first_name",
                isDefault = false,
                whatsappApprovedTemplateName = null,
            ),
        )

        // 3. Seed Contacts
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val marioId = contactDao.insertContact(
            ContactEntity(
                firstName = "Mario",
                lastName = "Rossi",
                displayName = "Mario Rossi",
                phone = "+393401234567",
                normalizedPhone = "+393401234567",
                email = "mario.rossi@example.it",
                company = "Rossi Termoidraulica",
                birthday = today.toString(), // Compleanno OGGI
                groupId = workGroupId,
                source = "MANUAL",
                notes = "Cliente importante",
                status = ContactStatus.ACTIVE,
                isBirthdayMonitored = true,
                lastContactDate = LocalDateTime.now().minusDays(5),
            ),
        )

        val giuliaId = contactDao.insertContact(
            ContactEntity(
                firstName = "Giulia",
                lastName = "Bianchi",
                displayName = "Giulia Bianchi",
                phone = "+393339876543",
                normalizedPhone = "+393339876543",
                email = "giulia.b@email.it",
                company = "",
                birthday = tomorrow.toString(), // Compleanno DOMANI
                groupId = familyGroupId,
                source = "MANUAL",
                notes = "Sorella",
                status = ContactStatus.ACTIVE,
                isBirthdayMonitored = true,
                lastContactDate = LocalDateTime.now().minusDays(1),
            ),
        )

        contactDao.insertContact(
            ContactEntity(
                firstName = "Luca",
                lastName = "Verdi",
                displayName = "Luca Verdi",
                phone = "+393281122334",
                normalizedPhone = "+393281122334",
                email = "luca.verdi@posta.it",
                company = "Verdi Tech",
                birthday = today.plusMonths(1).toString(),
                groupId = friendsGroupId,
                source = "MANUAL",
                notes = "Compagno di università - Monitoraggio Disattivato",
                status = ContactStatus.ACTIVE,
                isBirthdayMonitored = false, // Monitoraggio disattivato
                lastContactDate = LocalDateTime.now().minusMonths(1),
            ),
        )

        // 4. Seed Tasks
        mainDao.insertTask(
            TaskEntity(
                title = "Inviare preventivo",
                description = "Preparare e inviare il preventivo per la manutenzione a Mario Rossi",
                contactId = marioId,
                groupId = workGroupId,
                type = "REMINDER",
                dueDateTime = LocalDateTime.now().plusHours(2),
                repeatRule = null,
                priority = 2,
                status = TaskStatus.PENDING,
                templateId = null,
                sendMode = "REMINDER_ONLY",
            )
        )

        mainDao.insertTask(
            TaskEntity(
                title = "Reminder Compleanno Giulia",
                description = "Preparare regalo per Giulia",
                contactId = giuliaId,
                groupId = familyGroupId,
                type = "REMINDER",
                dueDateTime = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                repeatRule = null,
                priority = 1,
                status = TaskStatus.PENDING,
                templateId = bdayFriendsTemplate,
                sendMode = "MANUAL_WHATSAPP",
            )
        )

        // 5. Seed Tags
        val tags = listOf("Urgente", "Potenziale Cliente", "Famiglia")
        tags.forEach { tagDao.insertTag(TagEntity(it)) }

        tagDao.insertContactTagCrossRef(ContactTagCrossRefEntity(marioId, "Potenziale Cliente"))
        tagDao.insertContactTagCrossRef(ContactTagCrossRefEntity(giuliaId, "Famiglia"))
    }
}
