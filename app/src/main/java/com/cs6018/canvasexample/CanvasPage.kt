package com.cs6018.canvasexample

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        IconButton(onClick = onClick) {
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
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    captureController: CaptureController
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
                            snackbarHostState.showSnackbar(
                                if (previousIsEraseMode) {
                                    "Draw Mode On! You could draw with the pen now."
                                } else {
                                    "Erase Mode On! You could erase with the eraser now."
                                }
                            )
                        }
                    }
                )

                // Palette Button
                BottomAppBarItem(
                    iconResource = R.drawable.palette,
                    buttonText = "Palette",
                    onClick = {
                        navController.navigate("penCustomizer")
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
                        onShareClick(scope, context, captureController, drawingInfoViewModel)
                    }
                )
            }
        }
    )
}

// capture the current screenshot and share it
fun onShareClick(
    scope: CoroutineScope,
    context: Context,
    captureController: CaptureController,
    drawingInfoViewModel: DrawingInfoViewModel
) {
    scope.launch {
        // TODO: delete the current image after sharing or use caching
        captureController.capture()
        val imageUri = drawingInfoViewModel.getActiveDrawingInfoImageUri()
        if (imageUri == null) {
            Toast.makeText(
                context,
                "Image capture failed, please try again",
                Toast.LENGTH_LONG
            ).show()
            Log.d("CanvasPage", "No image found, please create an image first")
            return@launch
        }
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share Image")
        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(context, chooserIntent, null)
    }
}

fun customBackNavigation(
    navController: NavController,
    scope: CoroutineScope,
    drawingInfoViewModel: DrawingInfoViewModel,
    pathPropertiesViewModel: PathPropertiesViewModel
) {
    navController.popBackStack()
    pathPropertiesViewModel.reset()
    scope.launch {
        drawingInfoViewModel.setActiveDrawingInfoById(null)
        drawingInfoViewModel.setActiveCapturedImage(null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasPage(
    navController: NavHostController,
    pathPropertiesViewModel: PathPropertiesViewModel,
    drawingInfoViewModel: DrawingInfoViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val captureController = rememberCaptureController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val activeDrawingInfo by drawingInfoViewModel.activeDrawingInfo.observeAsState()

    Log.d("CanvasPage", "activeDrawingInfo | id: ${activeDrawingInfo?.id}")

    BackHandler {
        customBackNavigation(navController, scope, drawingInfoViewModel, pathPropertiesViewModel)
    }

    Scaffold(
        // Add a top title bar
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = activeDrawingInfo?.drawingTitle ?: "Untitled",
                    )
                },


                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            customBackNavigation(
                                navController,
                                scope,
                                drawingInfoViewModel,
                                pathPropertiesViewModel
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
                                navController,
                                pathPropertiesViewModel
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
                navController,
                snackbarHostState,
                scope,
                captureController
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Text(data.visuals.message, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        content = {
            Playground(pathPropertiesViewModel, it, captureController, drawingInfoViewModel)
        }
    )
}

fun saveCurrentDrawing(
    drawingInfoViewModel: DrawingInfoViewModel,
    coroutineScope: CoroutineScope,
    context: Context,
    captureController: CaptureController,
    navController: NavController,
    pathPropertiesViewModel: PathPropertiesViewModel
) {
    coroutineScope.launch {
        // TODO: Handle Save Button. Save files and go back to the list view
        captureController.capture()
        // TODO: find some way to singal the capture is done instead of using delay
        delay(200)
        val savedImagePath = drawingInfoViewModel.addDrawingInfoWithRecentCapturedImage(context)
        Log.d("CanvasPage", "Image saved to $savedImagePath")
        drawingInfoViewModel.setActiveDrawingInfoById(null)
        drawingInfoViewModel.setActiveCapturedImage(null)
        pathPropertiesViewModel.reset()
        navController.popBackStack()
    }
}

// TODO: add preview for CanvasPage