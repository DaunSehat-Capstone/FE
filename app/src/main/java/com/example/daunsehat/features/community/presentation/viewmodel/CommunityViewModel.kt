package com.example.daunsehat.features.community.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.remote.response.ListArticleItem
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.data.repository.UserRepository
import kotlinx.coroutines.launch

class CommunityViewModel(private val repository: UserRepository) : ViewModel() {

    val getAllArticle: LiveData<ResultApi<List<ListArticleItem>>> = repository.getAllArticle()

    fun getSearchArticle(query: String): LiveData<ResultApi<List<ListArticleItem>>> {
        return repository.getSearchArticle(query)
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