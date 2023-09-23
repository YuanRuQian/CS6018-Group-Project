package com.cs6018.canvasexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


// This factory class allows us to define custom constructors for the view model
class DrawingInfoViewModelFactory(private val repository: DrawingInfoRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawingInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrawingInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}