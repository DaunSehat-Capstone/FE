package com.example.daunsehat.features.history.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.remote.response.HistoryPredictResponseItem
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.data.repository.UserRepository

class HistoryViewModel(private val repository: UserRepository) : ViewModel() {

    val getHistoryPredict: LiveData<ResultApi<List<HistoryPredictResponseItem>>> = repository.getHistoryPredict()

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}