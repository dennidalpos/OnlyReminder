package com.onlyreminder.app.data.repository

import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.ContactTagCrossRefEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
) {
    fun getAllContacts(): Flow<List<ContactEntity>> = contactDao.getAllContacts()

    fun searchContacts(
        query: String? = null,
        groupId: Long? = null,
        status: String? = null
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

    suspend fun softDeleteContact(id: Long) {
        contactDao.softDeleteContact(id, LocalDateTime.now())
    }

    suspend fun archiveContact(id: Long) {
        // In this app, archive might be different from soft delete, 
        // but based on current code it's using softDeleteContact.
        // Let's use the status field for archive if that's the intent.
        contactDao.getContactById(id)?.let { contact ->
            contactDao.updateContact(contact.copy(status = "ARCHIVED"))
        }
    }

    suspend fun restoreContact(id: Long) {
        contactDao.restoreContact(id)
    }

    suspend fun hardDeleteContact(contact: ContactEntity) {
        contactDao.hardDeleteContact(contact)
    }

    // Groups
    fun getAllGroups(): Flow<List<GroupEntity>> = contactDao.getAllGroups()
    suspend fun getGroupById(id: Long): GroupEntity? = contactDao.getGroupById(id)
    suspend fun saveGroup(group: GroupEntity): Long = contactDao.insertGroup(group)
    suspend fun deleteGroup(group: GroupEntity) = contactDao.deleteGroup(group)

    // Tags
    fun getAllTags(): Flow<List<TagEntity>> = contactDao.getAllTags()

    suspend fun addTagToContact(contactId: Long, tagName: String) {
        contactDao.insertTag(TagEntity(tagName))
        contactDao.insertContactTagCrossRef(ContactTagCrossRefEntity(contactId, tagName))
    }

    suspend fun removeTagFromContact(contactId: Long, tagName: String) {
        contactDao.deleteContactTagCrossRef(contactId, tagName)
    }

    suspend fun updateContactTags(contactId: Long, tagNames: List<String>) {
        contactDao.deleteAllTagsForContact(contactId)
        tagNames.forEach { tagName ->
            addTagToContact(contactId, tagName)
        }
    }

    fun getTagsForContact(contactId: Long): Flow<List<TagEntity>> =
        contactDao.getTagsForContact(contactId)
}
