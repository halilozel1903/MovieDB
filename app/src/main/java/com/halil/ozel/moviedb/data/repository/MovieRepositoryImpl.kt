package com.halil.ozel.moviedb.data.repository

import com.halil.ozel.moviedb.BuildConfig
import com.halil.ozel.moviedb.data.remote.api.TMDbApiService
import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.Video
import com.halil.ozel.moviedb.domain.model.WatchProvider
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: TMDbApiService
) : MovieRepository {

    private val apiKey = BuildConfig.TMDB_API_KEY

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> = runCatching {
        apiService.getPopularMovies(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getNowPlayingMovies(page: Int): Result<List<Movie>> = runCatching {
        apiService.getNowPlaying(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getTopRatedMovies(page: Int): Result<List<Movie>> = runCatching {
        apiService.getTopRatedMovies(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getUpcomingMovies(page: Int): Result<List<Movie>> = runCatching {
        apiService.getUpcomingMovies(apiKey, page).results.map { it.toDomain() }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<Movie> = runCatching {
        apiService.getMovieDetail(movieId, apiKey).toDomain()
    }

    override suspend fun getMovieCast(movieId: Int): Result<List<Cast>> = runCatching {
        apiService.getMovieCredits(movieId, apiKey).cast.map { it.toDomain() }
    }

    override suspend fun getRecommendedMovies(movieId: Int): Result<List<Movie>> = runCatching {
        apiService.getMovieRecommendations(movieId, apiKey).results.map { it.toDomain() }
    }

    override suspend fun getMovieGenres(): Result<List<Genre>> = runCatching {
        apiService.getMovieGenres(apiKey).genres.map { it.toDomain() }
    }

    override suspend fun getMovieVideos(movieId: Int): Result<List<Video>> = runCatching {
        apiService.getMovieVideos(movieId, apiKey).results.map { it.toDomain() }
    }

    override suspend fun getMovieWatchProviders(movieId: Int): Result<List<WatchProvider>> = runCatching {
        val response = apiService.getMovieWatchProviders(movieId, apiKey)
        val countryData = response.results["TR"] ?: response.results["US"] ?: response.results.values.firstOrNull()
        countryData?.flatrate?.take(5)?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun discoverMoviesByGenre(genreId: Int, page: Int): Result<List<Movie>> = runCatching {
        apiService.discoverMoviesByGenre(apiKey, genreId, page).results.map { it.toDomain() }
    }
}
