package com.cs6018.canvasexample

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
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
    pathPropertiesViewModel: PathPropertiesViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
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
                        scope.launch {
                            val uriToImage = getUriOfFirstImageInDirectory(context)
                            val shareIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, uriToImage)
                                type = "image/jpeg"
                            }
                            val chooserIntent = Intent.createChooser(shareIntent, "Share Image")
                            chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(context, chooserIntent, null)
                        }
                    }
                )
            }
        }
    )
}


// TODO: get the proper URI of the image file
fun getUriOfFirstImageInDirectory(context: Context): Uri? {
    // Get the directory for storing files in external storage
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    if (directory != null && directory.exists() && directory.isDirectory) {
        // List all files in the directory
        val files = directory.listFiles()

        if (files == null || files.isEmpty()) {
            Log.d("CanvasPage", "No files found in the directory, please create an image first")
            return null
        }

        // Loop through the files to find the first image (you can adjust this logic as needed)
        for (file in files) {
            if (isImage(file)) {
                // Generate a content:// URI using FileProvider
                return FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
            }
        }
    }

    // If no image is found, return null
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasPage(
    navController: NavHostController,
    pathPropertiesViewModel: PathPropertiesViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val captureController = rememberCaptureController()

    Scaffold(
        // Add a top title bar
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Canvas",
                    )
                },

                // Back Button
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }

                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                        Text(text = "Back", modifier = Modifier.padding(start = 4.dp))
                    }
                },

                // Save Button
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            // TODO: Handle Save Button. Save files and go back to the list view
                            navController.popBackStack()
                            captureController.capture()
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
                pathPropertiesViewModel,
                navController,
                snackbarHostState,
                scope
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
            Playground(pathPropertiesViewModel, it, captureController)
        }
    )
}

// Preview the Canvas UI
@Preview
@Composable
fun CanvasPagePreview() {
    CanvasPage(
        navController = rememberNavController(),
        pathPropertiesViewModel = PathPropertiesViewModel()
    )
}
