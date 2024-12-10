package com.example.daunsehat.features.profile.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.remote.response.ListArticleItem
import com.example.daunsehat.data.remote.response.ProfileResponse
import com.example.daunsehat.data.remote.response.UserArticleResponse
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    val getUserArticle: LiveData<ResultApi<List<ListArticleItem>>> = repository.getUserArticle()

    fun getProfile(): LiveData<ResultApi<ProfileResponse>> {
        return repository.getProfile()
    }

    fun editProfile(name: String, email: String): LiveData<ResultApi<ProfileResponse>> {
        return repository.editProfile(name, email)
    }

    fun editPhotoProfile(imageFile: File): LiveData<ResultApi<ProfileResponse>> {
        return repository.editPhotoProfile(imageFile)
    }

    fun deleteUserArticle(articleId: String): LiveData<ResultApi<UserArticleResponse>> {
        return repository.deleteUserArticle(articleId)
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
