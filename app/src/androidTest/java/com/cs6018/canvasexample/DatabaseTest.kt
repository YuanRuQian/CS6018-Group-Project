package com.cs6018.canvasexample

import android.content.Context
import androidx.compose.runtime.livedata.observeAsState
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
    fun addADrawing() {
        runBlocking {
            val lifecycleOwner = TestLifecycleOwner()
            val info = DrawingInfo(Date(), Date(), "TestImage", null, null)
            lifecycleOwner.run {
                withContext(Dispatchers.Main) {
                    dao.allDrawingInfo().asLiveData().observe(lifecycleOwner) {
                        Assert.assertTrue(it.contains(info))
                    }
                    dao.addDrawingInfo(info)
                }
            }
        }
    }
}