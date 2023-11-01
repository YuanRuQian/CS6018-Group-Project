package com.cs6018.canvasexample.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Log
import androidx.compose.ui.geometry.Size
import com.cs6018.canvasexample.network.UserDrawing
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
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

fun getCurrentUserId(): String {
    return Firebase.auth.currentUser?.uid ?: ""
}

fun sortDrawingsByLastModifiedDate(drawings: List<UserDrawing>): List<UserDrawing> {
    return drawings.sortedByDescending { it.lastModifiedDate }
}

fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun scaleBitmapToCertainSize(bitmap: Bitmap, size: Size): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, size.width.toInt(), size.height.toInt(), false)
}


fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)(@)(.+)([.])(.+)$"
    return email.matches(emailRegex.toRegex())
}

/*
Password must contain:
At least one uppercase letter ((?=.*[A-Z])).
At least one lowercase letter ((?=.*[a-z])).
At least one digit ((?=.*\\d)).
At least one special character from the set of characters @#$%^&+= ((?=.*[@#$%^&+=])).
Minimum password length of 8 characters (.{8,}).
No whitespace characters allowed ((?!.*\\s)).
*/

fun isValidPassword(password: String): Boolean {
    val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=])(?!.*\\s).{8,}$")
    return passwordPattern.matches(password)
}
