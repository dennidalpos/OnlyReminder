package com.onlyreminder.app.features.birthday.domain

import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BirthdayScanner @Inject constructor(
    private val contactRepository: ContactRepositoryImpl
) {
    suspend fun findBirthdaysForDate(date: Date): List<ContactEntity> {
        val contacts = contactRepository.getAllContacts().first()
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) // 0-indexed

        return contacts.filter { contact ->
            contact.birthday?.let { bdayStr ->
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val bday = sdf.parse(bdayStr)
                    if (bday != null) {
                        val bdayCal = Calendar.getInstance()
                        bdayCal.time = bday
                        bdayCal.get(Calendar.DAY_OF_MONTH) == day && bdayCal.get(Calendar.MONTH) == month
                    } else false
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }
    }
}
