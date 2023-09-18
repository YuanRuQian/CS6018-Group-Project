package com.cs6018.canvasexample

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun isImage(file: File): Boolean {
    val imageExtensions = arrayOf(".jpg", ".jpeg", ".png", ".gif")
    val fileName = file.name.lowercase()

    for (extension in imageExtensions) {
        if (fileName.endsWith(extension)) {
            return true
        }
    }

    return false
}

fun getCurrentDateTimeString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
}


// TODO: get the proper URI of the image file
fun getUriOfLastFile(context: Context): Uri? {
    // Get the directory for storing files in external storage
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    if (directory != null && directory.exists() && directory.isDirectory) {
        // List all files in the directory
        val files = directory.listFiles()

        if (files == null || files.isEmpty()) {
            Log.d("CanvasPage", "No files found in the directory, please create an image first")
            return null
        }

        if (isImage(files[files.size - 1])) {
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                files[files.size - 1]
            )
        }

    }

    return null
}

fun saveImage(bitmap: Bitmap, context: Context) {
    try {
        val imageFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${getCurrentDateTimeString()}.jpg"
        )

        val outputStream = FileOutputStream(imageFile)
        val quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        Log.d("CanvasPage", "Image saved to ${imageFile.toUri()}")
        Toast.makeText(context, "Image saved to ${imageFile.toUri()}", LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("CanvasPage", "Error occurred while saving image: ${e.message}")
        Toast.makeText(context, "Error occurred while saving image: ${e.message}", LENGTH_LONG).show()
    }
}
