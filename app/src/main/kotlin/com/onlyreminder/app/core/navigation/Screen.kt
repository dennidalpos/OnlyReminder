package com.onlyreminder.app.core.navigation

sealed class Screen(val route: String, val title: String) {
    data object Onboarding : Screen("onboarding", "Onboarding")
    data object Home : Screen("home", "OnlyReminder")
    data object Contacts : Screen("contacts", "Contacts")
    data object ContactDetail : Screen("contact_detail/{contactId}", "Contact Detail") {
        fun createRoute(contactId: Long) = "contact_detail/$contactId"
    }

    data object ContactEdit : Screen("contact_edit?contactId={contactId}", "Edit Contact") {
        fun createRoute(contactId: Long? = null) =
            if (contactId != null) "contact_edit?contactId=$contactId" else "contact_edit"
    }

    data object Import : Screen("import", "Import Contacts")
    data object Groups : Screen("groups", "Groups")
    data object GroupEdit : Screen("group_edit?groupId={groupId}", "Edit Group") {
        fun createRoute(groupId: Long? = null) =
            if (groupId != null) "group_edit?groupId=$groupId" else "group_edit"
    }

    data object Tasks : Screen("tasks", "Tasks")
    data object TaskEdit : Screen("task_edit?taskId={taskId}&contactId={contactId}", "Edit Task") {
        fun createRoute(taskId: Long? = null, contactId: Long? = null): String {
            return buildString {
                append("task_edit")
                val params = mutableListOf<String>()
                if (taskId != null) params.add("taskId=$taskId")
                if (contactId != null) params.add("contactId=$contactId")
                if (params.isNotEmpty()) {
                    append("?")
                    append(params.joinToString("&"))
                }
            }
        }
    }

    data object Templates : Screen("templates", "Message Templates")
    data object TemplateEdit : Screen("template_edit?templateId={templateId}", "Edit Template") {
        fun createRoute(templateId: Long? = null) =
            if (templateId != null) "template_edit?templateId=$templateId" else "template_edit"
    }

    data object BirthdayReview : Screen("birthday_review", "Birthday Review")
    data object WhatsApp : Screen("whatsapp?runId={runId}", "WhatsApp Manual") {
        fun createRoute(runId: Long? = null) =
            if (runId != null) "whatsapp?runId=$runId" else "whatsapp"
    }

    data object WhatsAppApi : Screen("whatsapp_api?runId={runId}", "WhatsApp API") {
        fun createRoute(runId: Long? = null) =
            if (runId != null) "whatsapp_api?runId=$runId" else "whatsapp_api"
    }

    data object Backup : Screen("backup", "Backup & Restore")
    data object Settings : Screen("settings", "Settings")
    data object Security : Screen("security", "Security")
}
