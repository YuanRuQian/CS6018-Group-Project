package com.cs6018.canvasexample

import android.util.Log
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
                    saveImage(dataAsBitmap, context)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("CanvasPage", "Error occurred while saving bitmap.")
                }
            }

            if (error != null) {
                Log.d("CanvasPage", "Error occurred while capturing bitmap.")
            }
        }
    ) {
        content() // Render the content defined by the lambda
    }
}
