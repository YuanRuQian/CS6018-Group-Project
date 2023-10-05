package com.cs6018.canvasexample

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import java.io.IOException

// reference: https://blog.devgenius.io/testing-room-database-with-coroutines-and-flows-testing-fundamentals-iii-5f6c3b9e4c94
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
): TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DatabaseTest {

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var dao: DrawingInfoDAO
    private lateinit var db: DrawingInfoDatabase
    private val testData = generateTestDrawingInfoList()

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
    fun addItem_shouldReturn_theItem_inFlow() = runTest {

        testData.forEach { dao.addDrawingInfo(it) }

        val allDrawingInfo =  dao.allDrawingInfo()
//        allDrawingInfo.test{
//
//        }
    }
}