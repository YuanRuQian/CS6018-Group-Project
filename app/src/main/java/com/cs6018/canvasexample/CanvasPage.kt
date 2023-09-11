package com.cs6018.canvasexample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasPage(
    navController: NavHostController,
    pathPropertiesViewModel: PathPropertiesViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                        }

                    )  {
                        Text(text = "Done", modifier = Modifier.padding(end = 4.dp))
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done"
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
            BottomAppBar {
                IconButton(onClick = {
                    val previousIsEraseMode = pathPropertiesViewModel.isEraseMode()
                    pathPropertiesViewModel.toggleDrawMode()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (previousIsEraseMode) {
                                "Draw Mode On!"
                            } else {
                                "Erase Mode On!"
                            }
                        )
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.eraser),
                        contentDescription = "Toggle Erase Mode"
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Spacer to evenly distribute buttons
                IconButton(
                    onClick = {
                        navController.navigate("penCustomizer") // Navigate to penCustomizer
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.palette),
                        contentDescription = "Pen Customizer"
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Spacer to evenly distribute buttons
                IconButton(onClick = { pathPropertiesViewModel.undoLastAction() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.undo),
                        contentDescription = "Undo last action"
                    )
                }
            }
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
            Playground(pathPropertiesViewModel, it)
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
