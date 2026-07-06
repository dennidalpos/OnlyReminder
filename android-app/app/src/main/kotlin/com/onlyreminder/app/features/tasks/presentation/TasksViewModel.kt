package com.onlyreminder.app.features.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: MainRepositoryImpl,
    private val taskScheduler: com.onlyreminder.app.core.notifications.TaskScheduler
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    private val _filterStatus = MutableStateFlow<String?>(null)
    val filterStatus: StateFlow<String?> = _filterStatus.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _filterStatus.collectLatest { status ->
                val flow = if (status == null) {
                    repository.getAllTasks()
                } else {
                    repository.getTasksByStatus(status)
                }
                flow.collectLatest {
                    _tasks.value = it
                }
            }
        }
    }

    fun setFilterStatus(status: String?) {
        _filterStatus.value = status
    }

    fun updateTaskStatus(taskId: Long, status: String) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, status)
            val task = repository.getTaskById(taskId)
            if (task != null) {
                if (status == "COMPLETED" || status == "SKIPPED") {
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
