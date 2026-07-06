package com.onlyreminder.app.features.whatsapp.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.BirthdayRunItemEntity
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.MessageLogEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.features.birthday.presentation.BirthdayRunItemWithContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhatsAppViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl,
    private val contactRepository: ContactRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val runId: Long? = savedStateHandle.get<String>("runId")?.toLongOrNull()

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
                val pendingItems = runItems.filter { it.status == "PENDING" }.map { item ->
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
                    status = "manual_opened",
                    updatedAt = System.currentTimeMillis()
                )
            )
            mainRepository.addLog(
                MessageLogEntity(
                    contactId = current.item.contactId,
                    templateId = null, // Should ideally store template id
                    channel = "WHATSAPP_MANUAL",
                    status = "MANUAL_OPENED",
                    body = current.item.generatedMessagePreview
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
                    status = "SKIPPED",
                    updatedAt = System.currentTimeMillis()
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
