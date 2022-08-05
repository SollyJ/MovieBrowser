package com.example.moviebrowser

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.moviebrowser.adapter.HistoryAdapter
import com.example.moviebrowser.databinding.ActivityHistoryBinding
import com.example.moviebrowser.room.AppDataBase

class HistoryActivity: AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var db: AppDataBase
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDataBase.getInstance(applicationContext)!!

        showHistory()

        historyAdapter = HistoryAdapter()
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun showHistory() {
        Thread( Runnable {
            db.historyDAO()
                .getAll()
                .reversed()
                .run{
                    runOnUiThread {
                        historyAdapter.submitList(this)
                    }
                }
        }).start()
    }
}