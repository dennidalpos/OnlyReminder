package com.onlyreminder.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onlyreminder.app.data.database.entities.BirthdayRunEntity
import com.onlyreminder.app.data.database.entities.BirthdayRunItemEntity
import com.onlyreminder.app.data.database.entities.MessageLogEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.database.entities.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MainDao {
    // Templates
    @Query("SELECT * FROM templates")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): TemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity): Long

    @Delete
    suspend fun deleteTemplate(template: TemplateEntity)

    @Query("UPDATE templates SET isDefault = 0 WHERE channel = :channel")
    suspend fun clearDefaultsForChannel(channel: String)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY dueDateTime ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDateTime ASC")
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE contactId = :contactId ORDER BY dueDateTime ASC")
    fun getTasksForContact(contactId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Birthday Runs
    @Query("SELECT * FROM birthday_runs ORDER BY createdAt DESC")
    fun getAllBirthdayRuns(): Flow<List<BirthdayRunEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBirthdayRun(run: BirthdayRunEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBirthdayRunItem(item: BirthdayRunItemEntity)

    @Query("SELECT * FROM birthday_run_items WHERE birthdayRunId = :runId")
    fun getItemsForRun(runId: Long): Flow<List<BirthdayRunItemEntity>>

    // Logs
    @Query("SELECT * FROM message_logs ORDER BY createdAt DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<MessageLogEntity>>

    @Insert
    suspend fun insertLog(log: MessageLogEntity)
}
