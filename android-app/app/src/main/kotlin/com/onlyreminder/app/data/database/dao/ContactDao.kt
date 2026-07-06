package com.onlyreminder.app.data.database.dao

import androidx.room.*
import com.onlyreminder.app.data.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE deletedAt IS NULL ORDER BY displayName ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query(
        """
        SELECT * FROM contacts 
        WHERE deletedAt IS NULL 
        AND (:query IS NULL OR displayName LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%')
        AND (:groupId IS NULL OR groupId = :groupId)
        AND (:status IS NULL OR status = :status)
        ORDER BY displayName ASC
    """
    )
    fun searchContacts(query: String?, groupId: Long?, status: String?): Flow<List<ContactEntity>>

    @Query(
        """
        SELECT c.* FROM contacts c
        INNER JOIN contact_tag_cross_ref ct ON c.id = ct.contactId
        WHERE c.deletedAt IS NULL AND ct.tagName = :tagName
        ORDER BY c.displayName ASC
    """
    )
    fun getContactsByTag(tagName: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Query("UPDATE contacts SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteContact(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun hardDeleteContact(contact: ContactEntity)

    // Groups
    @Query("SELECT * FROM groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE id = :id")
    suspend fun getGroupById(id: Long): GroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long

    @Delete
    suspend fun deleteGroup(group: GroupEntity)

    // Tags
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactTagCrossRef(crossRef: ContactTagCrossRefEntity)

    @Query("DELETE FROM contact_tag_cross_ref WHERE contactId = :contactId AND tagName = :tagName")
    suspend fun deleteContactTagCrossRef(contactId: Long, tagName: String)

    @Query("DELETE FROM contact_tag_cross_ref WHERE contactId = :contactId")
    suspend fun deleteAllTagsForContact(contactId: Long)

    @Query(
        """
        SELECT tags.* FROM tags 
        INNER JOIN contact_tag_cross_ref ON tags.name = contact_tag_cross_ref.tagName 
        WHERE contact_tag_cross_ref.contactId = :contactId
    """
    )
    fun getTagsForContact(contactId: Long): Flow<List<TagEntity>>

    // Custom Fields
    @Query("SELECT * FROM contact_custom_fields WHERE contactId = :contactId")
    fun getCustomFieldsForContact(contactId: Long): Flow<List<CustomFieldEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomField(field: CustomFieldEntity)
}
