package com.onlyreminder.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.onlyreminder.app.features.backup.ui.BackupScreen
import com.onlyreminder.app.features.birthday.ui.BirthdayReviewScreen
import com.onlyreminder.app.features.birthday.ui.ReportScreen
import com.onlyreminder.app.features.contacts.ui.ContactDetailScreen
import com.onlyreminder.app.features.contacts.ui.ContactEditScreen
import com.onlyreminder.app.features.contacts.ui.ContactsScreen
import com.onlyreminder.app.features.groups.ui.GroupEditScreen
import com.onlyreminder.app.features.groups.ui.GroupsScreen
import com.onlyreminder.app.features.home.ui.HomeScreen
import com.onlyreminder.app.features.importer.ui.ImportScreen
import com.onlyreminder.app.features.onboarding.ui.OnboardingScreen
import com.onlyreminder.app.features.security.ui.SecurityScreen
import com.onlyreminder.app.features.settings.ui.SettingsScreen
import com.onlyreminder.app.features.tasks.ui.TaskEditScreen
import com.onlyreminder.app.features.tasks.ui.TasksScreen
import com.onlyreminder.app.features.templates.ui.TemplateEditScreen
import com.onlyreminder.app.features.templates.ui.TemplatesScreen
import com.onlyreminder.app.features.whatsapp.ui.WhatsAppApiScreen
import com.onlyreminder.app.features.whatsapp.ui.WhatsAppScreen
import androidx.compose.ui.Modifier

@Composable
fun OnlyReminderNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Route = Route.Home,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Onboarding> { OnboardingScreen(navController) }
        composable<Route.Home> { HomeScreen(navController) }
        composable<Route.Contacts> { ContactsScreen(navController) }
        composable<Route.ContactDetail> { ContactDetailScreen(navController) }
        composable<Route.ContactEdit> { ContactEditScreen(navController) }
        composable<Route.Import> { ImportScreen(navController) }
        composable<Route.Groups> { GroupsScreen(navController) }
        composable<Route.GroupEdit> { GroupEditScreen(navController) }
        composable<Route.Tasks> { TasksScreen(navController) }
        composable<Route.TaskEdit> { TaskEditScreen(navController) }
        composable<Route.Templates> { TemplatesScreen(navController) }
        composable<Route.TemplateEdit> { TemplateEditScreen(navController) }
        composable<Route.BirthdayReview> { BirthdayReviewScreen(navController) }
        composable<Route.WhatsApp> { WhatsAppScreen(navController) }
        composable<Route.Report> { ReportScreen(navController) }
        composable<Route.WhatsAppApi> { WhatsAppApiScreen(navController) }
        composable<Route.Backup> { BackupScreen(navController) }
        composable<Route.Settings> { SettingsScreen(navController) }
        composable<Route.Security> { SecurityScreen(navController) }
    }
}
