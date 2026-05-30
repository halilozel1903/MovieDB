package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class GetTvCastUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<List<Cast>> =
        repository.getTvCast(tvId)
}
