package com.example.moviebrowser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
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
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.searchButton.setOnClickListener() {
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)   // 키보드 내리기

            if(binding.searchEditText.text.isNullOrEmpty()) {   // 빈칸일때 검색버튼 누르면 토스트 메시지
                Toast.makeText(applicationContext, "검색어를 입력하세요!", Toast.LENGTH_SHORT).show()
            }
            else {
                search(binding.searchEditText.text.toString())
            }
        }
    }

    // 최근 검색 버튼 누르면 HistoryActivity로 전환
    private fun initRecentSearchButton() {
        binding.recentSearchButton.setOnClickListener() {
            startActivityForResult(Intent(this, HistoryActivity::class.java), 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if(resultCode != RESULT_OK)   return

        when(requestCode) {
            1000 -> {
                historySearch(intent?.getStringExtra("query").toString())
                Log.d("MainActivity", "search")
            }
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

                    response.body()?.let {   // 성공처리
                        movieAdapter.submitList(it.movies)   // 검색 결과 보여주기
                    }
                }

                override fun onFailure(call: Call<MovieDTO>, t: Throwable) {
                    Log.d("MainActivity", "FAIL!!")
                }
            })
    }

    private fun initMovieListRecyclerView() {
        movieAdapter = MovieAdapter(clickListener = { movie ->   // 영화 클릭하면
            val webViewIntent = Intent(this, WebViewActivity::class.java)
            webViewIntent.putExtra("url", movie.movieUrl)
            startActivity(webViewIntent)   // 웹뷰 실행
        })
        binding.movieListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.movieListRecyclerView.adapter = movieAdapter
    }

    private fun saveHistory(query: String) {
        if(queue.size >= 10) {
            Thread ( Runnable {
                db.historyDAO().insertHistory(History(null, query))
                queue.add(query)
                db.historyDAO().delete(queue.poll()!!)
                Log.d("MainActivity", queue.peek().toString())
            }).start()
        }

        else {
            Thread ( Runnable {
                db.historyDAO().insertHistory(History(null, query))
                queue.add(query)
                Log.d("MainActivity", queue.peek().toString())
            }).start()
        }
    }

    // 최근 검색 이력으로 검색했을때
    private fun historySearch(query: String) {
        movieService.getMovieList(getString(R.string.clientId), getString(R.string.clientSecret), query)
            .enqueue(object: Callback<MovieDTO> {
                override fun onResponse(call: Call<MovieDTO>, response: Response<MovieDTO>) {
                    saveHistory(query)   // 검색어 저장

                    if(response.isSuccessful.not()) {   // 예외처리
                        Log.d("MainActivity", "RESPONSE FAIL")
                        return
                    }

                    response.body()?.let {   // 성공처리
                        binding.searchEditText.setText(query)
                        movieAdapter.submitList(it.movies)   // 검색 결과 보여주기

                    }
                }

                override fun onFailure(call: Call<MovieDTO>, t: Throwable) {
                    Log.d("MainActivity", "FAIL!!")
                }
            })
    }

}