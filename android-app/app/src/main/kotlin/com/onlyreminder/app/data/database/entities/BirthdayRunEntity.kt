package com.onlyreminder.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "birthday_runs")
data class BirthdayRunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val status: String, // PENDING, COMPLETED
    val totalFound: Int,
    val totalSelected: Int,
    val totalSkipped: Int,
    val totalSent: Int,
    val totalFailed: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val completedAt: Long? = null
)

@Entity(tableName = "birthday_run_items")
data class BirthdayRunItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val birthdayRunId: Long,
    val contactId: Long,
    val status: String, // PENDING, SENT, FAILED, SKIPPED
    val generatedMessagePreview: String,
    val errorMessage: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
