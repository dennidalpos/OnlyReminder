package com.onlyreminder.app.features.contacts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.domain.model.ContactStatus
import com.onlyreminder.app.domain.model.TaskStatus
import java.time.LocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: ContactRepositoryImpl,
    private val mainRepository: com.onlyreminder.app.data.repository.MainRepositoryImpl,
    private val taskScheduler: com.onlyreminder.app.core.notifications.TaskScheduler
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId = _selectedGroupId.asStateFlow()

    private val _selectedStatus = MutableStateFlow<ContactStatus?>(null)
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag = _selectedTag.asStateFlow()

    private val _selectedContactIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedContactIds = _selectedContactIds.asStateFlow()

    val groups: StateFlow<List<GroupEntity>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tags: StateFlow<List<TagEntity>> = repository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val contacts: StateFlow<List<ContactEntity>> = combine(
        _searchQuery,
        _selectedGroupId,
        _selectedStatus,
        _selectedTag
    ) { query, groupId, status, tag ->
        FilterParams(query, groupId, status, tag)
    }.flatMapLatest { params ->
        if (params.tag != null) {
            repository.getContactsByTag(params.tag).map { list ->
                list.filter {
                    (params.query.isEmpty() || it.displayName.contains(
                        params.query,
                        ignoreCase = true
                    ) || it.phone.contains(params.query)) &&
                            (params.groupId == null || it.groupId == params.groupId) &&
                            (params.status == null || it.status == params.status)
                }
            }
        } else {
            repository.searchContacts(
                query = if (params.query.isEmpty()) null else params.query,
                groupId = params.groupId,
                status = params.status
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class ContactUiModel(
        val contact: ContactEntity,
        val hasActiveTasks: Boolean = false
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactUiModels: StateFlow<List<ContactUiModel>> = combine(
        contacts,
        mainRepository.getAllTasks()
    ) { contactList, taskList ->
        contactList.map { contact ->
            ContactUiModel(
                contact = contact,
                hasActiveTasks = taskList.any { it.contactId == contact.id && it.status == TaskStatus.PENDING }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class FilterParams(
        val query: String,
        val groupId: Long?,
        val status: ContactStatus?,
        val tag: String?
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onGroupFilterChanged(groupId: Long?) {
        _selectedGroupId.value = groupId
    }

    fun onStatusFilterChanged(status: ContactStatus?) {
        _selectedStatus.value = status
    }

    fun onTagFilterChanged(tag: String?) {
        _selectedTag.value = tag
    }

    fun toggleContactSelection(contactId: Long) {
        val current = _selectedContactIds.value
        if (current.contains(contactId)) {
            _selectedContactIds.value = current - contactId
        } else {
            _selectedContactIds.value = current + contactId
        }
    }

    fun selectAllContacts() {
        _selectedContactIds.value = contacts.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedContactIds.value = emptySet()
    }

    fun deleteSelectedContacts() {
        viewModelScope.launch {
            val idsToDelete = _selectedContactIds.value
            contacts.value.filter { it.id in idsToDelete }.forEach {
                repository.hardDeleteContact(it)
            }
            clearSelection()
        }
    }

    fun assignSelectedToGroup(groupId: Long?) {
        viewModelScope.launch {
            val idsToUpdate = _selectedContactIds.value
            idsToUpdate.forEach { id ->
                repository.getContactById(id)?.let { contact ->
                    repository.saveContact(contact.copy(groupId = groupId, updatedAt = LocalDateTime.now()))
                }
            }
            clearSelection()
        }
    }

    fun assignTaskToSelected(title: String, description: String, dueDateTime: LocalDateTime) {
        viewModelScope.launch {
            val ids = _selectedContactIds.value
            ids.forEach { contactId ->
                val task = TaskEntity(
                    title = title,
                    description = description,
                    contactId = contactId,
                    groupId = null,
                    type = "REMINDER",
                    dueDateTime = dueDateTime,
                    repeatRule = null,
                    priority = 1,
                    status = TaskStatus.PENDING,
                    templateId = null,
                    sendMode = "REMINDER_ONLY"
                )
                val savedId = mainRepository.saveTask(task)
                taskScheduler.scheduleTask(task.copy(id = savedId))
            }
            clearSelection()
        }
    }

    fun archiveContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.archiveContact(contact.id)
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.hardDeleteContact(contact)
        }
    }
}
