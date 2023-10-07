package com.cs6018.canvasexample

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


@Composable
fun BottomAppBarItem(
    iconResource: Int,
    buttonText: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onClick, modifier = Modifier.testTag(buttonText)) {
            Icon(
                painter = painterResource(id = iconResource),
                contentDescription = buttonText,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = buttonText,
            fontSize = 12.sp,
        )
    }
}


@Composable
fun BottomAppBarContent(
    drawingInfoViewModel: DrawingInfoViewModel,
    pathPropertiesViewModel: PathPropertiesViewModel,
    scope: CoroutineScope,
    captureController: CaptureController,
    capturableImageViewModel: CapturableImageViewModel,
    navigateToPenCustomizer: () -> Unit
) {
    val context = LocalContext.current
    BottomAppBar(
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Eraser Button
                BottomAppBarItem(
                    iconResource = pathPropertiesViewModel.eraseDrawToggleButtonIcon.collectAsState().value.iconResource,
                    buttonText = pathPropertiesViewModel.eraseDrawToggleButtonText.collectAsState().value.text,
                    onClick = {
                        val previousIsEraseMode = pathPropertiesViewModel.isEraseMode()
                        pathPropertiesViewModel.toggleDrawMode()
                        scope.launch {
                            if (previousIsEraseMode) {
                                Toast.makeText(
                                    context,
                                    "Draw Mode On! You could draw with the pen now.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erase Mode On! You could erase with the eraser now.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

                // Palette Button
                BottomAppBarItem(
                    iconResource = R.drawable.palette,
                    buttonText = "Palette",
                    onClick = {
                        navigateToPenCustomizer()
                    }
                )

                // Undo Button
                BottomAppBarItem(
                    iconResource = R.drawable.undo,
                    buttonText = "Undo",
                    onClick = {
                        pathPropertiesViewModel.undoLastAction()
                    }
                )

                BottomAppBarItem(
                    iconResource = R.drawable.share,
                    buttonText = "Share",
                    onClick = {
                        onShareClick(
                            scope,
                            context,
                            captureController,
                            drawingInfoViewModel,
                            capturableImageViewModel
                        )
                    }
                )
            }
        }
    )
}

fun onShareClick(
    scope: CoroutineScope,
    context: Context,
    captureController: CaptureController,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel
) {
    scope.launch {
        // Capture the current screenshot
        captureController.capture()
    }

    scope.launch {
        Log.d("CanvasPage", "onShareClick | Waiting for signal")

        capturableImageViewModel.signalChannel.value?.receive()

        capturableImageViewModel.setNewSignalChannel()

        val bitmap = drawingInfoViewModel.getActiveCapturedImage().value

        if (bitmap == null) {
            Log.e("CanvasPage", "Error occurred while sharing image: bitmap is null")
        } else {
            // Get the active drawing info's title
            val activeDrawingInfoDrawingTitle =
                drawingInfoViewModel.activeDrawingInfo.value?.drawingTitle

            // Convert the bitmap to a temporary file and get its URI
            val uri = saveBitmapAsTemporaryImage(context, bitmap)

            // Create an Intent for sharing
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/jpeg"
            }

            // Create a chooser dialog for sharing
            val chooserIntent =
                Intent.createChooser(shareIntent, activeDrawingInfoDrawingTitle)

            // Grant read URI permission to the receiving app
            chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Start the sharing process
            context.startActivity(chooserIntent)
        }

    }
}


private fun saveBitmapAsTemporaryImage(context: Context, bitmap: Bitmap): Uri {
    val cacheDir = context.cacheDir
    val imageFile = File.createTempFile(getCurrentDateTimeString(), ".jpg", cacheDir)

    val outputStream = FileOutputStream(imageFile)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    Log.d("CanvasPage", "authority: ${context.packageName + ".provider"}")
    return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
}


fun customBackNavigation(
    scope: CoroutineScope,
    drawingInfoViewModel: DrawingInfoViewModel,
    pathPropertiesViewModel: PathPropertiesViewModel,
    navigateToPopBack: () -> Boolean
) {
    navigateToPopBack()
    pathPropertiesViewModel.reset()
    scope.launch {
        drawingInfoViewModel.setActiveDrawingInfoById(null)
        drawingInfoViewModel.setActiveCapturedImage(null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasPage(
    pathPropertiesViewModel: PathPropertiesViewModel,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    navigateToPenCustomizer: () -> Unit,
    navigateToPopBack: () -> Boolean
) {
    val scope = rememberCoroutineScope()

    val captureController = rememberCaptureController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val activeDrawingInfo by drawingInfoViewModel.activeDrawingInfo.observeAsState()

    var drawingTitle by rememberSaveable {
        mutableStateOf(
            activeDrawingInfo?.drawingTitle ?: "Untitled"
        )
    }

    Log.d("CanvasPage", "activeDrawingInfo | id: ${activeDrawingInfo?.id}")

    BackHandler {
        customBackNavigation(
            scope,
            drawingInfoViewModel,
            pathPropertiesViewModel,
            navigateToPopBack
        )
    }

    Scaffold(
        // Add a top title bar
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    TextField(
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        value = drawingTitle,
                        onValueChange = {
                            drawingTitle = it
                            Log.d("CanvasPage", "Title: onValueChange | $it")
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                    )
                },

                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            customBackNavigation(
                                scope,
                                drawingInfoViewModel,
                                pathPropertiesViewModel,
                                navigateToPopBack
                            )
                        }

                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                        Text(text = "Back", modifier = Modifier.padding(start = 4.dp))
                    }
                },


                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            saveCurrentDrawing(
                                drawingInfoViewModel,
                                coroutineScope,
                                context,
                                captureController,
                                pathPropertiesViewModel,
                                capturableImageViewModel,
                                navigateToPopBack
                            )
                        }

                    ) {
                        Text(text = "Done", modifier = Modifier.padding(end = 4.dp))
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done",
                        )
                    }
                },

                // Set up the default color following the phone's theme
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        bottomBar = {
            BottomAppBarContent(
                drawingInfoViewModel,
                pathPropertiesViewModel,
                scope,
                captureController,
                capturableImageViewModel,
                navigateToPenCustomizer
            )
        },
        content = {
            Playground(
                pathPropertiesViewModel,
                it,
                captureController,
                drawingInfoViewModel,
                capturableImageViewModel
            )
        }
    )
}

fun saveCurrentDrawing(
    drawingInfoViewModel: DrawingInfoViewModel,
    coroutineScope: CoroutineScope,
    context: Context,
    captureController: CaptureController,
    pathPropertiesViewModel: PathPropertiesViewModel,
    captureableImageViewModel: CapturableImageViewModel,
    navigateToPopBack: () -> Boolean
) {
    coroutineScope.launch {
        captureController.capture()
    }

    coroutineScope.launch {
        Log.d("CanvasPage", "saveCurrentDrawing | Waiting for signal")
        captureableImageViewModel.signalChannel.value?.receive()
        captureableImageViewModel.setNewSignalChannel()

        val savedImagePath =
            drawingInfoViewModel.addDrawingInfoWithRecentCapturedImage(context)
        Log.d("CanvasPage", "Image saved to $savedImagePath")


        drawingInfoViewModel.setActiveDrawingInfoById(null)
        drawingInfoViewModel.setActiveCapturedImage(null)
        pathPropertiesViewModel.reset()
        navigateToPopBack()
    }
}
