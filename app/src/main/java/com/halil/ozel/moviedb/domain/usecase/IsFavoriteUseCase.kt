package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> = repository.isFavoriteFlow(movieId)
}
