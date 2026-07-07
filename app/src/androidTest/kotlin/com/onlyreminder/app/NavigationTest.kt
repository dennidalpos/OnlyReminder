package com.onlyreminder.app

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigationToAllScreens() {
        val items = listOf(
            "Contacts",
            "Tasks",
            "Birthday Review",
            "Templates",
            "Groups",
            "Import",
            "Backup",
            "Settings",
            "Security",
            "Configure WhatsApp API",
        )

        items.forEach { item ->
            // Click on the menu button
            composeTestRule.onNodeWithText(item).performClick()

            // Check if the screen loaded (at least wait for some synchronization)
            composeTestRule.waitForIdle()

            // Go back to Home
            // Depending on how back is implemented, we might use Espresso or a back button in the UI
            // Most screens have a back button in the top bar. 
            // We'll use the device back button for simplicity if no specific back button is easily targetable.
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
                .sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)

            composeTestRule.waitForIdle()
        }
    }
}
