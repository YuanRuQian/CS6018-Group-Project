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
import com.cs6018.canvasexample.activity.Navigation
import com.cs6018.canvasexample.data.CapturableImageViewModel
import com.cs6018.canvasexample.data.DrawingInfoDAO
import com.cs6018.canvasexample.data.DrawingInfoDatabase
import com.cs6018.canvasexample.data.DrawingInfoRepository
import com.cs6018.canvasexample.data.DrawingInfoViewModel
import com.cs6018.canvasexample.data.PathPropertiesViewModel
import com.cs6018.canvasexample.data.ShakeDetectionViewModel
import com.cs6018.canvasexample.utils.ShakeDetector
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class MockShakeListener : ShakeDetector.Listener {
    override fun hearLightShake() {
    }

    override fun hearHardShake() {
    }
}


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
                PathPropertiesViewModel(),
                DrawingInfoViewModel(repository),
                CapturableImageViewModel(),
                navController,
                ShakeDetectionViewModel(),
                // pass a mock implementation of ShakeDetector.Listener to pass the test
                MockShakeListener(),
                FirebaseAuth.getInstance(),
                { _, _, _, _ -> },
                isTest = true
            )
        }
    }

    @Test
    fun testEntryPage() {
        scope.launch {
            // test SplashScreen display
            composeTestRule.onNodeWithContentDescription("Splash Icon").assertIsDisplayed()
            composeTestRule.onNodeWithText("Draw Better Than It").assertIsDisplayed()

            delay(2000)
            composeTestRule.onNodeWithText("Drawing App").assertIsDisplayed()
            composeTestRule.onNodeWithText("3 drawings").assertIsDisplayed()
            // test EntryPage UI + navigation from EntryPage to CanvasPage
            composeTestRule.onNodeWithContentDescription("Add a new drawing").performClick()
            delay(100)
            composeTestRule.onNodeWithText("Untitled").assertIsDisplayed()

            // test CanvasPage UI
            val backBtn = composeTestRule.onNodeWithText("Back")
            backBtn.assertIsDisplayed()
            composeTestRule.onNodeWithTag("Done").assertIsDisplayed()
            composeTestRule.onNodeWithTag("Palette").assertIsDisplayed()
            composeTestRule.onNodeWithTag("Undo").assertIsDisplayed()
            composeTestRule.onNodeWithTag("Clear All").assertIsDisplayed()
            composeTestRule.onNodeWithTag("Share").assertIsDisplayed()
            composeTestRule.onNodeWithTag("Erase").performClick()
            composeTestRule.onNodeWithTag("Draw").assertIsDisplayed()
            // test update title
            composeTestRule.onNodeWithText("Untitled").performTextReplacement("New Title")

            // test navigation from CanvasPage to EntryPage
            backBtn.performClick()
            composeTestRule.onNodeWithText("Drawing App").assertIsDisplayed()
            composeTestRule.onNodeWithText("New Title").assertIsDisplayed()
        }
    }
}

