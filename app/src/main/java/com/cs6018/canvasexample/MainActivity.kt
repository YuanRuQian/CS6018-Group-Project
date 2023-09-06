package com.cs6018.canvasexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs6018.canvasexample.ui.theme.CanvasExampleTheme
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CanvasExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}


@Composable
fun Navigation(viewModel: PathPropertiesViewModel = viewModel()) {
    val navController = rememberNavController()
    val hexColorCodeString by viewModel.hexColorCode.collectAsState()
    val currentPathProperty by viewModel.currentPathProperty.collectAsState()
    val controller = rememberColorPickerController()

    NavHost(navController = navController, startDestination = "playground") {
        // TODO: Pass in the whole viewModel is a bad practice, but if not passing in the whole viewModel, then we need to pass in a ton of variable and functions
        composable("playground") { Playground(navController, viewModel) }
        composable("penCustomizer") {
            PenCustomizer(
                hexColorCodeString,
                currentPathProperty,
                viewModel::updateHexColorCode,
                viewModel::updateCurrentPathProperty,
                controller
            )
        }
    }
}
