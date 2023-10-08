package com.cs6018.canvasexample

import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.cs6018.canvasexample.DrawingInfoRepository as Repository

@RunWith(AndroidJUnit4::class)
class DrawingInfoViewModelTest {
    private lateinit var dao: DrawingInfoDAO
    private lateinit var db: DrawingInfoDatabase
    private lateinit var repository: Repository
    private lateinit var scope: CoroutineScope
    private lateinit var viewModel: DrawingInfoViewModel

    @Before
    fun setup() {
        // create a database in memory
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DrawingInfoDatabase::class.java
        ).build()
        dao = db.drawingInfoDao()
        scope = CoroutineScope(Dispatchers.IO)
        repository = Repository(scope, dao)
        viewModel = DrawingInfoViewModel(repository)
    }

    @Test
    fun testAddDrawingInfo() {
        val drawingInfoList = generateRandomTestDrawingInfoList(10)
        for (drawingInfo in drawingInfoList) {
            viewModel.addDrawingInfo(drawingInfo.drawingTitle, null, null)
        }

        runBlocking {
            val lifecycleOwner = TestLifecycleOwner()

            lifecycleOwner.run {
                withContext(Dispatchers.Main) {
                    val allDrawing = dao.allDrawingInfo().asLiveData()
                    allDrawing.observe(lifecycleOwner, object : Observer<List<DrawingInfo>> {
                        override fun onChanged(value: List<DrawingInfo>) {
                            assert(value.containsAll(drawingInfoList))
                            allDrawing.removeObserver(this)
                        }
                    })
                }
            }
        }
    }

    @Test
    fun testActiveCapturedImage() {
        val bitmap = generateRandomBitmap(100, 100)

        scope.launch {
            viewModel.setActiveCapturedImage(bitmap)
            val fetchedViewModel = viewModel.getActiveCapturedImage().value
            assert(fetchedViewModel == bitmap)
        }
    }

    @Test
    fun testUpdateThumbnail() {
        val thumbnail = generateRandomByteArray(100)

        scope.launch {
            viewModel.setActiveDrawingInfoById(1)
            viewModel.updateThumbnailForActiveDrawingInfo(thumbnail)
            val fetchedDrawingInfo = db.drawingInfoDao().fetchDrawingInfoWithId(1).firstOrNull()
            assert(fetchedDrawingInfo?.thumbnail?.contentEquals(thumbnail) ?: false)
        }
    }

    @Test
    fun testAddDrawingInfoWithRecentCapturedImage() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val bitmap = generateRandomBitmap(100, 100)

        scope.launch {
            viewModel.setActiveCapturedImage(bitmap)
            viewModel.setActiveDrawingInfoById(1)
            val imagePath = viewModel.addDrawingInfoWithRecentCapturedImage(context)
            assert(imagePath != null)
            println("Image path: $imagePath")

            // delete test image to prevent memory leak
            deleteImageFile(imagePath, context)
            assert(!doesFileExist(imagePath))
        }
    }

    @Test
    fun testDeleteDrawingInfo() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        scope.launch {
            val drawingInfoList = viewModel.allDrawingInfo.value
            if (drawingInfoList != null) {
                for (drawingInfo in drawingInfoList) {
                    viewModel.deleteDrawingInfoWithId(drawingInfo, context)
                    assert(viewModel.allDrawingInfo.value?.contains(drawingInfo) == false)
                }
            } else {
                assert(false)
            }
        }
    }
}