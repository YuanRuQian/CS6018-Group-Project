package com.cs6018.canvasexample.ui.components

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs6018.canvasexample.network.ApiViewModel
import com.cs6018.canvasexample.network.UserDrawing
import com.cs6018.canvasexample.utils.getCurrentUserId
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: swipe down to refresh

@Composable
fun ExploreFeedList(
    dataList: List<UserDrawing>?,
    navigateToCanvasPage: () -> Unit,
    setActiveDrawingBackgroundImageReference: (String?) -> Unit,
    setActiveDrawingInfoById: (String) -> Unit
) {
    Log.d("ExploreFeedList", "dataList: $dataList")
    if (dataList == null) {
        return
    }

    Log.d("ExploreFeedList", "dataList length: ${dataList.size}")

    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(dataList, key = {
            it.id
        }) { drawingInfo ->

            val onClick = {
                val isCurrentUserHistoryDrawing = drawingInfo.creatorId == getCurrentUserId()
                val toastTooltip = if (isCurrentUserHistoryDrawing) {
                    "Continue to work on your previous drawing"
                } else {
                    "Import template from other users as background"
                }
                Log.d("ExploreFeedList", "onClick: $drawingInfo")
                if (isCurrentUserHistoryDrawing) {
                    setActiveDrawingInfoById(drawingInfo.id)
                }
                setActiveDrawingBackgroundImageReference(drawingInfo.imagePath)
                navigateToCanvasPage()
                Toast.makeText(context, toastTooltip, Toast.LENGTH_LONG)
                    .show()
            }
            ExploreFeedDrawingCard(drawingInfo, onClick)
        }
    }
}

@Composable
fun HistoryDrawingList(
    navigateToCanvasPage: () -> Unit,
    dataList: List<UserDrawing>?,
    state: LazyListState,
    setActiveDrawingInfoById: (String) -> Unit,
    removeListItem: (String) -> Unit
) {

    Log.d("HistoryDrawingList", "dataList: $dataList")

    if (dataList == null) {
        return
    }

    LazyColumn(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxSize()
            .testTag("DrawingList"),
        state = state,
    ) {
        items(dataList, key = {
            it.id
        }) { drawingInfo ->
            HistoryDrawingListItem(
                drawingInfo,
                setActiveDrawingInfoById,
                removeListItem,
                navigateToCanvasPage
            )
        }
    }
}

// TODO: handle sign out failure
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingListScreen(
    apiViewModel: ApiViewModel,
    navigateToCanvasPage: () -> Unit,
    setActiveDrawingInfoById: (String?) -> Unit,
    currentUserDrawingHistory: List<UserDrawing>?,
    currentUserExploreFeed: List<UserDrawing>?,
    removeListItem: (String) -> Unit,
    navigateToAuthenticationScreen: () -> Unit,
    setActiveDrawingBackgroundImageReference: (String?) -> Unit
) {
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var currentActiveIndex by remember { mutableIntStateOf(0) }

    val updateCurrentActiveIndex = { index: Int ->
        currentActiveIndex = index
    }

    // Scroll to top when the screen is first displayed or when returning from another screen
    DisposableEffect(Unit) {
        coroutineScope.launch {
            // Add a delay to make sure the list is updated before scrolling
            delay(100)
            state.animateScrollToItem(0)
        }
        onDispose { }
    }

    // TODO: if there is no drawing, show a message to the user the list is empty
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(), // Ensure Text takes full width
                        contentAlignment = Alignment.Center
                    ) {
                        when (currentActiveIndex) {
                            0 -> Text(
                                text = "My Drawing History",
                                textAlign = TextAlign.Center,
                            )

                            1 -> Text(
                                text = "What's New",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                content = {
                    DrawingListPageTabRow(
                        apiViewModel,
                        currentActiveIndex,
                        updateCurrentActiveIndex,
                        navigateToCanvasPage,
                        navigateToAuthenticationScreen
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
            )
        }
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 56.dp)
        ) {
            when (currentActiveIndex) {
                0 -> {
                    HistoryDrawingList(
                        navigateToCanvasPage,
                        currentUserDrawingHistory,
                        state,
                        setActiveDrawingInfoById,
                        removeListItem
                    )
                }

                1 -> {
                    Log.d("ExploreFeedList", "currentUserExploreFeed: $currentUserExploreFeed")
                    ExploreFeedList(
                        currentUserExploreFeed,
                        navigateToCanvasPage,
                        setActiveDrawingBackgroundImageReference,
                        setActiveDrawingInfoById
                    )
                }
            }
        }
    }
}