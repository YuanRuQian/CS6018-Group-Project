package com.cs6018.canvasexample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule

class ListViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

        }
    }

    @Composable
    fun TestListView() {

        val drawingInfoList = generateTestDrawingInfoList()

        val coroutineScope = rememberCoroutineScope()


//        DrawingList(
//            navigateToCanvasPage = {},
//            dataList = drawingInfoList,
//            state = rememberLazyListState(),
//            drawingInfoViewModel = DrawingInfoViewModel()
//        )
    }

}