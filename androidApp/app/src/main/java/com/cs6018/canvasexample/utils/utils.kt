package com.cs6018.canvasexample.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(date)
}

fun getCurrentDateTimeString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
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
        Toast.makeText(context, "Image was saved to successfully", Toast.LENGTH_LONG).show()

        return imageUri // Return the saved image's URI as a string
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("CanvasPage", "Error occurred while saving image: ${e.message}")
        Toast.makeText(
            context,
            "Error occurred while saving image: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }

    return null // Return null in case of an error
}

fun doesFileExist(filePath: String?): Boolean {
    if (filePath != null) {
        val imageFile = File(Uri.parse(filePath).path ?: "")
        return imageFile.exists()
    }

    return false
}

fun deleteImageFile(imagePath: String?, context: Context): Boolean {
    if (imagePath != null) {
        try {
            val imageFile = File(Uri.parse(imagePath).path ?: "")
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                if (deleted) {
                    Log.d("CanvasPage", "Image deleted: $imagePath")
                    Toast.makeText(context, "Image deleted", Toast.LENGTH_LONG).show()
                    return true
                } else {
                    Log.e("CanvasPage", "Failed to delete image: $imagePath")
                    Toast.makeText(context, "Failed to delete image", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e("CanvasPage", "Image file not found: $imagePath")
                Toast.makeText(context, "Image file not found", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CanvasPage", "Error occurred while deleting image: ${e.message}")
            Toast.makeText(
                context,
                "Error occurred while deleting image: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    } else {
        Log.e("CanvasPage", "Invalid image path")
        Toast.makeText(context, "Invalid image path", Toast.LENGTH_LONG).show()
    }

    return false
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
        Toast.makeText(context, "Image changes were saved successfully", Toast.LENGTH_LONG).show()

        return imageUri.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("CanvasPage", "Error occurred while saving image: ${e.message}")
        Toast.makeText(
            context,
            "Error occurred while saving image: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }

    return null // Return null in case of an error
}


fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(
        Bitmap.CompressFormat.PNG,
        100,
        outputStream
    ) // Compress as PNG or JPEG based on your preference
    return outputStream.toByteArray()
}

fun bitmapToBase64String(bitmap: Bitmap): String {
    val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 150, 150)
    val byteArray = bitmapToByteArray(thumbnail)
    val sizeInKB = byteArray.size / 1024.0
    Log.d("bitmapToBase64String", "Thumbnail size: $sizeInKB KB")
    return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
}

fun base64StringToBitmap(base64String: String): Bitmap {
    val byteArray = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
    return BitmapFactory
        .decodeByteArray(byteArray, 0, byteArray.size)
}

data class GoogleApiClientConfig(
    val web: WebClient
)

data class WebClient(
    val client_id: String,
    val project_id: String,
    val auth_uri: String,
    val token_uri: String,
    val auth_provider_x509_cert_url: String,
    val client_secret: String,
    val redirect_uris: List<String>,
    val javascript_origins: List<String>
)

fun parseClientID(context: Context): String {
    val jsonString = context.assets.open("client_secret/client_secret.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val clientConfigToken = object : TypeToken<GoogleApiClientConfig>() {}.type
    val clientConfig: GoogleApiClientConfig = gson.fromJson(jsonString, clientConfigToken)
    return clientConfig.web.client_id
}