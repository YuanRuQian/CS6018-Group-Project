package com.cs6018.canvasexample



import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel



class DrawingInfoViewModel(private val repository: DrawingInfoRepository) : ViewModel() {

    val allDrawingInfo: LiveData<List<DrawingInfo>> = repository.allDrawingInfo

    fun addDrawingInfo(title: String, imageUrl: String?, thumbnail: ByteArray?){
        repository.addNewDrawingInfo(title, imageUrl, thumbnail)
    }

}