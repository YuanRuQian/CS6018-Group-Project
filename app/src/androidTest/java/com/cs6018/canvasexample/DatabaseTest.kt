package com.cs6018.canvasexample

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
    private var count = 0

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
    fun testAddADrawing() {
        runBlocking {
            val lifecycleOwner = TestLifecycleOwner()
            val info = DrawingInfo(Date(), Date(), "TestImage", null, null)
            lifecycleOwner.run {
                withContext(Dispatchers.Main) {
                    dao.allDrawingInfo().asLiveData().observe(lifecycleOwner) {
                        Assert.assertEquals("TestImage", dao.fetchDrawingInfoWithId(0).asLiveData().value?.drawingTitle)
                    }
                    dao.addDrawingInfo(info)
                    count += 1
                }
            }
        }
    }

    @Test
    fun testDeleteADrawing() {
        runBlocking {
            val lifecycleOwner = TestLifecycleOwner()
            val info = DrawingInfo(Date(), Date(), "TestImage", null, null)
            lifecycleOwner.run {
                withContext(Dispatchers.Main) {
                    dao.allDrawingInfo().asLiveData().observe(lifecycleOwner) {
                        Assert.assertEquals(count, it.size)
                    }
                    dao.addDrawingInfo(info)
                    count += 1
                    dao.deleteDrawingInfoWithId(0)
                    count -= 1
                }
            }
        }
    }

}