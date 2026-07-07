package com.onlyreminder.app.features.birthday.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.BirthdayRunEntity
import com.onlyreminder.app.data.database.entities.BirthdayRunItemEntity
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.domain.model.BirthdayItemStatus
import com.onlyreminder.app.domain.model.BirthdayRunStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirthdayReviewViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl,
    private val contactRepository: ContactRepositoryImpl
) : ViewModel() {

    private val _latestRun = MutableStateFlow<BirthdayRunEntity?>(null)
    val latestRun: StateFlow<BirthdayRunEntity?> = _latestRun.asStateFlow()

    private val _items = MutableStateFlow<List<BirthdayRunItemWithContact>>(emptyList())
    val items: StateFlow<List<BirthdayRunItemWithContact>> = _items.asStateFlow()

    init {
        loadLatestRun()
    }

    private fun loadLatestRun() {
        viewModelScope.launch {
            mainRepository.getAllBirthdayRuns().collectLatest { runs ->
                val latest = runs.firstOrNull { it.status == BirthdayRunStatus.PENDING }
                    ?: runs.firstOrNull()
                _latestRun.value = latest

                if (latest != null) {
                    mainRepository.getItemsForRun(latest.id).collectLatest { runItems ->
                        val contacts = contactRepository.getAllContacts().first()
                        _items.value = runItems.map { item ->
                            BirthdayRunItemWithContact(
                                item = item,
                                contact = contacts.find { it.id == item.contactId }
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateItemStatus(itemId: Long, status: BirthdayItemStatus) {
        viewModelScope.launch {
            val item = _items.value.find { it.item.id == itemId }?.item ?: return@launch
            mainRepository.addRunItem(
                item.copy(
                    status = status,
                    updatedAt = java.time.LocalDateTime.now()
                )
            )
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            contactRepository.hardDeleteContact(contact)
        }
    }
}

data class BirthdayRunItemWithContact(
    val item: BirthdayRunItemEntity,
    val contact: ContactEntity?
)
