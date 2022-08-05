package com.example.moviebrowser.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviebrowser.model.Movie
import com.example.moviebrowser.databinding.ItemMovieBinding

class MovieAdapter(): ListAdapter<Movie, MovieAdapter.MovieItemViewHolder>(diffUtil) {
    inner class MovieItemViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        fun bind(movie: Movie) {
            binding.titleTextView.text = "제목: " + Html.fromHtml(movie.title, Html.FROM_HTML_MODE_LEGACY).toString()
            binding.releaseDateTextView.text = "출시: " + movie.releaseDate
            binding.ratingTextView.text = "평점: " + movie.rating

            Glide
                .with(binding.posterImageView.context)
                .load(movie.imageUrl)
                .into(binding.posterImageView)

            // todo 클릭이벤트
        }
    }

    // 뷰홀더를 생성 해주는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        return MovieItemViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // 뷰홀더에 레이아웃 파일을 바인드 해주는 함수
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}