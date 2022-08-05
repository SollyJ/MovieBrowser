package com.example.moviebrowser.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moviebrowser.model.History

@Dao
interface HistoryDAO {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history WHERE `query` == :query")
    fun delete(query: String)
}