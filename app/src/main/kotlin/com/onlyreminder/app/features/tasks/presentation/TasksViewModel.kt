package com.onlyreminder.app.features.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiModel(
    val task: TaskEntity,
    val group: GroupEntity? = null,
    val contact: ContactEntity? = null,
    val groupContacts: List<ContactEntity> = emptyList(),
    val isExpanded: Boolean = false,
    val isGroupExpanded: Boolean = false
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: MainRepositoryImpl,
    private val contactRepository: ContactRepositoryImpl,
    private val taskScheduler: com.onlyreminder.app.core.notifications.TaskScheduler
) : ViewModel() {

    private val _taskUiModels = MutableStateFlow<List<TaskUiModel>>(emptyList())
    val taskUiModels: StateFlow<List<TaskUiModel>> = _taskUiModels.asStateFlow()

    private val _selectedTaskIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTaskIds = _selectedTaskIds.asStateFlow()

    private val _filterStatus = MutableStateFlow<TaskStatus?>(null)
    val filterStatus: StateFlow<TaskStatus?> = _filterStatus.asStateFlow()

    private val _expandedTaskIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _expandedGroupTaskIds = MutableStateFlow<Set<Long>>(emptySet())

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _filterStatus.collectLatest { status ->
                val tasksFlow = if (status == null) repository.getAllTasks() else repository.getTasksByStatus(status)
                
                combine(
                    tasksFlow,
                    _expandedTaskIds,
                    _expandedGroupTaskIds,
                    contactRepository.getAllGroups(),
                    contactRepository.getAllContacts()
                ) { tasks, expandedTasks, expandedGroups, groups, contacts ->
                    tasks.map { task ->
                        val group = groups.find { it.id == task.groupId }
                        val contact = contacts.find { it.id == task.contactId }
                        
                        val groupContacts = if (expandedGroups.contains(task.id) && group != null) {
                            contacts.filter { it.groupId == group.id }
                        } else emptyList()

                        TaskUiModel(
                            task = task,
                            group = group,
                            contact = contact,
                            groupContacts = groupContacts,
                            isExpanded = expandedTasks.contains(task.id),
                            isGroupExpanded = expandedGroups.contains(task.id)
                        )
                    }
                }.collectLatest {
                    _taskUiModels.value = it
                }
            }
        }
    }

    fun setFilterStatus(status: TaskStatus?) {
        _filterStatus.value = status
    }

    fun toggleTaskSelection(taskId: Long) {
        val current = _selectedTaskIds.value
        if (current.contains(taskId)) {
            _selectedTaskIds.value = current - taskId
        } else {
            _selectedTaskIds.value = current + taskId
        }
    }

    fun toggleTaskExpansion(taskId: Long) {
        val current = _expandedTaskIds.value
        _expandedTaskIds.value = if (current.contains(taskId)) current - taskId else current + taskId
    }

    fun toggleGroupExpansion(taskId: Long) {
        val current = _expandedGroupTaskIds.value
        _expandedGroupTaskIds.value = if (current.contains(taskId)) current - taskId else current + taskId
    }

    fun selectAllTasks() {
        _selectedTaskIds.value = _taskUiModels.value.map { it.task.id }.toSet()
    }

    fun clearSelection() {
        _selectedTaskIds.value = emptySet()
    }

    fun deleteSelectedTasks() {
        viewModelScope.launch {
            val idsToDelete = _selectedTaskIds.value
            _taskUiModels.value.filter { it.task.id in idsToDelete }.forEach {
                taskScheduler.cancelTask(it.task)
                repository.deleteTask(it.task)
            }
            clearSelection()
        }
    }

    fun updateTaskStatus(taskId: Long, status: TaskStatus) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, status)
            val task = repository.getTaskById(taskId)
            if (task != null) {
                if (status == TaskStatus.COMPLETED) {
                    taskScheduler.cancelTask(task)
                }
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskScheduler.cancelTask(task)
            repository.deleteTask(task)
        }
    }
}
