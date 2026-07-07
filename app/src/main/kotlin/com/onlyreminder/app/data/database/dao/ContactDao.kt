package com.onlyreminder.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.CustomFieldEntity
import com.onlyreminder.app.domain.model.ContactStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY displayName ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query(
        """
        SELECT * FROM contacts 
        WHERE (:query IS NULL OR displayName LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%')
        AND (:groupId IS NULL OR groupId = :groupId)
        AND (:status IS NULL OR status = :status)
        ORDER BY displayName ASC
    """,
    )
    fun searchContacts(
        query: String?,
        groupId: Long?,
        status: ContactStatus?
    ): Flow<List<ContactEntity>>

    @Query(
        """
        SELECT c.* FROM contacts c
        INNER JOIN contact_tag_cross_ref ct ON c.id = ct.contactId
        WHERE ct.tagName = :tagName
        ORDER BY c.displayName ASC
    """
    )
    fun getContactsByTag(tagName: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): ContactEntity?

    @Query(
        """
        SELECT * FROM contacts 
        WHERE birthday IS NOT NULL 
        AND strftime('%m-%d', birthday) = :monthDay
    """
    )
    suspend fun getContactsWithBirthdayOn(monthDay: String): List<ContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    // Custom Fields
    @Query("SELECT * FROM contact_custom_fields WHERE contactId = :contactId")
    fun getCustomFieldsForContact(contactId: Long): Flow<List<CustomFieldEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomField(field: CustomFieldEntity)
}
