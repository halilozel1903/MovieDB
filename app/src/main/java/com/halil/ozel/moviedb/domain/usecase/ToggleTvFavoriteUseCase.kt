package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.FavoritesRepository
import javax.inject.Inject

class ToggleTvFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(tvSeries: TvSeries) {
        if (repository.isTvFavorite(tvSeries.id)) repository.removeTvFavorite(tvSeries.id)
        else repository.addTvFavorite(tvSeries)
    }
}
