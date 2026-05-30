package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries

interface SearchRepository {
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>>
    suspend fun searchTv(query: String, page: Int = 1): Result<List<TvSeries>>
}
