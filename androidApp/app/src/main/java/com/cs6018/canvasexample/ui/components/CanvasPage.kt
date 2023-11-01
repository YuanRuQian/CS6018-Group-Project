package com.cs6018.canvasexample.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.SensorManager
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.cs6018.canvasexample.R
import com.cs6018.canvasexample.data.CapturableImageViewModel
import com.cs6018.canvasexample.data.PathPropertiesViewModel
import com.cs6018.canvasexample.data.ShakeDetectionViewModel
import com.cs6018.canvasexample.network.ApiViewModel
import com.cs6018.canvasexample.utils.ShakeDetector
import com.cs6018.canvasexample.utils.getCurrentDateTimeString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
    apiViewModel: ApiViewModel,
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

                // Clear All Button
                BottomAppBarItem(
                    iconResource = R.drawable.clear_all,
                    buttonText = "Clear All",
                    onClick = {
                        pathPropertiesViewModel.clearAllPaths()
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
                            apiViewModel,
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
    apiViewModel: ApiViewModel,
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

        val bitmap = apiViewModel.getActiveCapturedImage().value

        if (bitmap == null) {
            Log.e("CanvasPage", "Error occurred while sharing image: bitmap is null")
        } else {
            // Get the active drawing info's title
            val activeDrawingInfoDrawingTitle =
                apiViewModel.activeDrawingInfo.value?.title

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
    currentUserId: String,
    scope: CoroutineScope,
    apiViewModel: ApiViewModel,
    pathPropertiesViewModel: PathPropertiesViewModel,
    navigateToPopBack: () -> Boolean
) {
    Log.d("CanvasPage", "customBackNavigation | currentUserId: $currentUserId")
    navigateToPopBack()
    pathPropertiesViewModel.reset()
    scope.launch {
        apiViewModel.setActiveDrawingInfoById(null)
        apiViewModel.setActiveCapturedImage(null)
        apiViewModel.getCurrentUserDrawingHistory()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasPage(
    pathPropertiesViewModel: PathPropertiesViewModel,
    apiViewModel: ApiViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    navigateToPenCustomizer: () -> Unit,
    navigateToPopBack: () -> Boolean,
    shakeDetectorListener: ShakeDetector.Listener,
    shakeDetectionViewModel: ShakeDetectionViewModel
) {
    val scope = rememberCoroutineScope()

    val captureController = rememberCaptureController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val shakeDetector = ShakeDetector(shakeDetectorListener)
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var drawingTitle by remember {
        mutableStateOf(
            apiViewModel.activeDrawingTitle.value ?: "Untitled"
        )
    }

    LaunchedEffect(key1 = true) {
        // reference: https://github.com/square/seismic/issues/24#issuecomment-954231517
        shakeDetector.start(sensorManager, SensorManager.SENSOR_DELAY_GAME)
    }

    // Use LaunchedEffect to reset drawingTitle when activeDrawingInfo?.drawingTitle changes
    LaunchedEffect(apiViewModel.activeDrawingTitle.value) {
        val newDrawingTitle = apiViewModel.activeDrawingTitle.value ?: "Untitled"
        drawingTitle = newDrawingTitle
        Log.d("CanvasPage", "LaunchedEffect | update drawing title: $newDrawingTitle")
    }

    DisposableEffect(Unit) {
        onDispose {
            shakeDetector.stop()
        }
    }

    BackHandler {
        customBackNavigation(
            Firebase.auth.currentUser?.uid ?: "",
            scope,
            apiViewModel,
            pathPropertiesViewModel,
            navigateToPopBack
        )
    }

    UndoAlertDialog(pathPropertiesViewModel, shakeDetectionViewModel)
    ClearAllAlertDialog(pathPropertiesViewModel, shakeDetectionViewModel)

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
                            apiViewModel.setActiveDrawingInfoTitle(it)
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
                            Log.d(
                                "CanvasPage",
                                "Back button clicked, current user id: ${Firebase.auth.currentUser?.uid}"
                            )
                            customBackNavigation(
                                Firebase.auth.currentUser?.uid ?: "",
                                scope,
                                apiViewModel,
                                pathPropertiesViewModel,
                                navigateToPopBack
                            )
                        }

                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
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
                                coroutineScope,
                                context,
                                captureController,
                                pathPropertiesViewModel,
                                capturableImageViewModel,
                                apiViewModel,
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
                apiViewModel,
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
                apiViewModel,
                capturableImageViewModel
            )
        }
    )
}

fun saveCurrentDrawing(
    coroutineScope: CoroutineScope,
    context: Context,
    captureController: CaptureController,
    pathPropertiesViewModel: PathPropertiesViewModel,
    captureableImageViewModel: CapturableImageViewModel,
    apiViewModel: ApiViewModel,
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
            apiViewModel.addDrawingInfoWithRecentCapturedImage(context)
        Log.d("CanvasPage", "Image saved to $savedImagePath")

        pathPropertiesViewModel.reset()
        navigateToPopBack()
    }
}
