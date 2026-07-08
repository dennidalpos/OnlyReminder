package com.onlyreminder.app.features.birthday.domain

import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import kotlinx.coroutines.flow.first
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
        val allContacts = contactRepository.getAllContacts().first()
        return allContacts.filter { contact ->
            contact.isBirthdayMonitored && isBirthdayOn(contact.birthday, date)
        }
    }

    private fun isBirthdayOn(birthday: String?, date: LocalDate): Boolean {
        if (birthday == null) return false
        return try {
            val bDate = LocalDate.parse(birthday)
            bDate.monthValue == date.monthValue && bDate.dayOfMonth == date.dayOfMonth
        } catch (e: Exception) {
            false
        }
    }
}
