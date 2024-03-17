package com.example.ecodetect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecodetect.di.Injection
import com.example.ecodetect.ui.daftardaurulang.DaurUlangViewModel
import com.example.ecodetect.ui.main.MainViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(Injection.provideRepository()) as T
        } else if (modelClass.isAssignableFrom(DaurUlangViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DaurUlangViewModel(Injection.provideRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
