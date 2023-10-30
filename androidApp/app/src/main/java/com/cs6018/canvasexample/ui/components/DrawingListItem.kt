package com.cs6018.canvasexample.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cs6018.canvasexample.R
import com.cs6018.canvasexample.network.DrawingResponse
import com.cs6018.canvasexample.utils.formatDate
import kotlinx.coroutines.delay
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingCard(drawingInfo: DrawingResponse, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .testTag("DrawingCard${drawingInfo.id}"),
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
                    text = drawingInfo.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatDate(Date(drawingInfo.lastModifiedDate)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Splash Icon",
                modifier = Modifier.size(100.dp)
            )
            // TODO: add back thumbnail
//            if (drawingInfo.thumbnail != null) {
//                val thumbnail = BitmapFactory
//                    .decodeByteArray(drawingInfo.thumbnail, 0, drawingInfo.thumbnail!!.size)
//                Image(
//                    bitmap = thumbnail.asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(100.dp)
//                )
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingListItem(
    drawingInfo: DrawingResponse,
    setActiveDrawingInfoById: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    navigateToCanvasPage: () -> Unit
) {
    val context = LocalContext.current
    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(drawingInfo)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                show = false
                true
            } else false
        }, positionalThreshold = { 150f }
    )
    AnimatedVisibility(
        show, exit = fadeOut(spring())
    ) {
        SwipeToDismiss(
            state = dismissState,
            modifier = Modifier,
            background = {
                DismissBackground(dismissState)
            },
            dismissContent = {
                DrawingCard(
                    drawingInfo = currentItem,
                    onClick = {
                        Log.d("DrawingList", "Clicked on drawing ${drawingInfo.id}")
                        setActiveDrawingInfoById(currentItem.id)
                        navigateToCanvasPage()
                    }
                )
            }
        )
    }

    LaunchedEffect(show) {
        if (!show) {
            delay(500)
            onRemove(currentItem.id)
            Toast.makeText(context, "Drawing removed", Toast.LENGTH_SHORT).show()
        }
    }
}
