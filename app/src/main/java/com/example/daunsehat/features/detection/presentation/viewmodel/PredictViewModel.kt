package com.example.daunsehat.features.detection.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.remote.response.PredictResponse
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class PredictViewModel(private val repository: UserRepository) : ViewModel() {


    fun predictPlant(imageFile: File): LiveData<ResultApi<PredictResponse>> {
        return repository.predictPlant(imageFile)
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}