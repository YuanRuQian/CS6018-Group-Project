package com.cs6018.canvasexample.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import com.cs6018.canvasexample.data.CapturableImageViewModel
import com.cs6018.canvasexample.network.ApiViewModel
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.CaptureController

@Composable
fun CapturableWrapper(
    apiViewModel: ApiViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    content: @Composable () -> Unit,
    captureController: CaptureController
) {
    val context = LocalContext.current

    Capturable(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        controller = captureController,
        onCaptured = { bitmap, error ->
            if (bitmap != null) {
                Log.d("CanvasPage", "Bitmap is captured successfully.")
                val dataAsBitmap = bitmap.asAndroidBitmap()
                try {
                    apiViewModel.setActiveCapturedImage(dataAsBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("CanvasPage", "Error occurred while saving bitmap.")
                }
            }

            if (error != null) {
                Toast.makeText(context, "Error occurred while capturing bitmap.",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("CanvasPage", "Error occurred while capturing bitmap.")
            }

            capturableImageViewModel.fireSignal()
        }
    ) {
        content() // Render the content defined by the lambda
    }
}
