package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class GetTvGenresUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> =
        repository.getTvGenres()
}
