package com.onlyreminder.app.features.contacts.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.domain.model.ContactStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactEditViewModel @Inject constructor(
    private val repository: ContactRepositoryImpl,
    private val settingsDataStore: com.onlyreminder.app.data.settings.SettingsDataStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val contactId: Long? = savedStateHandle["id"]

    private val _uiState = MutableStateFlow(ContactEditUiState())
    val uiState = _uiState.asStateFlow()

    private var initialState = ContactEditUiState()

    val hasChanges: Boolean
        get() = _uiState.value != initialState

    val groups: StateFlow<List<GroupEntity>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        contactId?.let { loadContact(it) }
    }

    private fun loadContact(id: Long) {
        viewModelScope.launch {
            repository.getContactById(id)?.let { c ->
                val tags = repository.getTagsForContact(id).first().joinToString(", ") { it.name }
                val loadedState = ContactEditUiState(
                    firstName = c.firstName,
                    lastName = c.lastName,
                    displayName = c.displayName,
                    phone = c.phone,
                    email = c.email,
                    company = c.company,
                    birthday = c.birthday ?: "",
                    groupId = c.groupId,
                    notes = c.notes,
                    tags = tags,
                    status = c.status
                )
                _uiState.value = loadedState
                initialState = loadedState
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
    }

    fun onDisplayNameChange(value: String) {
        _uiState.value = _uiState.value.copy(displayName = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onCompanyChange(value: String) {
        _uiState.value = _uiState.value.copy(company = value)
    }

    fun onBirthdayChange(value: String) {
        _uiState.value = _uiState.value.copy(birthday = value)
    }

    fun onGroupChange(id: Long?) {
        _uiState.value = _uiState.value.copy(groupId = id)
    }

    fun onNotesChange(value: String) {
        _uiState.value = _uiState.value.copy(notes = value)
    }

    fun onTagsChange(value: String) {
        _uiState.value = _uiState.value.copy(tags = value)
    }

    private fun normalizePhone(phone: String, defaultCode: String): String {
        var clean = phone.replace(Regex("[\\s\\-()]"), "")
        if (clean.startsWith("00")) {
            clean = "+" + clean.substring(2)
        }
        if (!clean.startsWith("+") && clean.isNotEmpty()) {
            clean = "$defaultCode$clean"
        }
        return clean
    }

    fun saveContact(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.displayName.isBlank()) {
            // Validation error could be handled here
            return
        }

        viewModelScope.launch {
            val normalize = settingsDataStore.normalizePhone.first()
            val countryCode = settingsDataStore.defaultCountryCode.first()
            
            val normalizedPhone = if (normalize) {
                normalizePhone(state.phone, countryCode)
            } else {
                state.phone
            }

            val contact = ContactEntity(
                id = contactId ?: 0,
                firstName = state.firstName,
                lastName = state.lastName,
                displayName = state.displayName,
                phone = state.phone,
                normalizedPhone = normalizedPhone,
                email = state.email,
                company = state.company,
                birthday = state.birthday.ifBlank { null },
                groupId = state.groupId,
                source = "MANUAL",
                notes = state.notes,
                status = state.status,
                lastContactDate = null,
            )

            val id = if (contactId == null) {
                repository.saveContact(contact)
            } else {
                repository.updateContact(contact)
                contactId
            }

            // Save tags
            val tagList = state.tags.split(",").asSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
            repository.updateContactTags(id, tagList)

            onSuccess()
        }
    }
}

data class ContactEditUiState(
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val phone: String = "",
    val email: String = "",
    val company: String = "",
    val birthday: String = "",
    val groupId: Long? = null,
    val notes: String = "",
    val tags: String = "",
    val status: ContactStatus = ContactStatus.ACTIVE
)
