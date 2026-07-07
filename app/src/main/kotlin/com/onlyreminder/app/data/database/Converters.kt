package com.onlyreminder.app.data.database

import androidx.room.TypeConverter
import com.onlyreminder.app.domain.model.BirthdayItemStatus
import com.onlyreminder.app.domain.model.BirthdayRunStatus
import com.onlyreminder.app.domain.model.ContactStatus
import com.onlyreminder.app.domain.model.MessageStatus
import com.onlyreminder.app.domain.model.TaskStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromContactStatus(value: ContactStatus) = value.name

    @TypeConverter
    fun toContactStatus(value: String) = ContactStatus.valueOf(value)

    @TypeConverter
    fun fromBirthdayRunStatus(value: BirthdayRunStatus) = value.name

    @TypeConverter
    fun toBirthdayRunStatus(value: String) = BirthdayRunStatus.valueOf(value)

    @TypeConverter
    fun fromBirthdayItemStatus(value: BirthdayItemStatus) = value.name

    @TypeConverter
    fun toBirthdayItemStatus(value: String) = BirthdayItemStatus.valueOf(value)

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus) = value.name

    @TypeConverter
    fun toTaskStatus(value: String) = TaskStatus.valueOf(value)

    @TypeConverter
    fun fromMessageStatus(value: MessageStatus) = value.name

    @TypeConverter
    fun toMessageStatus(value: String) = MessageStatus.valueOf(value)
}
