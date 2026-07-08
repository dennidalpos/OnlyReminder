package com.onlyreminder.app.core.security

object SecurityUtils {

    /**
     * Mask sensitive information like phone numbers or emails.
     * Example: "+391234567890" -> "+39*******890"
     */
    fun maskString(input: String, visibleChars: Int = 3): String {
        if (input.length <= visibleChars * 2) return input
        val prefix = input.take(visibleChars)
        val suffix = input.takeLast(visibleChars)
        val maskedLength = input.length - (visibleChars * 2)
        return prefix + "*".repeat(maskedLength) + suffix
    }

    val countryCodes = listOf(
        CountryCode("+39", "Italy"),
        CountryCode("+1", "USA/Canada"),
        CountryCode("+44", "UK"),
        CountryCode("+49", "Germany"),
        CountryCode("+33", "France"),
        CountryCode("+34", "Spain"),
        CountryCode("+41", "Switzerland"),
        CountryCode("+43", "Austria"),
        CountryCode("+32", "Belgium"),
        CountryCode("+31", "Netherlands"),
        CountryCode("+351", "Portugal"),
        CountryCode("+30", "Greece"),
        CountryCode("+46", "Sweden"),
        CountryCode("+47", "Norway"),
        CountryCode("+45", "Denmark"),
        CountryCode("+358", "Finland"),
        CountryCode("+353", "Ireland"),
        CountryCode("+48", "Poland"),
        CountryCode("+420", "Czech Republic"),
        CountryCode("+36", "Hungary"),
        CountryCode("+40", "Romania"),
        CountryCode("+359", "Bulgaria"),
        CountryCode("+385", "Croatia"),
        CountryCode("+386", "Slovenia"),
        CountryCode("+421", "Slovakia"),
        CountryCode("+372", "Estonia"),
        CountryCode("+371", "Latvia"),
        CountryCode("+370", "Lithuania"),
        CountryCode("+352", "Luxembourg"),
        CountryCode("+356", "Malta"),
        CountryCode("+357", "Cyprus")
    ).sortedBy { it.name }
}

data class CountryCode(val code: String, val name: String)
