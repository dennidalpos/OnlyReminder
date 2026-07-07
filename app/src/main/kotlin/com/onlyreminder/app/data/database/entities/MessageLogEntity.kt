package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onlyreminder.app.domain.model.MessageStatus
import java.time.LocalDateTime

@Entity(tableName = "message_logs")
data class MessageLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long?,
    val templateId: Long?,
    val taskId: Long?,
    val birthdayRunId: Long?,
    val channel: String,
    val mode: String,
    val status: MessageStatus,
    val errorMessage: String?,
    val payloadPreview: String,
    val sentAt: LocalDateTime?,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
