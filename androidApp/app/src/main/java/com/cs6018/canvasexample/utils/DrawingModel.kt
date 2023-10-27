package com.cs6018.canvasexample.utils

import kotlinx.serialization.Serializable

@Serializable
data class PostDrawingModel(
    val creatorId: String,
    val title: String,
    val imagePath: String
)

@Serializable
data class ResponseDrawingModel(
    val id: Int,
    val creatorId: String,
    val title: String,
    val imagePath: String,
    val lastModifiedDate: Long,
    val createdDate: Long
)

