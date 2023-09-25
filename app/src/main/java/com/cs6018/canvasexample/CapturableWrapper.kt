package com.cs6018.canvasexample

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.CaptureController

@Composable
fun CapturableWrapper(
    drawingInfoViewModel: DrawingInfoViewModel,
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
                    drawingInfoViewModel.setActiveCapturedImage(dataAsBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error occurred while saving bitmap.",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("CanvasPage", "Error occurred while saving bitmap.")
                }
            }

            if (error != null) {
                Toast.makeText(context, "Error occurred while capturing bitmap.",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("CanvasPage", "Error occurred while capturing bitmap.")
            }
        }
    ) {
        content() // Render the content defined by the lambda
    }
}
