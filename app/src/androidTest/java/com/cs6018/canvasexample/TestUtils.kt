package com.cs6018.canvasexample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.security.SecureRandom
import java.util.Date
import java.util.Random
import java.util.concurrent.TimeUnit

fun generateRandomByteArray(length: Int): ByteArray {
    val random = SecureRandom()
    val byteArray = ByteArray(length)
    random.nextBytes(byteArray)
    return byteArray
}


fun generateRandomBitmap(width: Int, height: Int): Bitmap {
    // Create a new Bitmap with the specified width and height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Create a Canvas to draw on the Bitmap
    val canvas = Canvas(bitmap)

    // Create a Paint object for drawing
    val paint = Paint()

    // Create a random number generator
    val random = Random()

    // Fill the Bitmap with a random color
    val red = random.nextInt(256)
    val green = random.nextInt(256)
    val blue = random.nextInt(256)
    val color = Color.rgb(red, green, blue)
    canvas.drawColor(color)

    // Draw a random circle on the Bitmap
    val circleRadius = width / 4
    val circleX = random.nextInt(width)
    val circleY = random.nextInt(height)
    paint.color = Color.WHITE // Circle color
    canvas.drawCircle(circleX.toFloat(), circleY.toFloat(), circleRadius.toFloat(), paint)


    // Return the generated Bitmap
    return bitmap
}


fun generateRandomTestDrawingInfoList(n: Int): List<DrawingInfo> {
    val drawingInfoList = mutableListOf<DrawingInfo>()
    val random = Random()

    for (i in 1..n) {
        // Generate random time intervals (in milliseconds) within a reasonable range
        val createdTimeMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(
            random.nextInt(365)
                .toLong()
        )

        // Generate a random interval for last modified time (should be >= created time)
        val lastModifiedTimeMillis =
            createdTimeMillis + TimeUnit.DAYS.toMillis(random.nextInt(365).toLong())

        // Create Date objects from the random time intervals
        val lastModifiedDate = Date(lastModifiedTimeMillis)
        val createdDate = Date(createdTimeMillis)

        val drawingInfo = DrawingInfo(lastModifiedDate, createdDate, "Drawing $i", null, null)
        drawingInfo.id = i
        drawingInfoList.add(drawingInfo)
    }

    return drawingInfoList
}
