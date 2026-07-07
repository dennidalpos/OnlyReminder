package com.onlyreminder.app.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.data.settings.SettingsDataStore
import com.onlyreminder.app.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val contactCount: Int = 0,
    val upcomingBirthdays: List<ContactEntity> = emptyList(),
    val birthdaysTodayCount: Int = 0,
    val pendingTasks: List<TaskEntity> = emptyList(),
    val birthdayReviewRequired: Boolean = false,
    val sendMode: String = "REMINDER_ONLY"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contactRepository: ContactRepositoryImpl,
    private val mainRepository: MainRepositoryImpl,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        contactRepository.getAllContacts(),
        mainRepository.getTasksByStatus(TaskStatus.PENDING),
        mainRepository.getAllBirthdayRuns(),
        settingsDataStore.sendMode
    ) { contacts, tasks, birthdayRuns, sendMode ->
        val today = java.time.LocalDate.now()
        val birthdaysToday = contacts.filter { isBirthdayToday(it.birthday, today) }

        HomeUiState(
            contactCount = contacts.size,
            upcomingBirthdays = contacts.filter { isBirthdaySoon(it.birthday, today) }.take(5),
            birthdaysTodayCount = birthdaysToday.size,
            pendingTasks = tasks,
            birthdayReviewRequired = birthdayRuns.any { it.status == com.onlyreminder.app.domain.model.BirthdayRunStatus.PENDING },
            sendMode = sendMode
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    private fun isBirthdayToday(birthday: String?, today: java.time.LocalDate): Boolean {
        if (birthday == null) return false
        return try {
            val date = java.time.LocalDate.parse(birthday)
            date.monthValue == today.monthValue && date.dayOfMonth == today.dayOfMonth
        } catch (e: Exception) {
            false
        }
    }

    private fun isBirthdaySoon(birthday: String?, today: java.time.LocalDate): Boolean {
        if (birthday == null) return false
        return try {
            val bDate = java.time.LocalDate.parse(birthday)
            val birthdayThisYear = bDate.withYear(today.year)
            val diff = java.time.temporal.ChronoUnit.DAYS.between(today, birthdayThisYear)
            diff in 1..7
        } catch (e: Exception) {
            false
        }
    }
}
