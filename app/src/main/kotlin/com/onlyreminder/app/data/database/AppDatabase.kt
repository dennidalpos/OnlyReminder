package com.onlyreminder.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.MainDao
import com.onlyreminder.app.data.database.entities.BirthdayRunEntity
import com.onlyreminder.app.data.database.entities.BirthdayRunItemEntity
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.ContactTagCrossRefEntity
import com.onlyreminder.app.data.database.entities.CustomFieldEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.MessageLogEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.database.entities.TemplateEntity

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
        MessageLogEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun mainDao(): MainDao

    companion object {
        const val DB_NAME = "onlyreminder.db"
    }
}
