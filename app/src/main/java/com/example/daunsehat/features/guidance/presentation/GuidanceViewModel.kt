package com.example.daunsehat.features.guidance.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.daunsehat.data.remote.response.GuidanceResponse
import com.example.daunsehat.data.repository.GuidanceRepository
import com.example.daunsehat.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class GuidanceViewModel(
    private val userRepository: UserRepository,
    private val repository: GuidanceRepository
) : ViewModel() {

    fun getSession() = userRepository.getSession().asLiveData()

    private val _guidanceArticles = MutableStateFlow<List<GuidanceResponse>>(emptyList())
    val guidanceArticles: LiveData<List<GuidanceResponse>> get() = _guidanceArticles.asLiveData()

    private val _isSuccess = MutableStateFlow<Boolean>(false)

    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getArticlesByCategory(token: String, category: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getGuidanceArticlesByCategory(token, category)
                _guidanceArticles.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
