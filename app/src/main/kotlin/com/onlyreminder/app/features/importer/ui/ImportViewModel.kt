package com.onlyreminder.app.features.importer.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyreminder.app.BuildConfig
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.UiText
import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.repository.ContactRepositoryImpl
import com.onlyreminder.app.domain.model.ContactStatus
import com.onlyreminder.app.features.importer.data.CsvParser
import com.onlyreminder.app.features.importer.data.JsonParser
import com.onlyreminder.app.features.importer.data.XlsxParser
import com.onlyreminder.app.features.importer.data.XmlParser
import com.onlyreminder.app.features.importer.domain.ContactField
import com.onlyreminder.app.features.importer.domain.ImportContact
import com.onlyreminder.app.features.importer.domain.ImportError
import com.onlyreminder.app.features.importer.domain.ImportReport
import com.onlyreminder.app.features.importer.domain.RawImportRow
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ContactRepositoryImpl,
    private val backupManager: com.onlyreminder.app.features.backup.domain.BackupManager,
    private val settingsDataStore: com.onlyreminder.app.data.settings.SettingsDataStore
) : ViewModel() {

    private val csvParser = CsvParser()
    private val jsonParser = JsonParser()
    private val xmlParser = XmlParser()
    private val xlsxParser = XlsxParser()

    sealed class ImportStep {
        object SelectFile : ImportStep()
        data class PreviewAndMapping(val rows: List<RawImportRow>, val fileType: String) :
            ImportStep()

        data class ValidationAndDeduplication(val contacts: List<ImportContact>) : ImportStep()
        data class Summary(val report: ImportReport) : ImportStep()
        object Success : ImportStep()
    }

    private val _currentStep = MutableStateFlow<ImportStep>(ImportStep.SelectFile)
    val currentStep = _currentStep.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<UiText?>(null)
    val error = _error.asStateFlow()

    fun importFromSystem() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val normalize = settingsDataStore.normalizePhone.first()
                val countryCode = settingsDataStore.defaultCountryCode.first()
                
                val contacts = withContext(Dispatchers.IO) {
                    val systemContacts = mutableListOf<ImportContact>()
                    val contentResolver = context.contentResolver
                    val cursor = contentResolver.query(
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null
                    )
                    cursor?.use {
                        val nameIdx = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val phoneIdx = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val idIdx = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

                        var count = 0
                        while (it.moveToNext()) {
                            if (BuildConfig.FLAVOR == "demo" && count >= 10) break
                            
                            val name = it.getString(nameIdx) ?: ""
                            val phone = it.getString(phoneIdx) ?: ""
                            val contactId = it.getString(idIdx)
                            
                            // ... existing code ...
                            var birthday: String? = null
                            val bDayCursor = contentResolver.query(
                                android.provider.ContactsContract.Data.CONTENT_URI,
                                arrayOf(android.provider.ContactsContract.CommonDataKinds.Event.START_DATE),
                                "${android.provider.ContactsContract.Data.CONTACT_ID} = ? AND ${android.provider.ContactsContract.Data.MIMETYPE} = ? AND ${android.provider.ContactsContract.CommonDataKinds.Event.TYPE} = ?",
                                arrayOf(contactId, android.provider.ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, android.provider.ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString()),
                                null
                            )
                            bDayCursor?.use { bc ->
                                if (bc.moveToFirst()) {
                                    birthday = bc.getString(0)
                                }
                            }

                            systemContacts.add(
                                validateAndNormalize(
                                    ImportContact(
                                        displayName = name,
                                        phone = phone,
                                        birthday = birthday,
                                        source = "System Contacts"
                                    ),
                                    normalize,
                                    countryCode
                                )
                            )
                            count++
                        }
                    }
                    systemContacts
                }
                
                val finalContacts = detectDuplicates(contacts)
                _currentStep.value = ImportStep.ValidationAndDeduplication(finalContacts)
            } catch (e: Exception) {
                _error.value = UiText.StringResource(R.string.error_reading_contacts, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadFile(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                withContext(Dispatchers.IO) {
                    val contentResolver = context.contentResolver
                    val type = contentResolver.getType(uri) ?: ""
                    val fileName = getFileName(uri) ?: ""

                    val extension = fileName.substringAfterLast(".", "").lowercase()

                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val rows = when {
                            extension == "csv" || type.contains("csv") -> csvParser.parse(
                                inputStream
                            )

                            extension == "json" || type.contains("json") -> jsonParser.parse(
                                inputStream
                            )

                            extension == "xml" || type.contains("xml") -> xmlParser.parse(
                                inputStream
                            )

                            extension == "xlsx" || type.contains("spreadsheetml") -> xlsxParser.parse(
                                inputStream
                            )

                            else -> csvParser.parse(inputStream) // Fallback to CSV
                        }

                        if (rows.isNotEmpty()) {
                            // Demo limit check
                            val finalRows = if (BuildConfig.FLAVOR == "demo") {
                                rows.take(11) // Header + 10 rows
                            } else {
                                rows
                            }
                            _currentStep.value =
                                ImportStep.PreviewAndMapping(finalRows, extension.ifBlank { "csv" })
                        } else {
                            _error.value = UiText.StringResource(R.string.file_empty)
                        }
                    } ?: run {
                        _error.value = UiText.StringResource(R.string.error_opening_file)
                    }
                }
            } catch (e: Exception) {
                _error.value = UiText.StringResource(R.string.error_reading_file, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }

    fun processMapping(rows: List<RawImportRow>, mapping: Map<Int, ContactField?>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val normalize = settingsDataStore.normalizePhone.first()
                val countryCode = settingsDataStore.defaultCountryCode.first()
                
                val contacts = withContext(Dispatchers.Default) {
                    val header = rows.firstOrNull()
                    val dataRows = if (header != null) rows.drop(1) else emptyList()

                    dataRows.map { row ->
                        var importContact = ImportContact()
                        mapping.forEach { (colIdx, field) ->
                            if (field != null && colIdx < row.data.size) {
                                val value = row.data[colIdx]
                                importContact = when (field) {
                                    ContactField.FIRST_NAME -> importContact.copy(firstName = value)
                                    ContactField.LAST_NAME -> importContact.copy(lastName = value)
                                    ContactField.DISPLAY_NAME -> importContact.copy(displayName = value)
                                    ContactField.PHONE -> importContact.copy(phone = value)
                                    ContactField.EMAIL -> importContact.copy(email = value)
                                    ContactField.COMPANY -> importContact.copy(company = value)
                                    ContactField.BIRTHDAY -> importContact.copy(birthday = value)
                                    ContactField.TAGS -> importContact.copy(
                                        tags = value.split(",").map { it.trim() }
                                            .filter { it.isNotEmpty() })

                                    ContactField.GROUP -> importContact.copy(group = value)
                                    ContactField.SOURCE -> importContact.copy(source = value)
                                    ContactField.NOTES -> importContact.copy(notes = value)
                                    ContactField.STATUS -> importContact.copy(
                                        status = try {
                                            ContactStatus.valueOf(value.uppercase())
                                        } catch (_: Exception) {
                                            ContactStatus.ACTIVE
                                        }
                                    )

                                    ContactField.IGNORE -> importContact
                                }
                            }
                        }

                        // Default Display Name if empty
                        if (importContact.displayName.isBlank()) {
                            importContact =
                                importContact.copy(displayName = "${importContact.firstName} ${importContact.lastName}".trim())
                        }

                        // Normalization & Validation
                        validateAndNormalize(importContact, normalize, countryCode)
                    }
                }

                // Deduplication
                val finalContacts = detectDuplicates(contacts)
                _currentStep.value = ImportStep.ValidationAndDeduplication(finalContacts)
            } catch (e: Exception) {
                _error.value =
                    UiText.StringResource(R.string.error_processing_data, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateAndNormalize(
        contact: ImportContact,
        normalize: Boolean,
        countryCode: String
    ): ImportContact {
        val errors = mutableListOf<ImportError>()
        val finalPhone = if (normalize) normalizePhone(contact.phone, countryCode) else contact.phone

        if (contact.displayName.isBlank()) errors.add(ImportError.MISSING_DISPLAY_NAME)
        if (contact.phone.isBlank()) errors.add(ImportError.MISSING_PHONE_NUMBER)

        return contact.copy(
            phone = finalPhone,
            isValid = errors.isEmpty(),
            validationErrors = errors
        )
    }

    private fun normalizePhone(phone: String, defaultCode: String): String {
        var clean = phone.replace(Regex("[\\s\\-()]"), "")
        if (clean.startsWith("00")) {
            clean = "+" + clean.substring(2)
        }
        if (!clean.startsWith("+") && clean.isNotEmpty()) {
            clean = "$defaultCode$clean"
        }
        return clean
    }

    private suspend fun detectDuplicates(contacts: List<ImportContact>): List<ImportContact> {
        val existingContacts = repository.getAllContacts().first()
        return contacts.map { importContact ->
            val duplicate = existingContacts.find {
                it.normalizedPhone == importContact.phone ||
                        (it.email.isNotEmpty() && it.email == importContact.email) ||
                        (it.firstName == importContact.firstName && it.lastName == importContact.lastName && it.company == importContact.company)
            }
            if (duplicate != null) {
                importContact.copy(
                    isDuplicate = true,
                    duplicateReason = "Already exists: ${duplicate.displayName}"
                )
            } else {
                importContact
            }
        }
    }

    fun confirmImport(contacts: List<ImportContact>, skipDuplicates: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Backup pre-import (Step 14)
                backupManager.createBackup("auto_pre_import")

                withContext(Dispatchers.IO) {
                    contacts.forEach { importContact ->
                        if (importContact.isValid && (!importContact.isDuplicate || !skipDuplicates)) {
                            val entity = ContactEntity(
                                firstName = importContact.firstName,
                                lastName = importContact.lastName,
                                displayName = importContact.displayName,
                                phone = importContact.phone,
                                normalizedPhone = importContact.phone, // Already normalized in ViewModel
                                email = importContact.email,
                                company = importContact.company,
                                birthday = importContact.birthday,
                                groupId = null, // Logic to map group name to ID could be added
                                source = importContact.source.ifBlank { "CSV Import" },
                                notes = importContact.notes,
                                status = importContact.status,
                                lastContactDate = null
                            )
                            val contactId = repository.saveContact(entity)
                            repository.updateContactTags(contactId, importContact.tags)
                        }
                    }
                }
                _currentStep.value = ImportStep.Success
            } catch (e: Exception) {
                _error.value =
                    UiText.StringResource(R.string.error_saving_contacts, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reset() {
        _currentStep.value = ImportStep.SelectFile
        _error.value = null
    }
}
