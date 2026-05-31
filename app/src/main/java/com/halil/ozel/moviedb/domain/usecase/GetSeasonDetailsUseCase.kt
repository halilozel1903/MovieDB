package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Season
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class GetSeasonDetailsUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int, seasonNumber: Int): Result<Season> =
        repository.getSeasonDetails(tvId, seasonNumber)
}
