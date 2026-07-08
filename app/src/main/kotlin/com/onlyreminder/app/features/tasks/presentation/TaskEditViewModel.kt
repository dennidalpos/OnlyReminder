package com.onlyreminder.app.features.tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.TaskEntity
import com.onlyreminder.app.data.database.entities.TemplateEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl,
    private val contactRepository: ContactRepositoryImpl,
    private val taskScheduler: com.onlyreminder.app.core.notifications.TaskScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long? = savedStateHandle["id"]
    private val initialContactId: Long? = savedStateHandle["contactId"]
    private val initialGroupId: Long? = savedStateHandle["groupId"]

    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task: StateFlow<TaskEntity?> = _task.asStateFlow()

    private val _contacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    val contacts: StateFlow<List<ContactEntity>> = _contacts.asStateFlow()

    private val _groups = MutableStateFlow<List<GroupEntity>>(emptyList())
    val groups: StateFlow<List<GroupEntity>> = _groups.asStateFlow()

    private val _templates = MutableStateFlow<List<TemplateEntity>>(emptyList())
    val templates: StateFlow<List<TemplateEntity>> = _templates.asStateFlow()

    private var initialState: TaskEntity? = null

    val hasChanges: Boolean
        get() = _task.value != initialState

    init {
        viewModelScope.launch {
            _contacts.value = contactRepository.getAllContacts().first()
            _groups.value = contactRepository.getAllGroups().first()
            _templates.value = mainRepository.getAllTemplates().first()

            val entity = if (taskId != null) {
                mainRepository.getTaskById(taskId)
            } else {
                TaskEntity(
                    title = "",
                    description = "",
                    contactId = initialContactId,
                    groupId = initialGroupId,
                    type = "REMINDER",
                    dueDateTime = LocalDateTime.now().plusHours(1),
                    repeatRule = null,
                    priority = 1,
                    status = TaskStatus.PENDING,
                    templateId = null,
                    sendMode = "REMINDER_ONLY"
                )
            }
            _task.value = entity
            initialState = entity
        }
    }

    fun updateTitle(title: String) {
        _task.value = _task.value?.copy(title = title)
    }

    fun updateDescription(description: String) {
        _task.value = _task.value?.copy(description = description)
    }

    fun updateContact(contactId: Long?) {
        _task.value = _task.value?.copy(contactId = contactId, groupId = null)
    }

    fun updateGroup(groupId: Long?) {
        _task.value = _task.value?.copy(groupId = groupId, contactId = null)
    }

    fun updateType(type: String) {
        _task.value = _task.value?.copy(type = type)
    }

    fun updateTemplate(templateId: Long?) {
        _task.value = _task.value?.copy(templateId = templateId)
    }

    fun updateDueDateTime(dateTimeMillis: Long) {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(dateTimeMillis),
            ZoneId.systemDefault()
        )
        _task.value = _task.value?.copy(dueDateTime = dateTime)
    }

    fun updatePriority(priority: Int) {
        _task.value = _task.value?.copy(priority = priority)
    }

    fun updateSendMode(sendMode: String) {
        _task.value = _task.value?.copy(sendMode = sendMode)
    }

    fun saveTask(onSuccess: () -> Unit) {
        val currentTask = _task.value ?: return
        if (currentTask.title.isBlank()) return

        viewModelScope.launch {
            val savedId = mainRepository.saveTask(currentTask)
            val savedTask = currentTask.copy(id = savedId)
            taskScheduler.scheduleTask(savedTask)
            onSuccess()
        }
    }
}
