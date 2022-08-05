package com.example.moviebrowser.service

import com.example.moviebrowser.model.MovieDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieService {
    @GET("/v1/search/movie.json")
    fun getMovieList(
        @Header("X-Naver-Client-Id") id: String,
        @Header("X-Naver-Client-Secret") secret: String,
        @Query("query") name: String
    ): Call<MovieDTO>
}