package com.cs6018.canvasexample

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawingList(
    navController: NavHostController,
    dataList: List<DrawingInfo>?,
    state: LazyListState,
    drawingInfoViewModel: DrawingInfoViewModel
) {

    if (dataList == null) {
        return
    }

    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        state = state,
    ) {
        items(dataList, key = {
            it.id
        }) { drawingInfo ->
            DrawingCard(drawingInfo) {
                Log.d("DrawingList", "Clicked on drawing ${drawingInfo.id}")
                scope.launch {
                    drawingInfoViewModel.setActiveDrawingInfoById(drawingInfo.id)
                }
                navController.navigate("canvasPage")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingCard(drawingInfo: DrawingInfo, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = drawingInfo.drawingTitle,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatDate(drawingInfo.lastModifiedDate),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // TODO: replace placeholder with the actual preview
            if (drawingInfo.thumbnail != null) {
                val thumbnail = BitmapFactory
                    .decodeByteArray(drawingInfo.thumbnail, 0, drawingInfo.thumbnail!!.size);
                Image(
                    bitmap = thumbnail.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        }
    }
}


// TODO: when get back / first landing, scroll to top
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingListScreen(
    navController: NavHostController,
    drawingInfoViewModel: DrawingInfoViewModel
) {

    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val dataList by drawingInfoViewModel.allDrawingInfo.observeAsState()

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
                                drawingInfoViewModel.setActiveCapturedImage(null)
                                drawingInfoViewModel.setActiveDrawingInfoById(null)
                                // Add a small delay for better UX
                                delay(100)
                                navController.navigate("canvasPage")
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
            DrawingList(navController, dataList, state, drawingInfoViewModel)
        }
    }
}
