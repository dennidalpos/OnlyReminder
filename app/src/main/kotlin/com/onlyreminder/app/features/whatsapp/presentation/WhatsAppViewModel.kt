package com.onlyreminder.app.features.whatsapp.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.MessageLogEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.domain.model.BirthdayItemStatus
import com.onlyreminder.app.domain.model.MessageStatus
import com.onlyreminder.app.features.birthday.presentation.BirthdayRunItemWithContact
import com.onlyreminder.app.features.whatsapp.data.WhatsAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhatsAppViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl,
    private val contactRepository: ContactRepositoryImpl,
    private val settingsDataStore: com.onlyreminder.app.data.settings.SettingsDataStore,
    private val whatsappRepository: WhatsAppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val runId: Long? = savedStateHandle["runId"]

    val sendMode = settingsDataStore.sendMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        "REMINDER_ONLY"
    )

    private val _queue = MutableStateFlow<List<BirthdayRunItemWithContact>>(emptyList())
    val queue: StateFlow<List<BirthdayRunItemWithContact>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    val currentItem = combine(_queue, _currentIndex) { queue, index ->
        if (queue.isNotEmpty() && index < queue.size) queue[index] else null
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        loadQueue()
    }

    private fun loadQueue() {
        if (runId == null) return
        viewModelScope.launch {
            mainRepository.getItemsForRun(runId).first().let { runItems ->
                val contacts = contactRepository.getAllContacts().first()
                val pendingItems =
                    runItems.filter { it.status == BirthdayItemStatus.PENDING }.map { item ->
                        BirthdayRunItemWithContact(
                            item = item,
                            contact = contacts.find { it.id == item.contactId }
                        )
                    }
                _queue.value = pendingItems
            }
        }
    }

    fun markCurrentAsManualOpened() {
        val current = currentItem.value ?: return
        viewModelScope.launch {
            mainRepository.addRunItem(
                current.item.copy(
                    status = BirthdayItemStatus.SENT_MANUAL,
                    updatedAt = java.time.LocalDateTime.now()
                )
            )
            mainRepository.addLog(
                MessageLogEntity(
                    contactId = current.item.contactId,
                    templateId = null,
                    taskId = null,
                    birthdayRunId = runId,
                    channel = "WHATSAPP_MANUAL",
                    mode = "MANUAL",
                    status = MessageStatus.SENT,
                    errorMessage = null,
                    payloadPreview = current.item.generatedMessagePreview,
                    sentAt = java.time.LocalDateTime.now()
                )
            )
            next()
        }
    }

    fun sendCurrentViaApi() {
        val current = currentItem.value ?: return
        val contact = current.contact ?: return

        viewModelScope.launch {
            val templates = mainRepository.getAllTemplates().first()
            val birthdayTemplate =
                templates.find { it.isDefault && it.name.contains("Birthday", ignoreCase = true) }
                    ?: templates.find { it.name.contains("Birthday", ignoreCase = true) }

            val overrideName = birthdayTemplate?.whatsappApprovedTemplateName

            val success = whatsappRepository.sendMessage(contact, overrideName)

            mainRepository.addRunItem(
                current.item.copy(
                    status = if (success) BirthdayItemStatus.SENT else BirthdayItemStatus.FAILED,
                    updatedAt = java.time.LocalDateTime.now()
                )
            )

            mainRepository.addLog(
                MessageLogEntity(
                    contactId = contact.id,
                    templateId = null,
                    taskId = null,
                    birthdayRunId = runId,
                    channel = "WHATSAPP_API",
                    mode = "API",
                    status = if (success) MessageStatus.SENT else MessageStatus.FAILED,
                    errorMessage = if (success) null else "API Error",
                    payloadPreview = current.item.generatedMessagePreview,
                    sentAt = java.time.LocalDateTime.now()
                )
            )

            next()
        }
    }

    fun skipCurrent() {
        val current = currentItem.value ?: return
        viewModelScope.launch {
            mainRepository.addRunItem(
                current.item.copy(
                    status = BirthdayItemStatus.SKIPPED,
                    updatedAt = java.time.LocalDateTime.now()
                )
            )
            next()
        }
    }

    private fun next() {
        if (_currentIndex.value < _queue.value.size) {
            _currentIndex.value += 1
        }
    }
}
