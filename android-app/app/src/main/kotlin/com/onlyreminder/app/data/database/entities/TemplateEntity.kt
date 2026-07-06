package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val language: String,
    val channel: String, // WHATSAPP, SMS, etc.
    val body: String,
    val variables: String, // JSON or comma separated
    val isDefault: Boolean,
    val whatsappApprovedTemplateName: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
