package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<Movie>> =
        repository.getPopularMovies(page)
}
