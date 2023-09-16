package com.cs6018.canvasexample

import java.io.File
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