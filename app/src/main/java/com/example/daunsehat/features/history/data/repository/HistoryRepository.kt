package com.example.daunsehat.features.history.data.repository

import com.example.daunsehat.features.history.data.room.HistoryDao

class HistoryRepository private constructor(private val historyDao: HistoryDao) {

    fun getAllHistory() = historyDao.getAllHistory()

    suspend fun delete(image: String) {
        historyDao.deleteHistory(image)
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(historyDao: HistoryDao) =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(historyDao).also { instance = it }
            }
    }
}