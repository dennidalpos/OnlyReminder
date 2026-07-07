package com.onlyreminder.app.features.templates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.data.database.entities.TemplateEntity
import com.onlyreminder.app.data.repository.MainRepositoryImpl
import com.onlyreminder.app.features.templates.domain.TemplateEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val repository: MainRepositoryImpl
) : ViewModel() {

    private val templateEngine = TemplateEngine()

    private val _templates = MutableStateFlow<List<TemplateEntity>>(emptyList())
    val templates: StateFlow<List<TemplateEntity>> = _templates.asStateFlow()

    private val _selectedTemplateIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTemplateIds = _selectedTemplateIds.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultTemplates()
            repository.getAllTemplates().collectLatest {
                _templates.value = it
            }
        }
    }

    fun deleteTemplate(template: TemplateEntity) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
        }
    }

    fun toggleTemplateSelection(templateId: Long) {
        val current = _selectedTemplateIds.value
        if (current.contains(templateId)) {
            _selectedTemplateIds.value = current - templateId
        } else {
            _selectedTemplateIds.value = current + templateId
        }
    }

    fun selectAllTemplates() {
        _selectedTemplateIds.value = templates.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedTemplateIds.value = emptySet()
    }

    fun deleteSelectedTemplates() {
        viewModelScope.launch {
            val idsToDelete = _selectedTemplateIds.value
            templates.value.filter { it.id in idsToDelete }.forEach {
                repository.deleteTemplate(it)
            }
            clearSelection()
        }
    }

    fun duplicateTemplate(template: TemplateEntity) {
        viewModelScope.launch {
            repository.duplicateTemplate(template)
        }
    }

    fun saveTemplate(template: TemplateEntity) {
        viewModelScope.launch {
            repository.saveTemplate(template)
        }
    }

    fun isPromotional(body: String): Boolean {
        return templateEngine.containsPromotionalKeywords(body)
    }
}
