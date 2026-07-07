package com.onlyreminder.app.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.settings.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val contactCount: Int = 0,
    val upcomingBirthdays: List<ContactEntity> = emptyList(),
    val sendMode: String = "REMINDER_ONLY"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contactRepository: ContactRepositoryImpl,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        contactRepository.getAllContacts(),
        settingsDataStore.sendMode
    ) { contacts, sendMode ->
        HomeUiState(
            contactCount = contacts.size,
            upcomingBirthdays = contacts.filter { isBirthdaySoon(it.birthday) }.take(5),
            sendMode = sendMode
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    private fun isBirthdaySoon(birthday: String?): Boolean {
        // Simple logic for demonstration, could be improved with proper date handling
        if (birthday == null) return false
        // For now, just return false or implement a real check if needed
        return false
    }
}
