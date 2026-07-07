package com.onlyreminder.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onlyreminder.app.data.database.entities.ContactTagCrossRefEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
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
}
