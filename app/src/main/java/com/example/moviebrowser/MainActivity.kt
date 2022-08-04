package com.example.moviebrowser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.moviebrowser.Model.MovieDTO
import com.example.moviebrowser.Service.MovieService
import com.example.moviebrowser.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(MovieService::class.java)
        movieService.getMovieList(getString(R.string.clientId), getString(R.string.clientSecret), binding.searchEditText.toString())
            .enqueue(object: Callback<MovieDTO> {
                override fun onResponse(call: Call<MovieDTO>, response: Response<MovieDTO>) {
                    if(response.isSuccessful.not()) {   // 예외처리
                        Log.d("MainActivity", "FAIL")
                        return
                    }

                    // TODO 성공처리
                    response.body()?.let {
                        it.movies.forEach { movie ->
                            Log.d("MainActivity", movie.toString())
                        }
                    }
                }

                override fun onFailure(call: Call<MovieDTO>, t: Throwable) {
                    Log.d("MainActivity", "FAIL")
                }

            })
    }
}