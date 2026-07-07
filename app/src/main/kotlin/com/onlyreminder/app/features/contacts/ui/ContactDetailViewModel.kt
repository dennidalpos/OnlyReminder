package com.onlyreminder.app.features.contacts.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.GroupEntity
import com.onlyreminder.app.data.database.entities.TagEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.features.whatsapp.domain.WhatsAppManualManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val repository: ContactRepositoryImpl,
    private val whatsappManager: WhatsAppManualManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId: Long = checkNotNull(savedStateHandle["id"])

    private val _contact = MutableStateFlow<ContactEntity?>(null)
    val contact = _contact.asStateFlow()

    private val _group = MutableStateFlow<GroupEntity?>(null)
    val group = _group.asStateFlow()

    private val _tags = MutableStateFlow<List<TagEntity>>(emptyList())
    val tags = _tags.asStateFlow()

    init {
        loadContact()
    }

    private fun loadContact() {
        viewModelScope.launch {
            val contactEntity = repository.getContactById(contactId)
            _contact.value = contactEntity
            contactEntity?.groupId?.let {
                _group.value = repository.getGroupById(it)
            }
            repository.getTagsForContact(contactId).collect {
                _tags.value = it
            }
        }
    }

    fun archiveContact() {
        viewModelScope.launch {
            repository.archiveContact(contactId)
            loadContact()
        }
    }

    fun restoreContact() {
        viewModelScope.launch {
            repository.restoreContact(contactId)
            loadContact()
        }
    }

    fun deleteContact(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _contact.value?.let {
                repository.hardDeleteContact(it)
                onDeleted()
            }
        }
    }

    fun openWhatsApp(context: android.content.Context) {
        val c = _contact.value ?: return
        whatsappManager.openWhatsAppChat(context, c.phone, "Hi ${c.firstName}, how are you?")
    }
}
