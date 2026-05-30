package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class GetRecommendedTvUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<List<TvSeries>> =
        repository.getRecommendedTv(tvId)
}
