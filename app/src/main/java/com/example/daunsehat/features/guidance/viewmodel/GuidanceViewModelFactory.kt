package com.example.daunsehat.features.guidance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.daunsehat.data.repository.GuidanceRepository
import com.example.daunsehat.data.repository.UserRepository

class GuidanceViewModelFactory(
    private val userRepository: UserRepository,
    private val guidanceRepository: GuidanceRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuidanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GuidanceViewModel(userRepository, guidanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
