package com.cs6018.canvasexample

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class DrawingInfoRepository  {
    // Implement the repository methods with fake data or behavior for testing
    // For example:
    var activeDrawingInfo: MutableLiveData<DrawingInfo?> = MutableLiveData(null)
    var _allDrawingInfo: LiveData<List<DrawingInfo>> = MutableLiveData(generateRandomTestDrawingInfoList(3))
    var allDrawingInfo = _allDrawingInfo

    suspend fun setActiveDrawingInfoById(i: Int) {}
}


class TestEntryPage {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            Navigation(
                pathPropertiesViewModel = PathPropertiesViewModel(),
                drawingInfoViewModel = DrawingInfoViewModel(DrawingInfoRepository()),
                capturableImageViewModel = CapturableImageViewModel(),
                navController = navController
            )
        }
    }

    @Test
    fun testEntryPage() {
        // wait for splash screen to finish, then check for the presence of the drawing list
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Drawing App").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 drawings").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add a new drawing").performClick()
//        val route = navController.currentBackStackEntry?.destination?.route
//        assertEquals(route, "canvasPage")
    }
}


class TestCanvasPage {
    @get:Rule
    val composeTestRule = createComposeRule()

    val navigateToPopBack: () -> Boolean = {
        false
    }

    @Before
    fun setUp() {
        composeTestRule.setContent {
            // Your Compose UI hierarchy
            CanvasPage(
                pathPropertiesViewModel = PathPropertiesViewModel(),
                drawingInfoViewModel = DrawingInfoViewModel(DrawingInfoRepository()),
                capturableImageViewModel = CapturableImageViewModel(),
                navigateToPenCustomizer = {},
                navigateToPopBack = navigateToPopBack
            )
        }
    }

    @Test
    fun testMyComposable() {
        composeTestRule.onNodeWithText("Untitled").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Done").assertIsDisplayed()
        composeTestRule.onNodeWithText("Palette").assertIsDisplayed()
        composeTestRule.onNodeWithText("Undo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Share").assertIsDisplayed()
        composeTestRule.onNodeWithText("Erase").performClick()
        composeTestRule.onNodeWithText("Draw").assertIsDisplayed()
    }
}