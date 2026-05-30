package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<Movie>> = repository.getFavorites()
}
