package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.repository.FavoritesRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(movie: Movie) {
        val isFav = repository.isFavorite(movie.id)
        if (isFav) {
            repository.removeFavorite(movie.id)
        } else {
            repository.addFavorite(movie)
        }
    }
}
