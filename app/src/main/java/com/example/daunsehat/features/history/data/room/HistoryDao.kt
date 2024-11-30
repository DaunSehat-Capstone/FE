package com.example.daunsehat.features.history.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAllHistory(): LiveData<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE image = :name")
    suspend fun deleteHistory(name: String)
}