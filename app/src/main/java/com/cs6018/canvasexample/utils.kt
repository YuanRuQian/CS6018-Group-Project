package com.cs6018.canvasexample

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
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

fun saveImage(bitmap: Bitmap, context: Context): String? {
    try {
        val imageFileName = "${getCurrentDateTimeString()}.jpg"
        val imageFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            imageFileName
        )

        val outputStream = FileOutputStream(imageFile)
        val quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        val imageUri = imageFile.toUri().toString() // Convert the URI to a string

        Log.d("CanvasPage", "Image saved to $imageUri")
        Toast.makeText(context, "Image saved to $imageUri", Toast.LENGTH_LONG).show()

        return imageUri // Return the saved image's URI as a string
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("CanvasPage", "Error occurred while saving image: ${e.message}")
        Toast.makeText(context, "Error occurred while saving image: ${e.message}", Toast.LENGTH_LONG).show()
    }

    return null // Return null in case of an error
}


fun overwriteCurrentImageFile(bitmap: Bitmap, context: Context, filePath: String): String? {
    Log.d("CanvasPage", "Overwriting the current image file at $filePath")
    try {
        val imageUri = Uri.parse(filePath)
        val imageFile = imageUri.path?.let { File(it) }

        val outputStream = FileOutputStream(imageFile)
        val quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        Log.d("CanvasPage", "Image saved to $imageUri")
        Toast.makeText(context, "Image saved to $imageUri", Toast.LENGTH_LONG).show()

        return imageUri.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("CanvasPage", "Error occurred while saving image: ${e.message}")
        Toast.makeText(context, "Error occurred while saving image: ${e.message}", Toast.LENGTH_LONG).show()
    }

    return null // Return null in case of an error
}


fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Compress as PNG or JPEG based on your preference
    return outputStream.toByteArray()
}