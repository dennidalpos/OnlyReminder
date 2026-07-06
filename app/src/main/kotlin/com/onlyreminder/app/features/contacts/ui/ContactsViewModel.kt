package com.onlyreminder.app.features.contacts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
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
    private val repository: ContactRepositoryImpl
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId = _selectedGroupId.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag = _selectedTag.asStateFlow()

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

    data class FilterParams(
        val query: String,
        val groupId: Long?,
        val status: String?,
        val tag: String?
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onGroupFilterChanged(groupId: Long?) {
        _selectedGroupId.value = groupId
    }

    fun onStatusFilterChanged(status: String?) {
        _selectedStatus.value = status
    }

    fun onTagFilterChanged(tag: String?) {
        _selectedTag.value = tag
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
