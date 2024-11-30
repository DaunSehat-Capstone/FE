package com.example.daunsehat.features.history.data.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "history")
@Parcelize
data class HistoryEntity(
    @PrimaryKey(autoGenerate = false)
    var image: String = "",
    var score: String? = null,
    var label: String = "",
    var description: String = "",
) : Parcelable