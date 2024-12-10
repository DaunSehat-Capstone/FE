package com.example.daunsehat.features.community.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.remote.response.DetailArticleResponse
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.data.repository.UserRepository

class DetailArticleViewModel(private val repository: UserRepository) : ViewModel() {
    private val _article = MutableLiveData<DetailArticleResponse>()
    val article: LiveData<DetailArticleResponse> = _article

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetailArticle(storyId: String): LiveData<ResultApi<DetailArticleResponse>> {
        return repository.getDetailArticle(storyId)
    }
}