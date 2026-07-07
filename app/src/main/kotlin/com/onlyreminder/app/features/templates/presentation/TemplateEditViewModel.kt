package com.onlyreminder.app.features.templates.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.TemplateEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.domain.model.ContactStatus
import com.onlyreminder.app.features.templates.domain.TemplateEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateEditViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl,
    private val contactRepository: ContactRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val templateId: Long? = savedStateHandle["id"]
    private val templateEngine = TemplateEngine()

    private val _template = MutableStateFlow<TemplateEntity?>(null)
    val template: StateFlow<TemplateEntity?> = _template.asStateFlow()

    private val _previewText = MutableStateFlow("")
    val previewText: StateFlow<String> = _previewText.asStateFlow()

    private val _isPromotional = MutableStateFlow(false)
    val isPromotional: StateFlow<Boolean> = _isPromotional.asStateFlow()

    private var initialState: TemplateEntity? = null

    val hasChanges: Boolean
        get() = _template.value != initialState

    init {
        viewModelScope.launch {
            val entity = if (templateId != null) {
                mainRepository.getTemplateById(templateId)
            } else {
                TemplateEntity(
                    name = "",
                    language = "EN",
                    channel = "WHATSAPP_MANUAL",
                    body = "",
                    variables = "",
                    isDefault = false,
                    whatsappApprovedTemplateName = null
                )
            }
            _template.value = entity
            initialState = entity
            updatePreview()
        }
    }

    fun updateName(name: String) {
        _template.value = _template.value?.copy(name = name)
    }

    fun updateBody(body: String) {
        _template.value = _template.value?.copy(body = body)
        _isPromotional.value = templateEngine.containsPromotionalKeywords(body)
        updatePreview()
    }

    fun updateChannel(channel: String) {
        _template.value = _template.value?.copy(channel = channel)
    }

    fun updateLanguage(language: String) {
        _template.value = _template.value?.copy(language = language)
    }

    fun updateIsDefault(isDefault: Boolean) {
        _template.value = _template.value?.copy(isDefault = isDefault)
    }

    private fun updatePreview() {
        val currentBody = _template.value?.body ?: ""
        viewModelScope.launch {
            val sampleContact = contactRepository.getAllContacts().first().firstOrNull()
                ?: ContactEntity(
                    firstName = "John",
                    lastName = "Doe",
                    displayName = "John Doe",
                    phone = "+123456789",
                    normalizedPhone = "+123456789",
                    email = "",
                    company = "",
                    birthday = null,
                    groupId = null,
                    source = "MANUAL",
                    notes = "",
                    status = ContactStatus.ACTIVE,
                    lastContactDate = null,
                    marketingConsent = false,
                    privacyConsent = false
                )

            _previewText.value = templateEngine.render(currentBody, sampleContact)
        }
    }

    fun saveTemplate(onSuccess: () -> Unit) {
        val currentTemplate = _template.value ?: return
        if (currentTemplate.name.isBlank()) return

        viewModelScope.launch {
            mainRepository.saveTemplate(currentTemplate)
            onSuccess()
        }
    }
}
