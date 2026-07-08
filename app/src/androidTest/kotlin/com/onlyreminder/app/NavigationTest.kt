package com.onlyreminder.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigationToAllScreens() {
        // 0. Handle Onboarding if it exists
        composeTestRule.waitForIdle()

        // Define possible buttons to click through onboarding
        val buttonResIds = listOf(R.string.next, R.string.skip, R.string.finish)

        repeat(10) {
            var clicked = false
            for (resId in buttonResIds) {
                val text = composeTestRule.activity.getString(resId)
                val nodes = composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes()
                if (nodes.isNotEmpty()) {
                    composeTestRule.onNodeWithText(text).performClick()
                    composeTestRule.waitForIdle()
                    clicked = true
                    break
                }
            }
            if (!clicked) return@repeat
        }

        composeTestRule.waitForIdle()

        // 1. Test Management Section items (Home Screen)
        val homeItems = listOf(
            R.string.contacts_title,
            R.string.groups_title,
            R.string.templates_title,
            R.string.import_contacts,
        )

        homeItems.forEach { itemRes ->
            val label = composeTestRule.activity.getString(itemRes)
            composeTestRule.onNodeWithText(label).performClick()
            composeTestRule.waitForIdle()
            InstrumentationRegistry.getInstrumentation()
                .sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
            composeTestRule.waitForIdle()
        }

        // 2. Test Settings (Top Bar)
        val settingsLabel = composeTestRule.activity.getString(R.string.settings_title)
        composeTestRule.onNodeWithContentDescription(settingsLabel).performClick()
        composeTestRule.waitForIdle()

        // Return to Home
        InstrumentationRegistry.getInstrumentation()
            .sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        composeTestRule.waitForIdle()
    }
}
