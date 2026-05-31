package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> =
        repository.getMovieGenres()
}
