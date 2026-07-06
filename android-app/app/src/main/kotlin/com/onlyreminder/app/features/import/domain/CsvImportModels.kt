package com.onlyreminder.app.features.import.domain

data class RawImportRow(
    val index: Int,
    val data: List<String>
)

enum class ContactField(val displayName: String, val isRequired: Boolean = false) {
    FIRST_NAME("First Name", false),
    LAST_NAME("Last Name", false),
    DISPLAY_NAME("Display Name", true),
    PHONE("Phone", true),
    EMAIL("Email", false),
    COMPANY("Company", false),
    BIRTHDAY("Birthday (YYYY-MM-DD)", false),
    TAGS("Tags (comma separated)", false),
    GROUP("Group", false),
    SOURCE("Source", false),
    NOTES("Notes", false),
    STATUS("Status", false),
    MARKETING_CONSENT("Marketing Consent", false),
    PRIVACY_CONSENT("Privacy Consent", false)
}

data class ColumnMapping(
    val csvColumnIndex: Int,
    val contactField: ContactField?
)

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
    val status: String = "ACTIVE",
    val marketingConsent: Boolean = false,
    val privacyConsent: Boolean = false,
    val isValid: Boolean = true,
    val validationErrors: List<String> = emptyList(),
    val isDuplicate: Boolean = false,
    val duplicateReason: String? = null
)

data class ImportReport(
    val totalRows: Int = 0,
    val validContacts: Int = 0,
    val invalidContacts: Int = 0,
    val duplicatesFound: Int = 0,
    val processedContacts: List<ImportContact> = emptyList()
)
