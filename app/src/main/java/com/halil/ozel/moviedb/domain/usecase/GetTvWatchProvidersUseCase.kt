package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.WatchProvider
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class GetTvWatchProvidersUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<List<WatchProvider>> =
        repository.getTvWatchProviders(tvId)
}
