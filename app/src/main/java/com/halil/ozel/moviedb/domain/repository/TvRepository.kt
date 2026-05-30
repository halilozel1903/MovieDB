package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.model.Season
import com.halil.ozel.moviedb.domain.model.TvSeries

interface TvRepository {
    suspend fun getPopularTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getTopRatedTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getAiringTodayTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getOnTheAirTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getTvDetail(tvId: Int): Result<TvSeries>
    suspend fun getTvCast(tvId: Int): Result<List<Cast>>
    suspend fun getRecommendedTv(tvId: Int): Result<List<TvSeries>>
    suspend fun getTvGenres(): Result<List<Genre>>
    suspend fun getSeasonDetails(tvId: Int, seasonNumber: Int): Result<Season>
}
