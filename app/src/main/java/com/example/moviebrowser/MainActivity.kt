package com.example.moviebrowser

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.moviebrowser.model.MovieDTO
import com.example.moviebrowser.service.MovieService
import com.example.moviebrowser.adapter.MovieAdapter
import com.example.moviebrowser.databinding.ActivityMainBinding
import com.example.moviebrowser.model.History
import com.example.moviebrowser.room.AppDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieService: MovieService
    private lateinit var db: AppDataBase
    private lateinit var queue: Queue<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSearchButton()
        initRecentSearchButton()
        initMovieListRecyclerView()

        // 검색 기록을 저장하기 위한 room 생성
        db = AppDataBase.getInstance(applicationContext)!!

        // 최근 검색 기록의 자료구조는 큐 형태로 구현
        queue = LinkedList()

        // retrofit 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        movieService = retrofit.create(MovieService::class.java)

    }

    private fun initSearchButton() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager   // 키보드내리기

        binding.searchButton.setOnClickListener() {
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

            search(binding.searchEditText.text.toString())
        }
    }

    // 최근 검색 버튼 누르면 HistoryActivity로 전환
    private fun initRecentSearchButton() {
        binding.recentSearchButton.setOnClickListener() {
            startActivity(Intent(this,HistoryActivity::class.java))
        }
    }

    // 검색 결과 보여주기 & 검색 기록 저장
    private fun search(query: String) {
        movieService.getMovieList(getString(R.string.clientId), getString(R.string.clientSecret), query)
            .enqueue(object: Callback<MovieDTO> {
                override fun onResponse(call: Call<MovieDTO>, response: Response<MovieDTO>) {
                    saveHistory(query)   // 검색어 저장

                    if(response.isSuccessful.not()) {   // 예외처리
                        Log.d("MainActivity", "RESPONSE FAIL")
                        return
                    }

                    // 성공처리
                    response.body()?.let {
                        movieAdapter.submitList(it.movies)   // 검색 결과 보여주기
                    }
                }

                override fun onFailure(call: Call<MovieDTO>, t: Throwable) {
                    Log.d("MainActivity", "FAIL!!")
                }

            })
    }

    private fun initMovieListRecyclerView() {
        movieAdapter = MovieAdapter()
        binding.movieListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.movieListRecyclerView.adapter = movieAdapter
    }

    private fun saveHistory(query: String) {
        if(queue.size >= 10) {
            Thread ( Runnable {
                db.historyDAO().insertHistory(History(null, query))
                queue.add(query)
                db.historyDAO().delete(queue.poll()!!)
            }).start()
        }

        else {
            Thread ( Runnable {
                db.historyDAO().insertHistory(History(null, query))
                queue.add(query)
            }).start()
        }
    }

}