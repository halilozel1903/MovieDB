package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class GetAiringTodayTvUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<TvSeries>> =
        repository.getAiringTodayTv(page)
}
