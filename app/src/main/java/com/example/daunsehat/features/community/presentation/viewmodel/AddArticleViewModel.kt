package com.example.daunsehat.features.community.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.repository.UserRepository
import java.io.File

class AddArticleViewModel(private val repository: UserRepository) : ViewModel()  {
    val currentImageUri: MutableLiveData<Uri?> = MutableLiveData(null)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun addArticle(token: String, file: File?, title: String, description: String) = repository.addArticle(token, file, title, description)
}