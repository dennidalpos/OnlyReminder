package com.onlyreminder.app.features.groups.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupEditViewModel @Inject constructor(
    private val repository: ContactRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: Long? = savedStateHandle["id"]

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    val members: StateFlow<List<ContactEntity>> = if (groupId != null) {
        repository.searchContacts(null, groupId, null)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        MutableStateFlow(emptyList())
    }

    val availableContacts: StateFlow<List<ContactEntity>> = repository.getAllContacts()
        .map { list -> list.filter { it.groupId == null } } // Only contacts without a group
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        if (groupId != null) {
            loadGroup(groupId)
        }
    }

    private fun loadGroup(id: Long) {
        viewModelScope.launch {
            repository.getGroupById(id)?.let { group ->
                _name.value = group.name
                _description.value = group.description
            }
        }
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
    }

    fun addContactToGroup(contact: ContactEntity) {
        val gid = groupId ?: return
        viewModelScope.launch {
            repository.saveContact(contact.copy(groupId = gid))
        }
    }

    fun removeContactFromGroup(contact: ContactEntity) {
        viewModelScope.launch {
            repository.saveContact(contact.copy(groupId = null))
        }
    }

    fun saveGroup(onSuccess: () -> Unit) {
        if (_name.value.isBlank()) return

        viewModelScope.launch {
            val group = GroupEntity(
                id = groupId ?: 0,
                name = _name.value,
                description = _description.value,
                color = 0 // Default color
            )
            repository.saveGroup(group)
            onSuccess()
        }
    }
}
