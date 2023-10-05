package com.cs6018.canvasexample

import java.util.Date
import java.util.Random
import java.util.concurrent.TimeUnit

fun generateRandomTestDrawingInfoList(n: Int): List<DrawingInfo> {
    val drawingInfoList = mutableListOf<DrawingInfo>()
    val random = Random()

    for (i in 1..n) {
        // Generate random time intervals (in milliseconds) within a reasonable range
        val createdTimeMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(random.nextInt(365)
            .toLong())

        // Generate a random interval for last modified time (should be >= created time)
        val lastModifiedTimeMillis = createdTimeMillis + TimeUnit.DAYS.toMillis(random.nextInt(365).toLong())

        // Create Date objects from the random time intervals
        val lastModifiedDate = Date(lastModifiedTimeMillis)
        val createdDate = Date(createdTimeMillis)

        val drawingInfo = DrawingInfo(lastModifiedDate, createdDate, "Drawing $i", null, null)
        drawingInfo.id = i
        drawingInfoList.add(drawingInfo)
    }

    return drawingInfoList
}
