package com.halil.ozel.moviedb.data.repository

import com.halil.ozel.moviedb.BuildConfig
import com.halil.ozel.moviedb.data.remote.api.TMDbApiService
import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.model.Season
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class TvRepositoryImpl @Inject constructor(
    private val apiService: TMDbApiService
) : TvRepository {

    private val apiKey = BuildConfig.TMDB_API_KEY

    override suspend fun getPopularTv(page: Int): Result<List<TvSeries>> = runCatching {
        apiService.getPopularTv(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getTopRatedTv(page: Int): Result<List<TvSeries>> = runCatching {
        apiService.getTopRatedTv(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getAiringTodayTv(page: Int): Result<List<TvSeries>> = runCatching {
        apiService.getAiringTodayTv(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getOnTheAirTv(page: Int): Result<List<TvSeries>> = runCatching {
        apiService.getOnTheAirTv(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getTvDetail(tvId: Int): Result<TvSeries> = runCatching {
        apiService.getTvDetail(tvId, apiKey).toDomain()
    }

    override suspend fun getTvCast(tvId: Int): Result<List<Cast>> = runCatching {
        apiService.getTvCredits(tvId, apiKey).cast.map { it.toDomain() }
    }

    override suspend fun getRecommendedTv(tvId: Int): Result<List<TvSeries>> = runCatching {
        apiService.getTvRecommendations(tvId, apiKey).results.map { it.toDomain() }
    }

    override suspend fun getTvGenres(): Result<List<Genre>> = runCatching {
        apiService.getTvGenres(apiKey).genres.map { it.toDomain() }
    }

    override suspend fun getSeasonDetails(tvId: Int, seasonNumber: Int): Result<Season> = runCatching {
        apiService.getSeasonDetails(tvId, seasonNumber, apiKey).toDomain()
    }
}
