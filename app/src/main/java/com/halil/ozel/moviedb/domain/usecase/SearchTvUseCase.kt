package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.SearchRepository
import javax.inject.Inject

class SearchTvUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<TvSeries>> =
        repository.searchTv(query, page)
}
