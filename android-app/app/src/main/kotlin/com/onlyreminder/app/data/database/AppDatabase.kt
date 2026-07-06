package com.onlyreminder.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.MainDao
import com.onlyreminder.app.data.database.entities.*

@Database(
    entities = [
        ContactEntity::class,
        GroupEntity::class,
        TagEntity::class,
        ContactTagCrossRefEntity::class,
        CustomFieldEntity::class,
        TemplateEntity::class,
        TaskEntity::class,
        BirthdayRunEntity::class,
        BirthdayRunItemEntity::class,
        MessageLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun mainDao(): MainDao

    companion object {
        const val DB_NAME = "onlyreminder.db"
    }
}
