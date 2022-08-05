package com.example.moviebrowser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviebrowser.Model.MovieDTO
import com.example.moviebrowser.Service.MovieService
import com.example.moviebrowser.adapter.MovieAdapter
import com.example.moviebrowser.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieService: MovieService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSearchButton()
        initMovieListRecyclerView()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        movieService = retrofit.create(MovieService::class.java)

    }

    private fun initSearchButton() {
        binding.searchButton.setOnClickListener() {
            search(binding.searchEditText.text.toString())
        }
    }

    // 검색 결과 보여주기 & 검색 기록 저장
    private fun search(query: String) {
        movieService.getMovieList(getString(R.string.clientId), getString(R.string.clientSecret), query)
            .enqueue(object: Callback<MovieDTO> {
                override fun onResponse(call: Call<MovieDTO>, response: Response<MovieDTO>) {
                    // todo 검색 기록 저장 함수 구현

                    if(response.isSuccessful.not()) {   // 예외처리
                        Log.d("MainActivity", "RESPONSE FAIL")
                        return
                    }

                    // 성공처리
                    response.body()?.let {
                        movieAdapter.submitList(it.movies)
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
}