package com.cs6018.canvasexample.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs6018.canvasexample.data.DrawingInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawingList(
    navigateToCanvasPage: () -> Unit,
    dataList: List<DrawingInfo>?,
    state: LazyListState,
    setActiveDrawingInfoById: suspend (Int?) -> Unit,
    removeListItem: suspend (DrawingInfo, Context) -> Unit
) {

    if (dataList == null) {
        return
    }

    val scope = rememberCoroutineScope()

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
            DrawingListItem(
                scope,
                drawingInfo,
                setActiveDrawingInfoById,
                removeListItem,
                navigateToCanvasPage
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingListScreen(
    navigateToCanvasPage: () -> Unit,
    setActiveCapturedImage: (Bitmap?) -> Unit,
    setActiveDrawingInfoById: suspend (Int?) -> Unit,
    dataList: List<DrawingInfo>?,
    removeListItem: suspend (DrawingInfo, Context) -> Unit
) {

    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Calculate the number of drawings
    val numberOfDrawings = dataList?.size ?: 0

    // Scroll to top when the screen is first displayed or when returning from another screen
    DisposableEffect(Unit) {
        coroutineScope.launch {
            // Add a delay to make sure the list is updated before scrolling
            delay(100)
            state.animateScrollToItem(0)
        }
        onDispose { }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Drawing App",
                        textAlign = TextAlign.Center,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        bottomBar = {
            BottomAppBar(
                content = {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "$numberOfDrawings drawings",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    FloatingActionButton(
                        modifier = Modifier.padding(end = 16.dp),
                        onClick = {
                            coroutineScope.launch {
                                setActiveCapturedImage(null)
                                setActiveDrawingInfoById(null)
                                // Add a small delay for better UX
                                delay(100)
                                navigateToCanvasPage()
                            }
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Add a new drawing")
                    }
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
            DrawingList(
                navigateToCanvasPage,
                dataList,
                state,
                setActiveDrawingInfoById,
                removeListItem
            )
        }
    }
}
