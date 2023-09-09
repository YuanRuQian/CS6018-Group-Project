package com.cs6018.canvasexample

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun CanvasPage(
    navController: NavHostController,
    pathPropertiesViewModel: PathPropertiesViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
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
                        imageVector = Icons.Default.Clear,
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
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Pen Customizer"
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Spacer to evenly distribute buttons
                IconButton(onClick = { pathPropertiesViewModel.undoLastAction() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
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
