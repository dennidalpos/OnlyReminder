package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "contact_tag_cross_ref", primaryKeys = ["contactId", "tagName"])
data class ContactTagCrossRefEntity(
    val contactId: Long,
    val tagName: String
)
