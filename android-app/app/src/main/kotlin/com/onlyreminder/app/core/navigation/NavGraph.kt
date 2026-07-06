package com.onlyreminder.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.onlyreminder.app.features.backup.ui.BackupScreen
import com.onlyreminder.app.features.birthday.ui.BirthdayReviewScreen
import com.onlyreminder.app.features.contacts.ui.ContactDetailScreen
import com.onlyreminder.app.features.contacts.ui.ContactEditScreen
import com.onlyreminder.app.features.contacts.ui.ContactsScreen
import com.onlyreminder.app.features.groups.ui.GroupEditScreen
import com.onlyreminder.app.features.groups.ui.GroupsScreen
import com.onlyreminder.app.features.home.ui.HomeScreen
import com.onlyreminder.app.features.import.ui.ImportScreen
import com.onlyreminder.app.features.onboarding.ui.OnboardingScreen
import com.onlyreminder.app.features.security.ui.SecurityScreen
import com.onlyreminder.app.features.settings.ui.SettingsScreen
import com.onlyreminder.app.features.tasks.ui.TaskEditScreen
import com.onlyreminder.app.features.tasks.ui.TasksScreen
import com.onlyreminder.app.features.templates.ui.TemplateEditScreen
import com.onlyreminder.app.features.templates.ui.TemplatesScreen
import com.onlyreminder.app.features.whatsapp.ui.WhatsAppScreen

@Composable
fun OnlyReminderNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Contacts.route) { ContactsScreen(navController) }
        composable(Screen.ContactDetail.route) { ContactDetailScreen(navController) }
        composable(Screen.ContactEdit.route) { ContactEditScreen(navController) }
        composable(Screen.Import.route) { ImportScreen(navController) }
        composable(Screen.Groups.route) { GroupsScreen(navController) }
        composable(Screen.GroupEdit.route) { GroupEditScreen(navController) }
        composable(Screen.Tasks.route) { TasksScreen(navController) }
        composable(Screen.TaskEdit.route) { TaskEditScreen(navController) }
        composable(Screen.Templates.route) { TemplatesScreen(navController) }
        composable(Screen.TemplateEdit.route) { TemplateEditScreen(navController) }
        composable(Screen.BirthdayReview.route) { BirthdayReviewScreen(navController) }
        composable(Screen.WhatsApp.route) { WhatsAppScreen(navController) }
        composable(Screen.WhatsAppApi.route) { WhatsAppApiScreen(navController) }
        composable(Screen.Backup.route) { BackupScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.Security.route) { SecurityScreen(navController) }
    }
}
