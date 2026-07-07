package com.onlyreminder.app.features.importer.domain

import com.onlyreminder.app.domain.model.ContactStatus

data class RawImportRow(val index: Int, val data: List<String>)

data class ImportContact(
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val phone: String = "",
    val email: String = "",
    val company: String = "",
    val birthday: String? = null,
    val tags: List<String> = emptyList(),
    val group: String = "",
    val source: String = "",
    val notes: String = "",
    val status: ContactStatus = ContactStatus.ACTIVE,
    val marketingConsent: Boolean = false,
    val privacyConsent: Boolean = false,
    val isSelected: Boolean = true,
    val isValid: Boolean = true,
    val validationErrors: List<String> = emptyList(),
    val isDuplicate: Boolean = false,
    val duplicateReason: String? = null
)

enum class ContactField(val displayName: String) {
    FIRST_NAME("First Name"),
    LAST_NAME("Last Name"),
    DISPLAY_NAME("Display Name"),
    PHONE("Phone"),
    EMAIL("Email"),
    COMPANY("Company"),
    BIRTHDAY("Birthday"),
    TAGS("Tags"),
    GROUP("Group"),
    SOURCE("Source"),
    NOTES("Notes"),
    STATUS("Status"),
    MARKETING_CONSENT("Marketing Consent"),
    PRIVACY_CONSENT("Privacy Consent"),
    IGNORE("Ignore")
}

data class ImportReport(val total: Int, val success: Int, val failed: Int)
