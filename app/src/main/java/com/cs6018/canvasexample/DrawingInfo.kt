package com.cs6018.canvasexample

import java.util.Date

data class DrawingInfo(
    val id: Int,
    val lastModifiedDate: Date,
    val createdDate: Date,
    val drawingTitle: String
)
class DrawingInfoComparator : Comparator<DrawingInfo> {
    override fun compare(drawing1: DrawingInfo, drawing2: DrawingInfo): Int {
        // sorted by last modified date
        return drawing2.lastModifiedDate.compareTo(drawing1.lastModifiedDate)
    }
}

