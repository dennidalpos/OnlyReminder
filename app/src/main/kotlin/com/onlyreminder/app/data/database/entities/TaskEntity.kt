package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val contactId: Long?,
    val groupId: Long?,
    val type: String, // REMINDER, BIRTHDAY, MANUAL
    val dueDateTime: LocalDateTime,
    val repeatRule: String?,
    val priority: Int,
    val status: String, // PENDING, COMPLETED, SKIPPED
    val templateId: Long?,
    val sendMode: String, // MANUAL, AUTO_REVIEW
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)
