package com.cs6018.canvasexample

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UIAndNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var dao: DrawingInfoDAO
    private lateinit var db: DrawingInfoDatabase
    private lateinit var scope: CoroutineScope
    private lateinit var repository: DrawingInfoRepository

    @Before
    fun setup() {
        // create a database in memory
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DrawingInfoDatabase::class.java
        ).build()
        dao = db.drawingInfoDao()
        scope = CoroutineScope(Dispatchers.IO)
        repository = DrawingInfoRepository(scope, dao)

        val drawingInfoList = generateRandomTestDrawingInfoList(3)
        drawingInfoList.forEach {
            repository.addNewDrawingInfo(it)
        }


        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            Navigation(
                pathPropertiesViewModel = PathPropertiesViewModel(),
                drawingInfoViewModel = DrawingInfoViewModel(repository),
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

        scope.launch {
            // test EntryPage UI + navigation from EntryPage to CanvasPage
            composeTestRule.onNodeWithContentDescription("Add a new drawing").performClick()
            delay(100)
            composeTestRule.onNodeWithText("Untitled").assertIsDisplayed()

            // test CanvasPage UI
            val backBtn = composeTestRule.onNodeWithText("Back")
            backBtn.assertIsDisplayed()
            composeTestRule.onNodeWithText("Done").assertIsDisplayed()
            composeTestRule.onNodeWithText("Palette").assertIsDisplayed()
            composeTestRule.onNodeWithText("Undo").assertIsDisplayed()
            composeTestRule.onNodeWithText("Share").assertIsDisplayed()
            composeTestRule.onNodeWithTag("Erase").performClick()
            composeTestRule.onNodeWithText("Draw").assertIsDisplayed()
            // test update title
            composeTestRule.onNodeWithText("Untitled").performTextReplacement("New Title")

            // test navigation from CanvasPage to EntryPage
            backBtn.performClick()
            composeTestRule.onNodeWithText("Drawing App").assertIsDisplayed()
            composeTestRule.onNodeWithText("New Title").assertIsDisplayed()
        }
    }
}

