package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val phone: String,
    val normalizedPhone: String,
    val email: String,
    val company: String,
    val birthday: String?, // Format: YYYY-MM-DD
    val groupId: Long?,
    val source: String,
    val notes: String,
    val status: String, // ACTIVE, ARCHIVED
    val lastContactDate: Long?,
    val marketingConsent: Boolean,
    val privacyConsent: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)
