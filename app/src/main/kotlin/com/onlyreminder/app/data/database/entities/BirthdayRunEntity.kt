package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "birthday_runs")
data class BirthdayRunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val status: String, // PENDING, COMPLETED, NOT_REVIEWED
    val totalFound: Int,
    val totalSelected: Int,
    val totalSkipped: Int,
    val totalSent: Int,
    val totalFailed: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val reviewedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null
)

@Entity(tableName = "birthday_run_items")
data class BirthdayRunItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val birthdayRunId: Long,
    val contactId: Long,
    val status: String, // PENDING, SENT, FAILED, SKIPPED
    val generatedMessagePreview: String,
    val errorMessage: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
