package com.example.moviebrowser.Model

import com.google.gson.annotations.SerializedName

data class MovieDTO(
    @SerializedName("items") val movies: List<Movie>
)
