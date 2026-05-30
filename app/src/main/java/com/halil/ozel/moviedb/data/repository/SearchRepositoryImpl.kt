package com.halil.ozel.moviedb.data.repository

import com.halil.ozel.moviedb.BuildConfig
import com.halil.ozel.moviedb.data.remote.api.TMDbApiService
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: TMDbApiService
) : SearchRepository {

    private val apiKey = BuildConfig.TMDB_API_KEY

    override suspend fun searchMovies(query: String, page: Int): Result<List<Movie>> = runCatching {
        apiService.searchMovies(apiKey, query, page).results.map { it.toDomain() }
    }

    override suspend fun searchTv(query: String, page: Int): Result<List<TvSeries>> = runCatching {
        apiService.searchTv(apiKey, query, page).results.map { it.toDomain() }
    }
}
