package com.example.daunsehat.di

import android.content.Context
import com.example.daunsehat.data.pref.UserPreference
import com.example.daunsehat.data.pref.dataStore
import com.example.daunsehat.data.remote.retrofit.ApiConfig
import com.example.daunsehat.data.repository.UserRepository
import com.example.daunsehat.features.history.data.repository.HistoryRepository
import com.example.daunsehat.features.history.data.room.HistoryDatabase

object Injection {
    fun provideHistoryRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        return HistoryRepository.getInstance(dao)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}