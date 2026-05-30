package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<Movie>>
    suspend fun addFavorite(movie: Movie)
    suspend fun removeFavorite(movieId: Int)
    suspend fun isFavorite(movieId: Int): Boolean
    fun isFavoriteFlow(movieId: Int): Flow<Boolean>
}
