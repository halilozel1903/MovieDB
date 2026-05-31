package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.model.Season
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.model.Video
import com.halil.ozel.moviedb.domain.model.WatchProvider

interface TvRepository {
    suspend fun getPopularTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getTopRatedTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getAiringTodayTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getOnTheAirTv(page: Int = 1): Result<List<TvSeries>>
    suspend fun getTvDetail(tvId: Int): Result<TvSeries>
    suspend fun getTvCast(tvId: Int): Result<List<Cast>>
    suspend fun getRecommendedTv(tvId: Int, page: Int = 1): Result<List<TvSeries>>
    suspend fun getTvGenres(): Result<List<Genre>>
    suspend fun getSeasonDetails(tvId: Int, seasonNumber: Int): Result<Season>
    suspend fun getTvVideos(tvId: Int): Result<List<Video>>
    suspend fun getTvWatchProviders(tvId: Int): Result<List<WatchProvider>>
    suspend fun discoverTvByGenre(genreId: Int, page: Int = 1): Result<List<TvSeries>>
    suspend fun getTvExternalIds(tvId: Int): Result<String?>
}
