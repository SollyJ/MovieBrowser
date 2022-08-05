package com.example.moviebrowser.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey val key: Int?,
    @ColumnInfo(name = "query") val query: String
    )