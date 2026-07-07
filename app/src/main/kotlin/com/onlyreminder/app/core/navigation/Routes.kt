package com.onlyreminder.app.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Contacts : Route

    @Serializable
    data class ContactDetail(val id: Long) : Route

    @Serializable
    data class ContactEdit(val id: Long? = null) : Route

    @Serializable
    data object Import : Route

    @Serializable
    data object Groups : Route

    @Serializable
    data class GroupEdit(val id: Long? = null) : Route

    @Serializable
    data object Tasks : Route

    @Serializable
    data class TaskEdit(val id: Long? = null, val contactId: Long? = null) : Route

    @Serializable
    data object Templates : Route

    @Serializable
    data class TemplateEdit(val id: Long? = null) : Route

    @Serializable
    data object BirthdayReview : Route

    @Serializable
    data class WhatsApp(val runId: Long) : Route

    @Serializable
    data class Report(val runId: Long) : Route

    @Serializable
    data object WhatsAppApi : Route

    @Serializable
    data object Backup : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Security : Route
}
