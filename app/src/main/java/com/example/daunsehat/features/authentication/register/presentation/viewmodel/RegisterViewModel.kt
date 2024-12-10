package com.example.daunsehat.features.authentication.register.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.daunsehat.data.remote.response.RegisterResponse
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.data.repository.UserRepository

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    fun registerUser(email: String, name: String, password: String): LiveData<ResultApi<RegisterResponse>> {
        return liveData {
            emit(ResultApi.Loading)
            try {
                val response = repository.registerUser(email, name, password)
                emit(response)
            } catch (e: Exception) {
                emit(ResultApi.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}

