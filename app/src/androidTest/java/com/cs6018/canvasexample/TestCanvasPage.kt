package com.cs6018.canvasexample

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class TestCanvasPage {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val navigateToPopBack: () -> Boolean = {
        false
    }

    private lateinit var dao: DrawingInfoDAO
    private lateinit var db: DrawingInfoDatabase
    private lateinit var scope: CoroutineScope

    @Before
    fun setUp() {
        // create a database in memory
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DrawingInfoDatabase::class.java).build()
        dao = db.drawingInfoDao()
        scope = CoroutineScope(Dispatchers.IO)

        composeTestRule.setContent {
            // Your Compose UI hierarchy
            CanvasPage(
                pathPropertiesViewModel = PathPropertiesViewModel(),
                drawingInfoViewModel = DrawingInfoViewModel(DrawingInfoRepository(scope, dao)),
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
        composeTestRule.onNodeWithTag("Erase").performClick()
        composeTestRule.onNodeWithText("Draw").assertIsDisplayed()
    }
}