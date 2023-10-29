package com.cs6018.canvasexample

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cs6018.canvasexample.network.ApiService
import com.cs6018.canvasexample.network.DrawingPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApiServiceTest {

    private lateinit var apiService: ApiService
    private lateinit var scope: CoroutineScope

    @Before
    fun setUp() {
        apiService = ApiService()
        scope = CoroutineScope(Dispatchers.IO)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun testGetAllDrawings() {
        scope.launch {
            val dataToPost = DrawingPost("testCreatorId", "test title", "test image path")

            apiService.postNewDrawing(dataToPost)

            val drawings = apiService.getAllDrawings()
            assert(drawings.size == 1)

            var drawing = drawings[0]
            assert(drawing.creatorId == "testCreatorId")
            assert(drawing.title == "test title")
            assert(drawing.imagePath == "test image path")

            val changedDate = DrawingPost("testCreatorId", "test title updated", "test image path updated")
            apiService.updateDrawingById(drawing.id, changedDate)

            drawing = apiService.getDrawingById(drawing.id)
            assert(drawing.creatorId == "testCreatorId")
            assert(drawing.title == "test title updated")
            assert(drawing.imagePath == "test image path updated")

            apiService.deleteDrawingById(drawing.id)
            assert(apiService.getAllDrawings().isEmpty())
        }
    }
}
