package com.onlyreminder.app.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.BirthdayRunEntity
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.domain.repository.MainRepository
import com.onlyreminder.app.data.settings.SettingsDataStore
import com.onlyreminder.app.domain.model.SendMode
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
    val birthdaysTomorrowCount: Int = 0,
    val pendingTasks: List<TaskEntity> = emptyList(),
    val birthdayReviewRequired: Boolean = false,
    val sendMode: SendMode = SendMode.REMINDER_ONLY,
    val showBackupWarning: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: MainRepository,
    settingsDataStore: SettingsDataStore,
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllContacts(),
        repository.getTasksByStatus(TaskStatus.PENDING),
        repository.getAllBirthdayRuns(),
        settingsDataStore.sendMode,
        settingsDataStore.lastBackupTime,
        settingsDataStore.showBackupBanner,
    ) { args ->
        val contacts = args[0] as List<ContactEntity>
        val tasks = args[1] as List<TaskEntity>
        val birthdayRuns = args[2] as List<BirthdayRunEntity>
        val sendMode = args[3] as SendMode
        val lastBackupTime = args[4] as String?
        val showBackupBannerSetting = args[5] as Boolean

        val today = java.time.LocalDate.now()
        val tomorrow = today.plusDays(1)
        
        val monitoredContacts = contacts.filter { it.isBirthdayMonitored }
        val birthdaysToday = monitoredContacts.filter { isBirthdayOn(it.birthday, today) }
        val birthdaysTomorrow = monitoredContacts.filter { isBirthdayOn(it.birthday, tomorrow) }

        val backupWarning = if (lastBackupTime == null) true else {
            val lastBackup = java.time.LocalDateTime.parse(lastBackupTime)
            java.time.temporal.ChronoUnit.DAYS.between(lastBackup, java.time.LocalDateTime.now()) > 7
        }

        HomeUiState(
            contactCount = contacts.size,
            upcomingBirthdays = monitoredContacts.asSequence().filter { isBirthdaySoon(it.birthday, today) }.take(5).toList(),
            birthdaysTodayCount = birthdaysToday.size,
            birthdaysTomorrowCount = birthdaysTomorrow.size,
            pendingTasks = tasks,
            birthdayReviewRequired = birthdayRuns.any { it.status == com.onlyreminder.app.domain.model.BirthdayRunStatus.PENDING },
            sendMode = sendMode,
            showBackupWarning = showBackupBannerSetting && contacts.isNotEmpty() && backupWarning,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(),
        )

    private fun isBirthdayOn(birthday: String?, date: java.time.LocalDate): Boolean {
        if (birthday == null) return false
        return try {
            val bDate = java.time.LocalDate.parse(birthday)
            (bDate.monthValue == date.monthValue) && (bDate.dayOfMonth == date.dayOfMonth)
        } catch (_: Exception) {
            false
        }
    }

    private fun isBirthdaySoon(birthday: String?, today: java.time.LocalDate): Boolean {
        if (birthday == null) return false
        return try {
            val bDate = java.time.LocalDate.parse(birthday)
            val birthdayThisYear = bDate.withYear(today.year)
            val diff = java.time.temporal.ChronoUnit.DAYS.between(today, birthdayThisYear)
            (diff.toInt() in 1..7)
        } catch (_: Exception) {
            false
        }
    }
}
