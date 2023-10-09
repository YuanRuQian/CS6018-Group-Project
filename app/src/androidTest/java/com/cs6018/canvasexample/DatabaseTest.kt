package com.cs6018.canvasexample

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
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
            context, DrawingInfoDatabase::class.java
        ).build()
        dao = db.drawingInfoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testAddAndDeleteAndUpdateADrawing() {
        runBlocking {
            val lifecycleOwner = TestLifecycleOwner()
            val info = DrawingInfo(Date(), Date(), "TestImage", null, null)
            var count = 0
            lifecycleOwner.run {
                withContext(Dispatchers.Main) {
                    val allDrawing = dao.allDrawingInfo().asLiveData()
                    allDrawing
                        .observe(lifecycleOwner, object : Observer<List<DrawingInfo>> {
                            override fun onChanged(value: List<DrawingInfo>) {
                                when (count) {
                                    1 -> {
                                        Log.d("DBTest", "1-add test")
                                        Assert.assertEquals(1, value.size)
                                        Assert.assertEquals("TestImage", value[0].drawingTitle)
                                    }
                                    2 -> {
                                        Log.d("DBTest", "2-update title")
                                        Assert.assertEquals(1, value.size)
                                        Assert.assertEquals("New Title", value[0].drawingTitle)
                                    }

                                    3 -> {
                                        Log.d("DBTest", "3-delete test")
                                        Assert.assertEquals(0, value.size)
                                        allDrawing.removeObserver(this)
                                    }
                                }
                            }
                        })

                    dao.addDrawingInfo(info)
                    count += 1
                    dao.updateDrawingInfoTitle("New Title", 0)
                    count += 1
                    dao.deleteDrawingInfoWithId(0)
                    count += 1
                }
            }
        }
    }
}