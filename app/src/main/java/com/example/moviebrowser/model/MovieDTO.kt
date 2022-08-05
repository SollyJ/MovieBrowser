package com.example.moviebrowser.model

import com.google.gson.annotations.SerializedName

data class MovieDTO(
    @SerializedName("items") val movies: List<Movie>
)
