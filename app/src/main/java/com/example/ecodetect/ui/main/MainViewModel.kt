package com.example.ecodetect.ui.main

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecodetect.data.response.PredictionResult
import com.example.ecodetect.repository.EcoDetectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: EcoDetectRepository) : ViewModel() {

    suspend fun classifyImage(image: Bitmap, context: Context): LiveData<PredictionResult> {
        val resultLiveData = repository.classifyImage(image, context)

        viewModelScope.launch(Dispatchers.IO) {
            repository.classifyImage(image, context)
        }

        return resultLiveData
    }
}
