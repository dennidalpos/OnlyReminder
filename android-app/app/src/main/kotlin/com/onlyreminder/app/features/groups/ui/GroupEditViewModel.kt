package com.onlyreminder.app.features.groups.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupEditViewModel @Inject constructor(
    private val repository: ContactRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: Long? = savedStateHandle.get<String>("groupId")?.toLongOrNull()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

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
