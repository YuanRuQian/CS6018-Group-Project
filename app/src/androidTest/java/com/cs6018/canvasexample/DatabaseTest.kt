package com.cs6018.canvasexample

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var dao: DrawingInfoDAO
    private lateinit var db: DrawingInfoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DrawingInfoDatabase::class.java).build()
        dao = db.drawingInfoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addADrawing() = runBlocking {
        var beforeSize = -1
        var newSize = -1

        // Collect the old size
        val beforeJob = launch {
            dao.allDrawingInfo().collect { list ->
                beforeSize = list.size
                println("Collected beforeSize: $beforeSize")
            }
        }

        // Add a new drawing info
        val info = DrawingInfo(Date(), Date(), "TestImage", null, null)
        dao.addDrawingInfo(info)

        // Collect the new size
        val newJob = launch {
            dao.allDrawingInfo().collect { list ->
                newSize = list.size
            }
        }

        // Wait for both jobs to complete
        beforeJob.join()
        newJob.join()

        // Assert that the new size is one greater than the old size
        Assert.assertEquals(beforeSize + 1, newSize)
    }
}