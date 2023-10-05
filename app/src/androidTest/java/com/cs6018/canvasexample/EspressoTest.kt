package com.cs6018.canvasexample

import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cs6018.canvasexample.ui.theme.CanvasExampleTheme
import org.junit.Before

import org.junit.Rule
import org.junit.Test

class TestListViewViewModel : ViewModel() {
    private val _drawingInfoList = MutableLiveData<List<DrawingInfo>>()
    val drawingInfoList: LiveData<List<DrawingInfo>> = _drawingInfoList

    private val _navigateToCanvasPageClickCounter = MutableLiveData(0)
    val navigateToCanvasPageClickCounter: LiveData<Int> = _navigateToCanvasPageClickCounter

    private val _setActiveCapturedImageClickCounter = MutableLiveData(0)
    val setActiveCapturedImageClickCounter: LiveData<Int> = _setActiveCapturedImageClickCounter

    private val _setActiveDrawingInfoByIdClickCounter = MutableLiveData(0)
    val setActiveDrawingInfoByIdClickCounter: LiveData<Int> = _setActiveDrawingInfoByIdClickCounter

    fun incrementNavigateToCanvasPageClickCounter() {
        _navigateToCanvasPageClickCounter.value = (_navigateToCanvasPageClickCounter.value ?: 0) + 1
        println("incrementNavigateToCanvasPageClickCounter : ${_navigateToCanvasPageClickCounter.value}")
    }

    fun incrementSetActiveCapturedImageClickCounter() {
        _setActiveCapturedImageClickCounter.value =
            (_setActiveCapturedImageClickCounter.value ?: 0) + 1
        println("incrementSetActiveCapturedImageClickCounter : ${_setActiveCapturedImageClickCounter.value}")
    }

    fun incrementSetActiveDrawingInfoByIdClickCounter() {
        _setActiveDrawingInfoByIdClickCounter.value =
            (_setActiveDrawingInfoByIdClickCounter.value ?: 0) + 1
        println("incrementSetActiveDrawingInfoByIdClickCounter : ${_setActiveDrawingInfoByIdClickCounter.value}")
    }

    fun addDrawingInfo(drawingInfo: DrawingInfo) {
        // Get the current list of DrawingInfo objects
        val currentList = _drawingInfoList.value ?: emptyList()

        // Create a new list by adding the new DrawingInfo
        val newList = currentList.toMutableList().apply {
            add(drawingInfo)
        }

        // Update the LiveData with the new list
        _drawingInfoList.value = newList
    }
}

class EspressoTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Lazy list, only a few cards are loaded at a time
    private val listSize = 3
    private val drawingInfoList = generateTestDrawingInfoList(listSize)
    private lateinit var testViewModel: TestListViewViewModel

    @Before
    fun setup() {
        testViewModel = TestListViewViewModel()
        drawingInfoList.forEach(testViewModel::addDrawingInfo)
    }

    @Test
    fun myTest() {

        val dataList = testViewModel.drawingInfoList.value

        val navigateToCanvasPageMock = {
            println("navigateToCanvasPageMock")
            testViewModel.incrementNavigateToCanvasPageClickCounter()
        }

        val setActiveCapturedImageMock = { _: Bitmap? ->
            println("setActiveCapturedImageMock")
            testViewModel.incrementSetActiveCapturedImageClickCounter()
        }

        val setActiveDrawingInfoByIdMock = { _: Int? ->
            println("setActiveDrawingInfoByIdMock")
            testViewModel.incrementSetActiveDrawingInfoByIdClickCounter()
        }

        // Start the app
        composeTestRule.setContent {
            val navController = rememberNavController()
            CanvasExampleTheme {
                NavHost(navController = navController, startDestination = "drawingList") {
                    DrawingListScreen(
                        navigateToCanvasPageMock,
                        setActiveCapturedImageMock,
                        setActiveDrawingInfoByIdMock,
                        dataList
                    )
                }
            }
        }

//        composeTestRule.onNodeWithText("Continue").performClick()
//
//        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }

}