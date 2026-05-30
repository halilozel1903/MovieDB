package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieCastUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<List<Cast>> =
        repository.getMovieCast(movieId)
}
