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
            val drawingInfoSize = 10
            val drawingInfo = generateRandomTestDrawingInfoList(drawingInfoSize)
            var count = 0
            lifecycleOwner.run {
                withContext(Dispatchers.Main) {
                    val allDrawing = dao.allDrawingInfo().asLiveData()
                    allDrawing
                        .observe(lifecycleOwner, object : Observer<List<DrawingInfo>> {
                            override fun onChanged(value: List<DrawingInfo>) {
                                when (count) {
                                    drawingInfoSize -> {
                                        Log.d("DBTest", "add test")
                                        Assert.assertTrue(
                                            drawingInfo.containsAll(value)
                                        )
                                        Assert.assertTrue(
                                            isDrawingInfoListOrderedByLastModifiedDateByDesc(value)
                                        )
                                    }

                                    drawingInfoSize * 2 -> {
                                        Log.d("DBTest", "update title")
                                        value.forEach {
                                            Assert.assertEquals("New Title", it.drawingTitle)
                                        }
                                    }

                                    drawingInfoSize * 3 -> {
                                        Log.d("DBTest", "delete test")
                                        Assert.assertEquals(
                                            0,
                                            value.size
                                        )
                                        allDrawing.removeObserver(this)
                                    }

                                    else -> {
                                        Assert.assertTrue(
                                            isDrawingInfoListOrderedByLastModifiedDateByDesc(value)
                                        )
                                    }
                                }
                            }
                        })

                    drawingInfo.forEach {
                        dao.addDrawingInfo(it)
                        count += 1
                    }

                    drawingInfo.forEach {
                        dao.updateDrawingInfoTitle("New Title", it.id)
                        count += 1
                    }

                    drawingInfo.forEach {
                        dao.deleteDrawingInfoWithId(it.id)
                        count += 1
                    }
                }
            }
        }
    }
}