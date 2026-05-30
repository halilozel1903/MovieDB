package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Movie> =
        repository.getMovieDetail(movieId)
}
