package com.cs6018.canvasexample

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class TestEntryPage {

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
            context, DrawingInfoDatabase::class.java).build()
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
        scope.launch {
            delay(2000)
            composeTestRule.onNodeWithText("Drawing App").assertIsDisplayed()
            composeTestRule.onNodeWithText("3 drawings").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Add a new drawing").performClick()
        }

//        val route = navController.currentBackStackEntry?.destination?.route
//        assertEquals(route, "canvasPage")
    }
}

