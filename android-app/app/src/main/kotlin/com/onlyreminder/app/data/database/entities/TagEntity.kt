package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "contact_tag_cross_ref", primaryKeys = ["contactId", "tagName"])
data class ContactTagCrossRefEntity(
    val contactId: Long,
    val tagName: String
)
