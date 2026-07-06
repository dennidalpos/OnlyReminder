package com.onlyreminder.app.features.templates.domain

import com.onlyreminder.app.data.database.entities.ContactEntity
import com.onlyreminder.app.data.database.entities.CustomFieldEntity

class TemplateEngine {

    fun render(
        templateBody: String,
        contact: ContactEntity,
        customFields: List<CustomFieldEntity> = emptyList()
    ): String {
        var rendered = templateBody

        rendered = rendered.replace("{first_name}", contact.firstName)
        rendered = rendered.replace("{last_name}", contact.lastName)
        rendered = rendered.replace("{full_name}", contact.displayName)
        rendered = rendered.replace("{company}", contact.company)
        rendered = rendered.replace("{birthday}", contact.birthday ?: "")

        customFields.forEach { field ->
            rendered = rendered.replace("{custom.${field.fieldName}}", field.fieldValue)
        }

        return rendered
    }

    fun getVariables(templateBody: String): List<String> {
        val regex = Regex("\\{([^}]+)\\}")
        return regex.findAll(templateBody).map { it.groupValues[1] }.toList()
    }

    fun containsPromotionalKeywords(templateBody: String): Boolean {
        val keywords = listOf("promo", "offerta", "sconto", "discount", "deal", "http", "https")
        val lowerBody = templateBody.lowercase()
        return keywords.any { lowerBody.contains(it) }
    }
}
