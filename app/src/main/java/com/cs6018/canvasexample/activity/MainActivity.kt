package com.cs6018.canvasexample.activity

import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs6018.canvasexample.R
import com.cs6018.canvasexample.data.CapturableImageViewModel
import com.cs6018.canvasexample.data.DrawingInfoViewModel
import com.cs6018.canvasexample.data.DrawingInfoViewModelFactory
import com.cs6018.canvasexample.data.PathPropertiesViewModel
import com.cs6018.canvasexample.ui.components.CanvasPage
import com.cs6018.canvasexample.ui.components.DrawingListScreen
import com.cs6018.canvasexample.ui.components.PenCustomizer
import com.cs6018.canvasexample.ui.theme.CanvasExampleTheme
import com.cs6018.canvasexample.utils.DrawingApplication
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val capturableImageViewModel: CapturableImageViewModel by viewModels()
        val pathPropertiesViewModel: PathPropertiesViewModel by viewModels()
        val drawingInfoViewModel: DrawingInfoViewModel by viewModels { DrawingInfoViewModelFactory((application as DrawingApplication).drawingInfoRepository) }

        setContent {
            CanvasExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        pathPropertiesViewModel,
                        drawingInfoViewModel,
                        capturableImageViewModel,
                        rememberNavController()
                    )
                }
            }
        }
    }
}


@Composable
fun Navigation(
    pathPropertiesViewModel: PathPropertiesViewModel,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    navController: NavHostController,
    isTest:Boolean = false
) {
//    val navController = rememberNavController()
    val hexColorCodeString by pathPropertiesViewModel.hexColorCode.collectAsState()
    val currentPathProperty by pathPropertiesViewModel.currentPathProperty.collectAsState()
    val controller = rememberColorPickerController()

    val navigateToPenCustomizer = {
        navController.navigate("penCustomizer")
    }
    val navigateToCanvasPage = {
        navController.navigate("canvasPage")
    }
    val navigateToPopBack = {
        navController.popBackStack()
    }

    val drawingInfoDataList by drawingInfoViewModel.allDrawingInfo.observeAsState()


    // Completed SplashScreen: change startDestination to splash screen
    NavHost(navController = navController, startDestination = "splash") {
        // TODO: Pass in the whole viewModel is a bad practice, but if not passing in the whole viewModel, then we need to pass in a ton of variable and functions

        composable("splash") {
            SplashScreen ({
                navController.navigate("drawingList")
            }, isTest)
        }

        composable("canvasPage") {
            CanvasPage(
                pathPropertiesViewModel,
                drawingInfoViewModel,
                capturableImageViewModel,
                navigateToPenCustomizer,
                navigateToPopBack
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
                navigateToCanvasPage,
                drawingInfoViewModel::setActiveCapturedImage,
                drawingInfoViewModel::setActiveDrawingInfoById,
                drawingInfoDataList,
                drawingInfoViewModel::deleteDrawingInfoWithId
            )
        }
    }
}

@Composable
fun SplashScreen(
    onSplashScreenComplete: ()-> Unit,
    isTest: Boolean = false) {

    val scale = remember {
        Animatable(0f)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        try {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = {
                        OvershootInterpolator(1.5f).getInterpolation(it)
                    }
                )
            )
            delay(1500)
        } catch (e: Exception) {
            if(!isTest) {
                // Log the error for debugging purposes
                Log.e("SplashScreen", "Error: The SplashScreen image was not loaded correctly! ")
            }
        } finally {
            if(!isTest) {
                coroutineScope.launch {
                    onSplashScreenComplete()
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.size(150.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Splash Icon",
                modifier = Modifier.scale(scale.value)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Draw Better Than It",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.scale(scale.value)
        )
    }
}
