package com.onlyreminder.app.data.repository

import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.GroupDao
import com.onlyreminder.app.data.database.dao.TagDao
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.ContactTagCrossRefEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import com.onlyreminder.app.domain.model.ContactStatus
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val groupDao: GroupDao,
    private val tagDao: TagDao,
) {
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
}
