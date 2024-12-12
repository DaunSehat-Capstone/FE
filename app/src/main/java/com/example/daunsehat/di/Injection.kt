package com.example.daunsehat.di

import android.content.Context
import com.example.daunsehat.data.pref.UserPreference
import com.example.daunsehat.data.pref.dataStore
import com.example.daunsehat.data.remote.retrofit.ApiConfig
import com.example.daunsehat.data.repository.UserRepository

object Injection {

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}