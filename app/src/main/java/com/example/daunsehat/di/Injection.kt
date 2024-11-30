package com.example.daunsehat.di

import android.content.Context
import com.example.daunsehat.features.history.data.repository.HistoryRepository
import com.example.daunsehat.features.history.data.room.HistoryDatabase

object Injection {
    fun provideRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        return HistoryRepository.getInstance(dao)
    }
}