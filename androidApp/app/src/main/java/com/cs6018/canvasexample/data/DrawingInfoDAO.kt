package com.cs6018.canvasexample.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cs6018.canvasexample.data.DrawingInfo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DrawingInfoDAO {
    @Insert
    suspend fun addDrawingInfo(data: DrawingInfo)

    @Query("SELECT * from drawing_info ORDER BY lastModifiedDate DESC")
    fun allDrawingInfo(): Flow<List<DrawingInfo>>

    @Query("SELECT * FROM drawing_info WHERE id = :id")
    fun fetchDrawingInfoWithId(id: Int): Flow<DrawingInfo?>

    @Query("UPDATE drawing_info SET drawingTitle = :title WHERE id = :id")
    suspend fun updateDrawingInfoTitle(
        title: String,
        id: Int
    )

    @Query("UPDATE drawing_info SET thumbnail = :thumbnailByteArray, lastModifiedDate = :lastModifiedTime WHERE id = :id")
    suspend fun updateDrawingInfoThumbnailAndLastModifiedTimeWithId(
        thumbnailByteArray: ByteArray,
        lastModifiedTime: Date,
        id: Int
    )

    @Query("DELETE FROM drawing_info WHERE id = :id")
    suspend fun deleteDrawingInfoWithId(id: Int)

    @Query("DELETE FROM drawing_info")
    suspend fun deleteAllDrawingInfo()
}
