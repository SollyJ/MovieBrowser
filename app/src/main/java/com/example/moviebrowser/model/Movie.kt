package com.example.moviebrowser.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("title") val title: String,
    @SerializedName("pubDate") val releaseDate: String,
    @SerializedName("userRating") val rating: String,
    @SerializedName("image") val imageUrl: String
)
