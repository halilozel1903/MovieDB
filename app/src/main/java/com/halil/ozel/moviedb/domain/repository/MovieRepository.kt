package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.Video
import com.halil.ozel.moviedb.domain.model.WatchProvider

interface MovieRepository {
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getUpcomingMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Result<Movie>
    suspend fun getMovieCast(movieId: Int): Result<List<Cast>>
    suspend fun getRecommendedMovies(movieId: Int, page: Int = 1): Result<List<Movie>>
    suspend fun getMovieGenres(): Result<List<Genre>>
    suspend fun getMovieVideos(movieId: Int): Result<List<Video>>
    suspend fun getMovieWatchProviders(movieId: Int): Result<List<WatchProvider>>
    suspend fun discoverMoviesByGenre(genreId: Int, page: Int = 1): Result<List<Movie>>
    suspend fun getTrendingMovies(): Result<List<Movie>>
}
