package com.onlyreminder.app.features.groups.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repository: ContactRepositoryImpl
) : ViewModel() {

    val groups: StateFlow<List<GroupEntity>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedGroupIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedGroupIds = _selectedGroupIds.asStateFlow()

    fun toggleGroupSelection(groupId: Long) {
        val current = _selectedGroupIds.value
        if (current.contains(groupId)) {
            _selectedGroupIds.value = current - groupId
        } else {
            _selectedGroupIds.value = current + groupId
        }
    }

    fun selectAllGroups() {
        _selectedGroupIds.value = groups.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedGroupIds.value = emptySet()
    }

    fun deleteSelectedGroups() {
        viewModelScope.launch {
            val idsToDelete = _selectedGroupIds.value
            groups.value.filter { it.id in idsToDelete }.forEach {
                repository.deleteGroup(it)
            }
            clearSelection()
        }
    }

    fun deleteGroup(group: GroupEntity) {
        viewModelScope.launch {
            repository.deleteGroup(group)
        }
    }
}
