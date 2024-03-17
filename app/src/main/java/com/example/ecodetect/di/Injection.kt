package com.example.ecodetect.di

import com.example.ecodetect.repository.EcoDetectRepository

object Injection {
    fun provideRepository(): EcoDetectRepository {
        return EcoDetectRepository()
    }
}