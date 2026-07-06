package com.onlyreminder.app.features.birthday.domain

import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BirthdayScanner @Inject constructor(
    private val contactRepository: ContactRepositoryImpl,
) {
    /**
     * Finds contacts whose birthday matches the given date (day and month).
     * Now uses database-level filtering for better performance.
     */
    suspend fun findBirthdaysForDate(date: LocalDate): List<ContactEntity> {
        return contactRepository.getContactsWithBirthdayOn(
            month = date.monthValue,
            day = date.dayOfMonth
        )
    }
}
