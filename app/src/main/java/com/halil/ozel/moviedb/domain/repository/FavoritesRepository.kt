package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<Movie>>
    suspend fun addFavorite(movie: Movie)
    suspend fun removeFavorite(movieId: Int)
    suspend fun isFavorite(movieId: Int): Boolean
    fun isFavoriteFlow(movieId: Int): Flow<Boolean>

    fun getTvFavorites(): Flow<List<TvSeries>>
    suspend fun addTvFavorite(tvSeries: TvSeries)
    suspend fun removeTvFavorite(tvId: Int)
    suspend fun isTvFavorite(tvId: Int): Boolean
    fun isTvFavoriteFlow(tvId: Int): Flow<Boolean>
}
