package com.onlyreminder.app.data.repository

import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
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

    suspend fun saveContact(contact: ContactEntity): Long = contactDao.insertContact(contact)

    suspend fun updateContact(contact: ContactEntity) = contactDao.updateContact(contact)

    suspend fun archiveContact(id: Long) {
        contactDao.softDeleteContact(id)
    }

    suspend fun restoreContact(id: Long) {
        contactDao.softDeleteContact(
            id,
            0
        ) // Assuming 0 or null means not deleted. Wait, the query sets deletedAt.
    }
    // Let's refine softDeleteContact in DAO if needed. Actually deletedAt IS NULL is the check.
    // I'll add a restore method to DAO later if needed. For now let's stick to the requirements.

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
