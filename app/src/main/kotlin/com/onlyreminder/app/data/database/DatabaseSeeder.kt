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
        val vipGroupId = groupDao.insertGroup(
            GroupEntity(
                name = "Clienti VIP",
                description = "Clienti con priorità alta",
                color = 0xFFFFD700.toInt(),
            ),
        )
        val sportGroupId = groupDao.insertGroup(
            GroupEntity(
                name = "Sport",
                description = "Compagni di squadra e allenatori",
                color = 0xFFFF9800.toInt(),
            ),
        )

        // 2. Seed Templates
        mainDao.insertTemplate(
            TemplateEntity(
                name = "Auguri Compleanno Formale",
                language = "it",
                channel = "WHATSAPP",
                body = "Gentile {nome}, Le auguro un felice compleanno. Cordiali saluti.",
                variables = "nome",
                isDefault = true,
                whatsappApprovedTemplateName = null,
            ),
        )
        mainDao.insertTemplate(
            TemplateEntity(
                name = "Auguri Compleanno Amici",
                language = "it",
                channel = "WHATSAPP",
                body = "Ehi {nome}! Tanti auguri di buon compleanno! Ci vediamo presto per festeggiare! 🎂",
                variables = "nome",
                isDefault = false,
                whatsappApprovedTemplateName = null,
            ),
        )

        // 3. Seed Contacts
        val today = LocalDate.now()

        val marioId = contactDao.insertContact(
            ContactEntity(
                firstName = "Mario",
                lastName = "Rossi",
                displayName = "Mario Rossi",
                phone = "+393401234567",
                normalizedPhone = "393401234567",
                email = "mario.rossi@example.it",
                company = "Rossi Termoidraulica",
                birthday = today.toString(), // Birthday today for demo purposes
                groupId = workGroupId,
                source = "MANUAL",
                notes = "Cliente importante",
                status = ContactStatus.ACTIVE,
                lastContactDate = LocalDateTime.now().minusDays(5),
                marketingConsent = true,
                privacyConsent = true,
            ),
        )

        val giuliaId = contactDao.insertContact(
            ContactEntity(
                firstName = "Giulia",
                lastName = "Bianchi",
                displayName = "Giulia Bianchi",
                phone = "+393339876543",
                normalizedPhone = "393339876543",
                email = "giulia.b@email.it",
                company = "",
                birthday = today.minusMonths(2).toString(),
                groupId = familyGroupId,
                source = "MANUAL",
                notes = "Sorella",
                status = ContactStatus.ACTIVE,
                lastContactDate = LocalDateTime.now().minusDays(1),
                marketingConsent = true,
                privacyConsent = true,
            ),
        )

        contactDao.insertContact(
            ContactEntity(
                firstName = "Luca",
                lastName = "Verdi",
                displayName = "Luca Verdi",
                phone = "+393281122334",
                normalizedPhone = "393281122334",
                email = "luca.verdi@posta.it",
                company = "Verdi Tech",
                birthday = today.plusDays(2).toString(), // Birthday in 2 days
                groupId = friendsGroupId,
                source = "MANUAL",
                notes = "Compagno di università",
                status = ContactStatus.ACTIVE,
                lastContactDate = LocalDateTime.now().minusMonths(1),
                marketingConsent = false,
                privacyConsent = true,
            ),
        )

        val elenaId = contactDao.insertContact(
            ContactEntity(
                firstName = "Elena",
                lastName = "Gialli",
                displayName = "Elena Gialli",
                phone = "+393475566778",
                normalizedPhone = "393475566778",
                email = "elena.g@vip.it",
                company = "Luxury Living",
                birthday = today.plusDays(10).toString(),
                groupId = vipGroupId,
                source = "MANUAL",
                notes = "Contatto VIP per eventi",
                status = ContactStatus.ACTIVE,
                lastContactDate = LocalDateTime.now().minusWeeks(2),
                marketingConsent = true,
                privacyConsent = true,
            ),
        )

        val marcoId = contactDao.insertContact(
            ContactEntity(
                firstName = "Marco",
                lastName = "Bruni",
                displayName = "Marco Bruni",
                phone = "+393201122334",
                normalizedPhone = "393201122334",
                email = "m.bruni@sport.it",
                company = "Palestra Fit",
                birthday = today.minusDays(5).toString(),
                groupId = sportGroupId,
                source = "MANUAL",
                notes = "Allenatore",
                status = ContactStatus.ACTIVE,
                lastContactDate = LocalDateTime.now().minusDays(3),
                marketingConsent = true,
                privacyConsent = true,
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
                dueDateTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                repeatRule = null,
                priority = 2,
                status = TaskStatus.PENDING,
                templateId = null,
                sendMode = "MANUAL",
            )
        )

        mainDao.insertTask(
            TaskEntity(
                title = "Chiamata di follow-up",
                description = "Chiamare Elena per confermare la partecipazione all'evento",
                contactId = elenaId,
                groupId = vipGroupId,
                type = "REMINDER",
                dueDateTime = LocalDateTime.now().plusHours(4),
                repeatRule = null,
                priority = 1,
                status = TaskStatus.PENDING,
                templateId = null,
                sendMode = "MANUAL",
            )
        )

        mainDao.insertTask(
            TaskEntity(
                title = "Aggiornare catalogo",
                description = "Aggiornare il file PDF del catalogo prodotti 2024",
                contactId = null,
                groupId = workGroupId,
                type = "MANUAL",
                dueDateTime = LocalDateTime.now().plusDays(3),
                repeatRule = null,
                priority = 0,
                status = TaskStatus.PENDING,
                templateId = null,
                sendMode = "MANUAL",
            )
        )

        mainDao.insertTask(
            TaskEntity(
                title = "Verifica Backup",
                description = "Controllare che il backup settimanale sia stato eseguito correttamente",
                contactId = null,
                groupId = null,
                type = "MANUAL",
                dueDateTime = LocalDateTime.now().minusDays(1),
                repeatRule = "WEEKLY",
                priority = 1,
                status = TaskStatus.COMPLETED,
                templateId = null,
                sendMode = "MANUAL",
                completedAt = LocalDateTime.now().minusDays(1)
            )
        )

        // 5. Seed Tags
        val tags = listOf("VIP", "Urgente", "Potenziale Cliente", "Famiglia", "Palestra")
        tags.forEach { tagDao.insertTag(TagEntity(it)) }

        tagDao.insertContactTagCrossRef(ContactTagCrossRefEntity(marioId, "Potenziale Cliente"))
        tagDao.insertContactTagCrossRef(ContactTagCrossRefEntity(elenaId, "VIP"))
        tagDao.insertContactTagCrossRef(ContactTagCrossRefEntity(elenaId, "Urgente"))
        tagDao.insertContactTagCrossRef(ContactTagCrossRefEntity(marcoId, "Palestra"))

        // 6. Seed Message Logs (Simulation of previous activity)
        mainDao.insertLog(
            MessageLogEntity(
                contactId = marioId,
                templateId = null,
                taskId = null,
                birthdayRunId = null,
                channel = "WHATSAPP",
                mode = "MANUAL",
                status = MessageStatus.SENT,
                errorMessage = null,
                payloadPreview = "Buongiorno Mario, come concordato...",
                sentAt = LocalDateTime.now().minusDays(5),
            )
        )

        mainDao.insertLog(
            MessageLogEntity(
                contactId = giuliaId,
                templateId = null,
                taskId = null,
                birthdayRunId = null,
                channel = "WHATSAPP",
                mode = "MANUAL",
                status = MessageStatus.SENT,
                errorMessage = null,
                payloadPreview = "Ciao Giulia, come stai?",
                sentAt = LocalDateTime.now().minusDays(1),
            )
        )
    }
}
