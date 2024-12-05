package com.example.daunsehat.features.authentication.login.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.remote.response.LoginResponse
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun loginUser(email: String, password: String): LiveData<ResultApi<LoginResponse>> {
        return repository.loginUser(email, password)
    }
}