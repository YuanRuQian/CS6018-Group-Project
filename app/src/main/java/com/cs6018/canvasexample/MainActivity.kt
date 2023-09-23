package com.cs6018.canvasexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs6018.canvasexample.ui.theme.CanvasExampleTheme
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pathPropertiesViewModel: PathPropertiesViewModel by viewModels()
        val drawingInfoViewModel: DrawingInfoViewModel by viewModels { DrawingInfoViewModelFactory((application as DrawingApplication).drawingInfoRepository) }

        setContent {
            CanvasExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(pathPropertiesViewModel, drawingInfoViewModel)
                }
            }
        }
    }
}


@Composable
fun Navigation(
    pathPropertiesViewModel: PathPropertiesViewModel,
    drawingInfoViewModel: DrawingInfoViewModel
) {
    val navController = rememberNavController()
    val hexColorCodeString by pathPropertiesViewModel.hexColorCode.collectAsState()
    val currentPathProperty by pathPropertiesViewModel.currentPathProperty.collectAsState()
    val controller = rememberColorPickerController()


    // TODO: change startDestination to change the starting screen
    NavHost(navController = navController, startDestination = "drawingList") {
        // TODO: Pass in the whole viewModel is a bad practice, but if not passing in the whole viewModel, then we need to pass in a ton of variable and functions
        composable("canvasPage") {
            CanvasPage(
                navController,
                pathPropertiesViewModel,
                drawingInfoViewModel
            )
        }
        composable("penCustomizer") {
            PenCustomizer(
                hexColorCodeString,
                currentPathProperty,
                pathPropertiesViewModel::updateHexColorCode,
                pathPropertiesViewModel::updateCurrentPathProperty,
                controller
            )
        }
        composable("drawingList") {
            DrawingListScreen(
                navController,
                drawingInfoViewModel
            )
        }
    }
}
