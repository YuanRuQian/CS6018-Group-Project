package com.cs6018.canvasexample

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingInfoDAO {
    @Insert
    suspend fun addDrawingInfo(data: DrawingInfo)

    @Query("SELECT * from drawing_info ORDER BY lastModifiedDate DESC")
    fun allDrawingInfo() : Flow<List<DrawingInfo>>
}