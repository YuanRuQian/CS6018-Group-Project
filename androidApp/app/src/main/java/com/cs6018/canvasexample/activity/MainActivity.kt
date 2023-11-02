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
import com.cs6018.canvasexample.data.PathPropertiesViewModel
import com.cs6018.canvasexample.data.ShakeDetectionViewModel
import com.cs6018.canvasexample.network.ApiViewModel
import com.cs6018.canvasexample.network.ApiViewModelFactory
import com.cs6018.canvasexample.ui.components.AuthenticationScreen
import com.cs6018.canvasexample.ui.components.CanvasPage
import com.cs6018.canvasexample.ui.components.DrawingListScreen
import com.cs6018.canvasexample.ui.components.PenCustomizer
import com.cs6018.canvasexample.ui.theme.CanvasExampleTheme
import com.cs6018.canvasexample.utils.DrawingApplication
import com.cs6018.canvasexample.utils.ShakeDetector
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity(), ShakeDetector.Listener {
    private lateinit var shakeDetectionViewModel: ShakeDetectionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiViewModel: ApiViewModel by viewModels { ApiViewModelFactory((application as DrawingApplication).apiRepository) }
        val capturableImageViewModel: CapturableImageViewModel by viewModels()
        val pathPropertiesViewModel: PathPropertiesViewModel by viewModels()

        shakeDetectionViewModel = viewModels<ShakeDetectionViewModel>().value
        val shakeDetectorListener = this

        setContent {
            CanvasExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        apiViewModel,
                        pathPropertiesViewModel,
                        capturableImageViewModel,
                        rememberNavController(),
                        shakeDetectionViewModel,
                        shakeDetectorListener,
                        ::createUserWithEmailAndPassword,
                        ::signInWithEmailAndPassword
                    )
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    private fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = Firebase.auth.currentUser
                    onSuccess(user)
                    Log.d(TAG, "user info: ${user?.email}")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: Exception) {
                        Log.e(TAG, "sign up failure: ${e.message}")
                        onFailure(e.message ?: "Sign up failed")
                    }
                }
            }
    }

    private fun signInWithEmailAndPassword(
        email: String, password: String, onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = Firebase.auth.currentUser
                    onSuccess(user)
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: Exception) {
                        Log.e(TAG, "Log in failure: ${e.message}")
                        onFailure(e.message ?: "Log in failed")
                    }
                }
            }
    }

    companion object {
        private const val TAG = "Authentication"
    }

    override fun hearLightShake() {
        shakeDetectionViewModel.setAsLightShake()
    }

    override fun hearHardShake() {
        shakeDetectionViewModel.setAsHardShake()
    }

}


@Composable
fun Navigation(
    apiViewModel: ApiViewModel,
    pathPropertiesViewModel: PathPropertiesViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    navController: NavHostController,
    shakeDetectionViewModel: ShakeDetectionViewModel,
    shakeDetectorListener: ShakeDetector.Listener,
    createUserWithEmailAndPassword: (
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) -> Unit,
    signInWithEmailAndPassword: (
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) -> Unit,
) {
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

    val navigateToDrawingList = {
        navController.navigate("drawingList") {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    val navigateToAuthenticationScreen = {
        navController.navigate("authentication") {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    val currentUserDrawingHistory by apiViewModel.currentUserDrawingHistory.observeAsState()
    val currentUserExploreFeed by apiViewModel.currentUserExploreFeed.observeAsState()

    // Completed SplashScreen: change startDestination to splash screen
    NavHost(navController = navController, startDestination = "splash") {
        // TODO: Pass in the whole viewModel is a bad practice, but if not passing in the whole viewModel, then we need to pass in a ton of variable and functions

        composable("authentication") {
            AuthenticationScreen(
                createUserWithEmailAndPassword,
                signInWithEmailAndPassword,
                navigateToDrawingList,
                apiViewModel::getCurrentUserDrawingHistory
            )
        }

        composable("splash") {
            SplashScreen {
                val currentUser = Firebase.auth.currentUser
                val isSignedIn = currentUser != null
                if (isSignedIn) {
                    apiViewModel.getCurrentUserDrawingHistory(Firebase.auth.currentUser?.uid ?: "")
                    navController.navigate("drawingList")
                } else {
                    navController.navigate("authentication")
                }
            }
        }

        composable("canvasPage") {
            CanvasPage(
                pathPropertiesViewModel,
                apiViewModel,
                capturableImageViewModel,
                navigateToPenCustomizer,
                navigateToPopBack,
                shakeDetectorListener,
                shakeDetectionViewModel
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
                apiViewModel,
                navigateToCanvasPage,
                apiViewModel::setActiveDrawingInfoById,
                currentUserDrawingHistory,
                currentUserExploreFeed,
                apiViewModel::deleteDrawingById,
                navigateToAuthenticationScreen
            )
        }
    }
}

@Composable
fun SplashScreen(
    onSplashScreenComplete: () -> Unit
) {

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
            // Log the error for debugging purposes
            Log.e("SplashScreen", "Error: The SplashScreen image was not loaded correctly! ")
        } finally {
            coroutineScope.launch {
                onSplashScreenComplete()
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
