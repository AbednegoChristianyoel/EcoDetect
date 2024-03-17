package com.example.ecodetect.ui.daftardaurulang

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.ecodetect.data.response.RecommendationResponseItem
import com.example.ecodetect.repository.EcoDetectRepository

class DaurUlangViewModel (private val repository: EcoDetectRepository) : ViewModel() {
    fun getRecommendationsByType(jenis: String, context: Context): List<RecommendationResponseItem?>? {
        return repository.getRecommendationsByType(jenis, context)
    }
}