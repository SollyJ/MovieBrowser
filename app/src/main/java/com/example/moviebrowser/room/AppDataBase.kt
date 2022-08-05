package com.example.moviebrowser.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviebrowser.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract fun historyDAO(): HistoryDAO

    // 싱글톤으로 db생성
    companion object {
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase? {
            if(instance == null) {
                synchronized(AppDataBase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "History"
                    ).build()
                }
            }
            return instance
        }
    }
}